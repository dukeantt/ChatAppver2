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
    private boolean isNeedUpdateFriendList;
    private String directMessage;
    private String friendId;
    private boolean isNewMessageFromFriend;
    private boolean isNeedUpdateOutputText;
    private String updateOutputText;
    private String groupName;
    private String groupMembers;
    private boolean isNewGroup;

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
    public void removeClients(String clientId) throws RemoteException {
        this.clientsMap.remove(clientId);
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
    public void setIsNewMessageFromFriend(boolean i) throws RemoteException {
        this.isNewMessageFromFriend = i;
    }

    @Override
    public boolean getIsNewMessageFromFriend() throws RemoteException {
        return this.isNewMessageFromFriend;
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
        }
        return this.username + ";" + this.friendUsername;
    }

    @Override
    public void setFriends(String friends) throws RemoteException {
        this.friends = friends;
    }

    @Override
    public String getFriends() throws RemoteException {
        return this.friends;
    }

    @Override
    public void setIsNeedUpdateFriendList(boolean isNeedUpdateFriendList) throws RemoteException {
        this.isNeedUpdateFriendList = isNeedUpdateFriendList;
    }

    @Override
    public boolean getIsNeedUpdateFriendList() throws RemoteException {
        return this.isNeedUpdateFriendList;
    }

    @Override
    public void setDirectMessage(String sender, String receiverId, String msg) throws RemoteException {
        if (sender == null && receiverId == null && msg == null) {
            this.directMessage = null;
        } else {
            this.directMessage = sender + ";" + receiverId + ";" + msg;
        }
    }

    @Override
    public String getDirectMessage() throws RemoteException {
        return this.directMessage;
    }

    @Override
    public void setIsNeedUpdateOutputText(boolean i) throws RemoteException {
        this.isNeedUpdateOutputText = i;
    }

    @Override
    public boolean getIsNeedUpdateOutputText() throws RemoteException {
        return this.isNeedUpdateOutputText;
    }

    @Override
    public void setSelectedFriendId(String friendId) throws RemoteException {
        this.friendId = friendId;
    }

    @Override
    public String getSelectedFriendId() throws RemoteException {
        return this.friendId;
    }

    @Override
    public void setUpdateOutputText(String message) throws RemoteException {
        this.updateOutputText = message;
    }

    @Override
    public String getUpdateOutputText() throws RemoteException {
        return this.updateOutputText;
    }

    @Override
    public void setGroup(String groupName, String groupMembers) throws RemoteException {
        this.groupMembers = groupMembers;
        this.groupName = groupName;
    }

    @Override
    public String getGroup() throws RemoteException {
        return this.groupName + "``" + this.groupMembers;
    }

    @Override
    public void setIsNewGroup(boolean i) throws RemoteException {
        this.isNewGroup = i;
    }

    @Override
    public boolean getIsNewGroup() throws RemoteException {
        return this.isNewGroup;
    }

}
