import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat implements ChatInterface {
    public String name;
    public ChatInterface client;
    public List<ChatInterface> clients = new ArrayList<ChatInterface>();
    public HashMap<String, ChatInterface> clientsMap = new HashMap<String, ChatInterface>();
    public String message;
    public boolean isNewMessage;
    public String clientId;

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
}
