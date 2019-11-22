import javax.swing.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ChatClientGui {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        String ip;
        String hostName = "127.0.0.1";
        // GET CLIENT IP ADDRESS
        // ethernet duoc uu tien hon wifi
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

        // SET SECURITY POLICY AND SET RMI CLIENT IP ADDR
        if (System.getSecurityManager() == null) {
            System.setProperty("java.security.policy", "client.policy");
            System.setSecurityManager(new SecurityManager());
            System.setProperty("java.rmi.server.hostname", hostName);
        }

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginRegisterForm loginRegisterForm = new LoginRegisterForm();
                loginRegisterForm.setVisible(true);
            }
        });
    }
}
