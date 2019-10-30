import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class SecondChatClient {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setProperty("java.security.policy", "client.policy");
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Scanner s = new Scanner(System.in);

            String name = "ducanhchatapp";
            String clientName = "anonymous_client";
            String clientId = "anon1";
            Registry myReg = LocateRegistry.getRegistry("172.17.0.2", 6000);
            ChatInterface client = new Chat(clientName);
            ChatInterface server = (ChatInterface) myReg.lookup(name);
            server.setClients(clientId, client);
            String msg = "[" + client.getName() + "] " + "is connected";
            server.printMsg(msg);
            System.out.println("[System] Chat Remote Object is ready:");

            while (true) {
                msg = s.nextLine().trim();
                msg = "[" + client.getName() + "] " + msg;
                server.printMsg(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}