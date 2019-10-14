import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Chat extends UnicastRemoteObject implements ChatInterface {
    public String name;
    public ChatInterface client;
    public List<ChatInterface> clients = new ArrayList<ChatInterface>();

    public Chat(String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }

    @Override
    public void send(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public void setClients(ChatInterface c) throws RemoteException {
        this.clients.add(c);
    }

    @Override
    public List<ChatInterface> getClient() throws RemoteException {
        return this.clients;
    }
}
