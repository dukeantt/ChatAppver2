import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface ChatInterface extends Remote {

    public String getName() throws RemoteException;

    public void send(String msg) throws RemoteException;

    public void setClients(String clientId, ChatInterface c) throws RemoteException;

    public HashMap<String, ChatInterface> getClients() throws RemoteException;

    public ChatInterface getClientById(String clientId) throws RemoteException;

}
