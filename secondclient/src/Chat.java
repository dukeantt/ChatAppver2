import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat extends UnicastRemoteObject implements ChatInterface {
    public String name;
    public ChatInterface client;
    public List<ChatInterface> clients = new ArrayList<ChatInterface>();
    public HashMap<String, ChatInterface> clientsMap = new HashMap<String, ChatInterface>();

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
    public void setClients(String clientId, ChatInterface c) throws RemoteException {
        this.clientsMap.put(clientId, c);
    }

    @Override
    public HashMap<String, ChatInterface> getClients() throws RemoteException {
        return this.clientsMap;
    }

    @Override
    public ChatInterface getClientById(String clientId) throws RemoteException {
        return this.clientsMap.get(clientId);
    }
}
