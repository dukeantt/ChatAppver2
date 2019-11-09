import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class LoginRegisterForm extends JFrame {
    private JPanel panelMain;
    private JButton loginRegisterButton;
    private JTextField userTextField;
    private JPasswordField passwordTextField;
    private ChatInterface clientLocal;
    private ChatInterface serverLocal;

    public LoginRegisterForm() {
        add(panelMain);
        getRootPane().setDefaultButton(loginRegisterButton);
        setTitle("Login/Register");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loginRegisterButton.addActionListener(validateUser);
    }

    ActionListener validateUser = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String user = "";
                    String password = "";
                    ChatInterface client = null;
                    ChatInterface server = null;
                    boolean isUserOrPasswordEmpty = true;

                    //SERVER INFORMATION
                    String serverName = "RMIchatapp";
                    String serverIp = "172.17.0.2";
                    int port = 6000;

                    //CLIENT INFORMATION
                    user = userTextField.getText();
                    password = passwordTextField.getText();
                    if (user.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(panelMain, "Check your username or password again pal   ");
                    } else {
                        isUserOrPasswordEmpty = false;
                    }
                    byte[] array = new byte[6]; // length is bounded by 7
                    new Random().nextBytes(array);
                    String randomString = new String(array, Charset.forName("UTF-8"));
                    String clientId = user + "_client_" + randomString;


                    if (!isUserOrPasswordEmpty) {
                        //CONNECT TO SERVER
                        try {
                            Registry myReg = LocateRegistry.getRegistry(serverIp, port);
                            client = new Chat(user); // CREATE CLIENT
                            client.setClientId(clientId); //SET CLIENT ID TO GET IT IN CHATFORM.JAVA
                            server = (ChatInterface) myReg.lookup(serverName); // GET SERVER

                            server.setClientsToValidate(clientId, client);
                            client.setClientId(clientId);
                            client.setClientPassword(password);

//                            server.setClients(clientId, client); //SET CLIENT ON SERVER AND SERVER WILL ADD CLIENT INFO TO DATABASE
//
//                            String msg = "[" + client.getName() + "] " + "is connected";
//                            server.printMsg(msg);
//                            System.out.println("[System] Chat Remote Object is ready:");
                        } catch (RemoteException | NotBoundException e) {
                            e.printStackTrace();
                        }
                        setClientLocal(client);
                        setServerLocal(server);


                        Runnable validateClient = new Runnable() {
                            @Override
                            public void run() {
                                ChatInterface server = getServerLocal();
                                ChatInterface client = getClientLocal();
                                while (true) {
                                    try {
                                        boolean isValidate = server.getValidate();
                                        if (isValidate) {
                                            try {
                                                server.setClients(clientId, client); //SET CLIENT ON SERVER AND SERVER WILL ADD CLIENT INFO TO DATABASE

                                                String msg = "[" + client.getName() + "] " + "is connected";
                                                server.printMsg(msg);
                                                System.out.println("[System] Chat Remote Object is ready:");
                                                server.removeClientFromHashMap(clientId);
                                                setVisible(false);
                                                openChatForm();
                                                break;
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                                JOptionPane.showMessageDialog(panelMain, "Failed to connect to server");
                                            }
                                        }else{
                                            JOptionPane.showMessageDialog(panelMain, "The username already exists and the password is incorrect ");
                                            break;
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        };
                        new Thread(validateClient).start();

                        //OPEN CHATFORM
//                        try {
//                            setVisible(false);
//                            openChatForm();
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(panelMain, "Failed to connect to server");
//                        }
                    }
                }
            });
        }
    };

    private void openChatForm() throws RemoteException {
        ChatInterface client = getClientLocal();
        ChatInterface server = getServerLocal();
        ChatForm chatForm = new ChatForm(client, server);
        chatForm.setVisible(true);
    }

    private void setClientLocal(ChatInterface client) {
        this.clientLocal = client;
    }

    private ChatInterface getClientLocal() {
        return this.clientLocal;
    }

    private void setServerLocal(ChatInterface server) {
        this.serverLocal = server;
    }

    private ChatInterface getServerLocal() {
        return this.serverLocal;
    }
}
