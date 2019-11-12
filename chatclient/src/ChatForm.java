import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatForm extends JFrame {
    private JPanel panelMain;
    private JTextArea outputTextArea;
    private JTextField inputTextField;
    private JButton sendButton;
    private JList friendList;
    private JButton addFriendButton;
    private String clientName;
    private String serverName;
    private String clientId;


    public ChatForm(ChatInterface client, ChatInterface server) throws RemoteException {
        add(panelMain);
        getRootPane().setDefaultButton(sendButton);
        outputTextArea.setEditable(false);
        setTitle("Simple chat app");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //ADD FRIEND
        addFriendButton.addActionListener(openFindFriendWindow);

        // SEND MESSAGE TO SERVER
        clientName = client.getName();
        serverName = server.getName();
        clientId = client.getClientId();
        sendButton.addActionListener(sendMessage);

        // THREAD TO RECEIVE MESSAGE
        Runnable r = new Runnable() {
            @Override
            public void run() {
                String messageOfServer = " ";
                boolean isNewMessage = false;
                String serverName = null;
                while (true) {
                    try {
                        messageOfServer = client.getMsg();
                        isNewMessage = client.getIsNewMessage();
                        serverName = server.getName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (messageOfServer != null && isNewMessage) {
                        String msg = "[" + serverName + "]: " + messageOfServer;
                        outputTextArea.append("\n" + msg);
                        try {
                            client.setIsNewMessage(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        new Thread(r).start();
    }

    private ActionListener sendMessage = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String serverIp = "172.17.0.2";
            int port = 6000;
            String messageInTextField = inputTextField.getText();
            try {
                Registry myReg = LocateRegistry.getRegistry(serverIp, port);
                ChatInterface server = (ChatInterface) myReg.lookup(serverName);
                ChatInterface client = server.getClients().get(clientId);
                server.printMsg("[" + client.getName() + "] " + messageInTextField);
                server.setMsg(messageInTextField);
                inputTextField.setText("");
                outputTextArea.append("\n" + "[" + client.getName() + "]: " + messageInTextField);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    };

    private ActionListener openFindFriendWindow = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FindFriend findFriend = new FindFriend();
                    findFriend.setVisible(true);
                }
            });
        }
    };
}
