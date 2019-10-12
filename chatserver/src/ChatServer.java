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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
