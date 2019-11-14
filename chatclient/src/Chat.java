import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Chat extends UnicastRemoteObject implements ChatInterface {
    private String name;
    public ChatInterface client;
    public List<ChatInterface> clients = new ArrayList<ChatInterface>();
    private HashMap<String, ChatInterface> clientsMap = new HashMap<String, ChatInterface>();
    private HashMap<String, ChatInterface> clientsMapToValidate = new HashMap<String, ChatInterface>();
    public String message;
    public boolean isNewMessage;
    public String clientId;
    private String password;
    private boolean isValidate;
    private String friendUsername;
    private String username;
    private String friends;

    public Chat(String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }

    @Override
    public void setMsg(String msg) throws RemoteException {
        this.message = msg;
    }

    @Override
    public String getMsg() throws RemoteException {
        return this.message;
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

    @Override
    public void printMsg(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public void setIsNewMessage(boolean i) throws RemoteException {
        this.isNewMessage = i;
    }

    @Override
    public boolean getIsNewMessage() throws RemoteException {
        return this.isNewMessage;
    }

    @Override
    public void setClientId(String clientId) throws RemoteException {
        this.clientId = clientId;
    }

    @Override
    public String getClientId() throws RemoteException {
        return this.clientId;
    }

    @Override
    public void setClientPassword(String password) throws RemoteException {
        this.password = password;
    }

    @Override
    public String getClientPassword() throws RemoteException {
        return this.password;
    }

    @Override
    public void setClientsToValidate(String clientId, ChatInterface c) throws RemoteException {
        this.clientsMapToValidate.put(clientId, c);

    }

    @Override
    public HashMap<String, ChatInterface> getClientsToValidate() throws RemoteException {
        return this.clientsMapToValidate;
    }

    @Override
    public void removeClientFromHashMap(String clientId) throws RemoteException {
        this.clientsMapToValidate.remove(clientId);
    }

    @Override
    public void setValidate(boolean isValidate) throws RemoteException {
        this.isValidate = isValidate;
    }

    @Override
    public boolean getValidate() throws RemoteException {
        return this.isValidate;
    }

    @Override
    public void setFriendToAdd(String user, String friendUsername) throws RemoteException {
        this.friendUsername = friendUsername;
        this.username = user;
    }

    @Override
    public String getFriendToAdd() throws RemoteException {
        if (this.username == null && this.friendUsername == null) {
            return null;
        } return this.username + ";" + this.friendUsername;
    }

    @Override
    public void setFriends(String friends) throws RemoteException {
        this.friends = friends;
    }

    @Override
    public String getFriends() throws RemoteException {
        return this.friends;
    }
}
