import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SuppressWarnings("InfiniteLoopStatement")

public class ChatForm extends JFrame {
    private JPanel panelMain;
    private JTextArea outputTextArea;
    private JTextField inputTextField;
    private JButton sendButton;
    private JList<String> friendList;
    private JButton addFriendButton;
    private JButton createGroupButton;
    private JScrollPane scrollPane;
    //    private JScrollBar vertical;
    private String clientName;
    private String serverName;
    private String clientId;
    private String friendId;


    public ChatForm(ChatInterface client, ChatInterface server) throws RemoteException {
        add(panelMain);
        getRootPane().setDefaultButton(sendButton);
        outputTextArea.setEditable(false);
//        vertical = scrollPane.getVerticalScrollBar();

        setTitle("Simple chat app - " + client.getName());
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // HANDLE QUIT EVENT
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    String clientId = client.getClientId();
                    server.removeClients(clientId);
                    String message = " Really Quit ? ";
                    String title = "Quit ???";
                    int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    } else {
                        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                        server.setClients(clientId, client);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //CREATE GROUP
        createGroupButton.addActionListener(openCreateGroupWindow);
        //ADD FRIEND
        addFriendButton.addActionListener(openFindFriendWindow);

        // CALL FUNCTION TO SEND MESSAGE TO SERVER
        clientName = client.getName();
        serverName = server.getName();
        clientId = client.getClientId();
        sendButton.addActionListener(sendMessage);

        // THREAD TO RECEIVE MESSAGE
        Runnable r = new Runnable() {
            @Override
            public void run() {
                String messageOfServer = " ";
                String[] directMessageFromFriend = null;
                String messageFromFriend = null;
                String receiverId = null;
                String senderId = null;
                boolean isNewMessage = false;
                boolean isNewMessageFromFriend = false;
                boolean isCorrectSelectedFriend = false;
                String serverName = null;
                while (true) {
                    try {
                        messageOfServer = client.getMsg();
                        if (client.getDirectMessage() != null) {
                            directMessageFromFriend = client.getDirectMessage().split(";");
                            senderId = directMessageFromFriend[0];
                            receiverId = directMessageFromFriend[1];
                            messageFromFriend = directMessageFromFriend[2];
                        }

                        isNewMessage = client.getIsNewMessage();
                        isNewMessageFromFriend = client.getIsNewMessageFromFriend();
                        serverName = server.getName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        break;
                    }
                    //RECEIVE MESSAGE FROM FRIEND


                    if (messageFromFriend != null && isNewMessageFromFriend) {
                        if (receiverId != null) {
                            isCorrectSelectedFriend = false;
                            if (receiverId.contains("group:")) {
                                if (receiverId.equals(friendId)) {
                                    System.out.println("group");
                                    isCorrectSelectedFriend = true;
                                }
                            } else {
                                if (senderId.equals(friendId)) {
                                    System.out.println("friend");
                                    isCorrectSelectedFriend = true;
                                }
                            }
                        }
                        if (isCorrectSelectedFriend) {
//                            outputTextArea.append("\n" + messageFromFriend);
                            System.out.println("message from friend" + messageFromFriend);
                            try {

                                client.setIsNewMessageFromFriend(false);
                                messageFromFriend = null;
//                            client.setDirectMessage(null, null, null);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                    //RECEIVE MESSAGE FROM SERVER
                    if (messageOfServer != null && isNewMessage) {
                        String msg = "[" + serverName + "]: " + messageOfServer;
                        outputTextArea.append("\n" + msg);
                        System.out.println("message from server: " + messageOfServer);
                        try {
                            client.setIsNewMessage(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        };
        new Thread(r).start();

        // THREAD TO GET FRIEND LIST AND UPDATE FRIEND LIST
        Runnable updateFriendList = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (client.getIsNeedUpdateFriendList()) {
                            if (client.getFriends() != null) {
                                String allFriends = client.getFriends();
                                String[] friends = allFriends.split(";");
                                DefaultListModel<String> demoList = new DefaultListModel<String>();
                                for (int i = 0; i < friends.length; i++) {
                                    demoList.addElement(friends[i]);
                                }
                                friendList.setModel(demoList);
                                client.setIsNeedUpdateFriendList(false);
                            }
                        }
                    } catch (RemoteException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(updateFriendList).start();

        // THREAD TO UPDATE OUTPUT TEXT FIELD WHEN SELECT FRIEND IN FRIEND LIST
        Runnable updateOuputTextField = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    outputTextArea.setCaretPosition(outputTextArea.getDocument().getLength());
                    try {
                        Thread.sleep(1000);
                        if (client.getIsNeedUpdateOutputText() == 2) {
                            if (client.getUpdateOutputText() != null) {
                                String[] message = client.getUpdateOutputText().split(";");
                                for (int i = 0; i < message.length; i++) {
                                    System.out.println("update output text field");
                                    outputTextArea.append("\n" + message[i]);
                                }
                                client.setIsNeedUpdateOutputText(0);
                            }
                        }
                    } catch (RemoteException | InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        new Thread(updateOuputTextField).start();

        //SELECT FRIEND IN FRIEND LIST, GET FRIEND ID WHEN SELECTED
        friendList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                try {
                    client.setIsNeedUpdateOutputText(1);
                    client.setUpdateOutputText("");
                    friendId = friendList.getSelectedValue();
                    client.setSelectedFriendId(friendId);
                    outputTextArea.setText("");

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // SEND MESSAGE TO SERVER AND DIRECT MESSAGE
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

                // SEND DIRECT MESSAGE TO FRIEND ID
                if (friendId != null) {
                    client.setDirectMessage(client.getName(), friendId, messageInTextField);
                    client.setIsNewMessage(true);
                    server.setIsNewMessage(true);
                }
                server.setMsg(messageInTextField);
                inputTextField.setText("");
                System.out.println("send button");
                client.setIsNeedUpdateOutputText(0);
                outputTextArea.append("\n" + "[" + client.getName() + "]: " + messageInTextField);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
//            vertical.setValue(vertical.getMaximum());
            outputTextArea.setCaretPosition(outputTextArea.getDocument().getLength());

        }
    };

    // OPEN ADD FRIEND WINDOW
    private ActionListener openFindFriendWindow = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ChatInterface server = null;
                    ChatInterface client = null;
                    String serverIp = "172.17.0.2";
                    int port = 6000;
                    Registry myReg = null;
                    try {
                        myReg = LocateRegistry.getRegistry(serverIp, port);
                        server = (ChatInterface) myReg.lookup(serverName);
                        client = server.getClients().get(clientId);
                    } catch (RemoteException | NotBoundException e) {
                        e.printStackTrace();
                    }
                    FindFriend findFriend = new FindFriend(client, server);
                    findFriend.setVisible(true);
                }
            });
        }
    };

    //OPEN CREATE GROUP WINDOW
    private ActionListener openCreateGroupWindow = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ChatInterface server = null;
                    ChatInterface client = null;
                    String serverIp = "172.17.0.2";
                    int port = 6000;
                    Registry myReg = null;
                    try {
                        myReg = LocateRegistry.getRegistry(serverIp, port);
                        server = (ChatInterface) myReg.lookup(serverName);
                        client = server.getClients().get(clientId);
                    } catch (RemoteException | NotBoundException e) {
                        e.printStackTrace();
                    }
                    CreateGroup createGroup = new CreateGroup(client, server);
                    createGroup.setVisible(true);
                }
            });
        }
    };
}
