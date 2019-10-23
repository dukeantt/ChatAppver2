import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;

@SuppressWarnings("InfiniteLoopStatement")

public class testform extends OutputStream {
    private JTextField textField1;
    private JTextArea textArea1;
    private JButton sendButton;
    private JList list1;
    private JPanel panelMain;

    public testform() throws RemoteException, NotBoundException {
        textArea1.setEditable(false);
        sendButton.addActionListener(sendMessageToOthers);
        String name = "ducanhchatapp";
        String clientName = "ducanhclient";
        String clientId = "ducanh";
        Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 6000);
        ChatInterface server = (ChatInterface) myReg.lookup(name);
        ChatInterface client = server.getClients().get(clientId);
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
                        String msg  = "[" + serverName + "]: " + messageOfServer;
                        textArea1.append("\n" + msg);
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

    @Override
    public void write(int i) throws IOException {

    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        String ip;
        String hostName = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (((Enumeration) interfaces).hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet6Address) continue;
                    ip = addr.getHostAddress();
                    if (ip != null && !ip.isEmpty()) {
                        if (iface.getDisplayName().contains("en")) {
//                            System.out.println(iface.getDisplayName() + "ethernet" + " " + ip);
                            hostName = ip;
                            break;
                        } else if (iface.getDisplayName().contains("wl")) {
//                            System.out.println(iface.getDisplayName() + "wifi" + " " + ip);
                            hostName = ip;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        if (System.getSecurityManager() == null) {
            System.setProperty("java.security.policy", "client.policy");
            System.setSecurityManager(new SecurityManager());
            System.setProperty("java.rmi.server.hostname", hostName);
        }

        try {
            String name = "ducanhchatapp";
            String clientName = "ducanhclient";
            String clientId = "ducanh";
            Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 6000);
            ChatInterface client = new Chat(clientName);
            ChatInterface server = (ChatInterface) myReg.lookup(name);
            server.setClients(clientId, client);

            String msg = "[" + client.getName() + "] " + "is connected";
            server.printMsg(msg);
            System.out.println("[System] Chat Remote Object is ready:");

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("test");
        frame.setPreferredSize(new Dimension(640, 480));
        frame.setContentPane(new testform().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    ActionListener sendMessageToOthers = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                String messageInTextField = textField1.getText();
                String name = "ducanhchatapp";
                String clientName = "ducanhclient";
                String clientId = "ducanh";
                Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 6000);
                ChatInterface server = (ChatInterface) myReg.lookup(name);
                ChatInterface client = server.getClients().get(clientId);

                server.printMsg("[" + client.getName() + "] " + messageInTextField);
                server.setMsg(messageInTextField);
                textField1.setText("");
                textArea1.append("\n" + "[" + client.getName() + "]: " + messageInTextField);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
