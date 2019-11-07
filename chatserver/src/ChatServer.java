import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            String name = "RMIchatapp";
            int port = 6000;
            ChatInterface server = new Chat("RMIchatapp");
            ChatInterface stub = (ChatInterface) UnicastRemoteObject.exportObject(server, port);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, stub);
            System.out.println("Server is ready");

            Scanner s = new Scanner(System.in);
            while (true) {
                String msg = s.nextLine().trim();
                if (server.getClients() != null) {
                    HashMap<String, ChatInterface> clients = server.getClients();
                    for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                        ChatInterface client = clientMap.getValue();
//                        msg = "[" + server.getName() + "] " + msg;
                        client.printMsg("[" + server.getName() + "] " + msg);
                        client.setMsg(msg);
                        client.setIsNewMessage(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
