import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface ChatInterface extends Remote {

    public String getName() throws RemoteException;

    public void setMsg(String msg) throws RemoteException;

    public String getMsg() throws RemoteException;

    public void setClients(String clientId, ChatInterface c) throws RemoteException;

    public HashMap<String, ChatInterface> getClients() throws RemoteException;

    public ChatInterface getClientById(String clientId) throws RemoteException;

    public String printMsg(String msg) throws RemoteException;


    public void setTextArea(JTextArea textArea) throws RemoteException;

    public JTextArea getTextArea() throws RemoteException;

    public void appendTextArea(JTextArea textArea, String msg) throws RemoteException;

    public void setIsNewMessage(boolean i) throws RemoteException;

    public boolean getIsNewMessage() throws RemoteException;

}
