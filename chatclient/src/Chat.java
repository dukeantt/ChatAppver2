import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Chat extends UnicastRemoteObject implements ChatInterface {
    public String name;
    public ChatInterface client;

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
    public void setClient(ChatInterface c) throws RemoteException {
        this.client = c;
    }

    @Override
    public ChatInterface getClient() throws RemoteException {
        return this.client;
    }
}
