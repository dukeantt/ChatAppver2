import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatInterface extends Remote {

    public String getName() throws RemoteException;

    public void send(String msg) throws RemoteException;

    public void setClients(ChatInterface c) throws RemoteException;

    public List<ChatInterface> getClient() throws RemoteException;
}
