import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Scanner;

public class ChatClient {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
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
            Scanner s = new Scanner(System.in);
            String name = "ducanhchatapp";
            String clientName = "ducanhclient";
            String clientId = "ducanh";
            Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 6000);
            ChatInterface client = new Chat(clientName);
            ChatInterface server = (ChatInterface) myReg.lookup(name);
            server.setClients(clientId, client);
            String msg = "[" + client.getName() + "] " + "is connected";
            server.setMsg(msg);
            System.out.println("[System] Chat Remote Object is ready:");
            ChatInterface clientObject = new Chat("anon");
            while (true) {
                msg = s.nextLine().trim();
                msg = "[" + client.getName() + "] " + msg;
                server.printMsg(msg);
                server.setMsg(msg);
                System.out.println(client.getMsg());
//                if (client.getMsg() != null && !client.getMsg().isEmpty()) {
//                    System.out.println(client.getMsg());
//                }
//                server.getClientById("anon1").send(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}