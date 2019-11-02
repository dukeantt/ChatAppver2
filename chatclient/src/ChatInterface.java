import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ChatInterface extends Remote {

    public String getName() throws RemoteException;

    public void setMsg(String msg) throws RemoteException;

    public String getMsg() throws RemoteException;

    public void printMsg(String msg) throws RemoteException;

    public void setClients(String clientId, ChatInterface c) throws RemoteException;

    public HashMap<String, ChatInterface> getClients() throws RemoteException;

    public ChatInterface getClientById(String clientId) throws RemoteException;

    public void setIsNewMessage(boolean i) throws RemoteException;

    public boolean getIsNewMessage() throws RemoteException;

    public void setClientId(String clientId) throws RemoteException;

    public String getClientId() throws RemoteException;

}
