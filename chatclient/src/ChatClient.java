import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ChatClient {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setProperty("java.security.policy", "client.policy");
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Scanner s = new Scanner(System.in);
//            Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 8080);
//            String name = s.nextLine().trim();
//            ChatInterface client = new Chat(name);
//            ChatInterface server = (ChatInterface) myReg.lookup("dachat");
//            String msg = "[" + client.getName() + "] " + "is connected";
//            server.send(msg);
//            System.out.println("[System] Chat Remote Object is ready:");
//            server.setClient(client);

            String name = "ducanhchatapp";
            String clientName = "ducanhclient";
            Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 6000);
            ChatInterface client = new Chat(clientName);
            ChatInterface server = (ChatInterface) myReg.lookup(name);
            String msg = "[" + client.getName() + "] " + "is connected";
            server.send(msg);
            System.out.println("[System] Chat Remote Object is ready:");
//            server.setClient(client);

            while (true) {
                msg = s.nextLine().trim();
                msg = "[" + client.getName() + "] " + msg;
                server.send(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}