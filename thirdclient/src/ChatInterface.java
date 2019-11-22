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

    public void removeClients(String clientId) throws RemoteException;

    public ChatInterface getClientById(String clientId) throws RemoteException;

    public void setIsNewMessage(boolean i) throws RemoteException;

    public boolean getIsNewMessage() throws RemoteException;

    public void setIsNewMessageFromFriend(boolean i) throws RemoteException;

    public boolean getIsNewMessageFromFriend() throws RemoteException;

    public void setClientId(String clientId) throws RemoteException;

    public String getClientId() throws RemoteException;

    public void setClientPassword(String password) throws RemoteException;

    public String getClientPassword() throws RemoteException;

    public void setClientsToValidate(String clientId, ChatInterface c) throws RemoteException;

    public HashMap<String, ChatInterface> getClientsToValidate() throws RemoteException;

    public void removeClientFromHashMap(String clientId) throws RemoteException;

    public void setValidate(boolean isValidate) throws RemoteException;

    public boolean getValidate() throws RemoteException;

    public void setFriendToAdd(String user, String username) throws RemoteException;

    public String getFriendToAdd() throws RemoteException;

    public void setFriends(String friends) throws RemoteException;

    public String getFriends() throws RemoteException;

    public void setIsNeedUpdateFriendList(boolean isNeedUpdateFriendList) throws RemoteException;

    public boolean getIsNeedUpdateFriendList() throws RemoteException;

    public void setDirectMessage(String sender, String receiverId, String msg) throws RemoteException;

    public String getDirectMessage() throws RemoteException;

    public void setIsNeedUpdateOutputText(boolean i) throws RemoteException;

    public boolean getIsNeedUpdateOutputText() throws RemoteException;

    public void setSelectedFriendId(String friendId) throws RemoteException;

    public String getSelectedFriendId() throws RemoteException;

    public void setUpdateOutputText(String message) throws RemoteException;

    public String getUpdateOutputText() throws RemoteException;

    public void setGroup(String groupName, String groupMembers) throws RemoteException;

    public String getGroup() throws RemoteException;

    public void setIsNewGroup(boolean i) throws RemoteException;

    public boolean getIsNewGroup() throws RemoteException;
}
