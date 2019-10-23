import javax.swing.*;
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
    public String message;
    public JTextArea textArea;
    public boolean isNewMessage;

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
    public void setTextArea(JTextArea textArea) throws RemoteException {
        this.textArea = textArea;

    }

    @Override
    public JTextArea getTextArea() throws RemoteException {
        return this.textArea;
    }

    @Override
    public void appendTextArea(JTextArea textArea, String msg) throws RemoteException {
        textArea.append(msg + "\n");
    }

    @Override
    public String printMsg(String msg) throws RemoteException {
        System.out.println(msg);
        return msg;
    }

    @Override
    public void setIsNewMessage(boolean i) throws RemoteException {
        this.isNewMessage = i;
    }

    @Override
    public boolean getIsNewMessage() throws RemoteException {
        return this.isNewMessage;
    }
}
