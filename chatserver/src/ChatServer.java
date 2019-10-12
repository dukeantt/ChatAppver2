import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
//            System.setProperty("java.security.policy","security.policy");
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "ducanhchatapp";
            int port = 6000;
            ChatInterface server = new Chat("ducanhchatapp");
            ChatInterface stub = (ChatInterface) UnicastRemoteObject.exportObject(server, port);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, stub);
            System.out.println("Server is ready");
//
//            Registry reg = LocateRegistry.createRegistry(8080);
//            Scanner s = new Scanner(System.in);
//            System.out.println("Enter name");
//            String serverName = s.nextLine().trim();
//            Chat server = new Chat("ducanh");
//            reg.rebind("dachat", server);
//            System.setProperty("java.security.policy","file:./security.policy");
////            Chat engine = new Chat("ducanh");
////            Chat stub = (Chat) UnicastRemoteObject.exportObject(engine, 0);
////            reg.rebind("dachat", stub);
//            System.out.println("Server is ready");
//
//            while (true) {
//                String msg = s.nextLine().trim();
//                if (server.getClient() != null) {
//                    ChatInterface client = server.getClient();
//                    msg = "[" + server.getName() + "] " + msg;
//                    client.send(msg);
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
