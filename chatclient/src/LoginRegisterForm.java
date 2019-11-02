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

    public LoginRegisterForm() {
        add(panelMain);
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
                    Boolean isUserOrPasswordEmpty = true;

                    //SERVER INFORMATION
                    String serverName = "ducanhchatapp";
                    String serverIp = "172.17.0.2";
                    int port = 6000;

                    //CLIENT INFORMATION
                    user = userTextField.getText();
                    password = passwordTextField.getText();
                    if (user.isEmpty() || user == null || password.isEmpty() || password == null) {
                        JOptionPane.showMessageDialog(panelMain, "Check your username or password again pal   ");
                    }else{
                        isUserOrPasswordEmpty = false;
                    }
                    byte[] array = new byte[6]; // length is bounded by 7
                    new Random().nextBytes(array);
                    String randomString = new String(array, Charset.forName("UTF-8"));
                    String clientId = user + "_client_" + randomString;
                    System.out.println(user);
                    System.out.println(password);

                    if (!isUserOrPasswordEmpty) {
                    //CONNECT TO SERVER
                    try {
                        Registry myReg = LocateRegistry.getRegistry(serverIp, port);
                        client = new Chat(user); // CREATE CLIENT
                        client.setClientId(clientId); //SET CLIENT ID TO GET IT IN CHATFORM.JAVA
                        server = (ChatInterface) myReg.lookup(serverName); // GET SERVER
                        server.setClients(clientId, client); //SET CLIENT ON SERVER AND SERVER WILL ADD CLIENT INFO TO DATABASE

                        String msg = "[" + client.getName() + "] " + "is connected";
                        server.printMsg(msg);
                        System.out.println("[System] Chat Remote Object is ready:");
                    } catch (RemoteException | NotBoundException e) {
                        e.printStackTrace();
                    }
                        if (client != null) {
                            JOptionPane.showMessageDialog(panelMain, "Cool!!!");
                            setVisible(false);
                            ChatForm chatForm = null;
                            try {
                                assert server != null;
                                chatForm = new ChatForm(client,server);
                                chatForm.setVisible(true);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }else {
                            JOptionPane.showMessageDialog(panelMain, "Failed to connect to server");
                        }
                    }
                }
            });
        }
    };
}
