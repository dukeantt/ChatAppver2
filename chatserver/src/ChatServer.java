import javax.swing.*;
import javax.xml.transform.Result;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws SQLException {
        if (System.getSecurityManager() == null) {
            System.setProperty("java.security.policy", "server.policy");
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "RMIchatapp";
            int port = 6000;
            ChatInterface server = new Chat("RMIchatapp");
            ChatInterface stub = (ChatInterface) UnicastRemoteObject.exportObject(server, port);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, stub);
            System.out.println("Server is ready");
            server.setValidate(true);

            //THREAD TO VALIDATE ACCOUNT ACCESS TO SERVER
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                            if (server.getClientsToValidate().size() != 0) {
                                System.out.println("validate user");
                                HashMap<String, ChatInterface> clients = server.getClientsToValidate();
                                for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                                    try {
                                        ChatInterface client = clientMap.getValue();
                                        assert client != null;
                                        String clientName = client.getName();
                                        String password = client.getClientPassword();
                                        String clientId = client.getClientId();
                                        server.removeClientFromHashMap(clientId);

                                        //CONNECT TO DATABASE
                                        String DB_URL = "jdbc:mysql://172.17.0.1:3306/chatapp";
                                        String USER_NAME = "root";
                                        String PASSWORD = "1";
                                        Connection conn = null;
                                        conn = getConnection(DB_URL, USER_NAME, PASSWORD);
                                        Statement stmt = conn.createStatement();
                                        ResultSet rs = stmt.executeQuery("select * from users where username =" + "\'" + clientName + "\'");

                                        //PREPARE INSERT DATA TO DATABASE
                                        String SQL = "INSERT INTO users(username,password,client_id) " + "VALUES(?,?,?)";
                                        PreparedStatement preparedStatement = conn.prepareStatement(SQL);

                                        // COMPARE DATA
                                        if (rs.next()) {
                                            String passwordInDb = rs.getString(3);
                                            if (password.equals(passwordInDb)) {
                                                server.setValidate(true);
                                                server.removeClientFromHashMap(clientId);

                                                //GET FRIEND LIST WHEN USER LOGIN
                                                String friendList = getUpdatedFriendList(conn, clientName);
                                                client.setFriends(friendList);
                                                client.setIsNeedUpdateFriendList(true);
                                            } else {
                                                server.setValidate(false);
                                            }
                                        } else {
                                            server.setValidate(true);
                                            System.out.println("New user");
                                            //SET USER DATA AND EXECUTE INSERT
                                            preparedStatement.setString(1, clientName);
                                            preparedStatement.setString(2, password);
                                            preparedStatement.setString(3, clientId);
                                            preparedStatement.addBatch();
                                            preparedStatement.executeBatch();
                                        }
                                        // close connection
                                        conn.close();
                                    } catch (ConnectException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                            }
                        } catch (RemoteException | SQLException | InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            };
            new Thread(r).start();

            //THREAD TO HANDLE ADD FRIEND FEATURE
            Runnable addFriend = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                            if (server.getFriendToAdd() != null) {
                                System.out.println("handle add friend");
                                String friendToAdd = server.getFriendToAdd();
                                String[] addFriend = friendToAdd.split(";");
                                String username = addFriend[0];
                                String friendName = addFriend[1];
                                String userId = null;
                                String friendId = null;

                                //CONNECT TO DATABASE
                                String DB_URL = "jdbc:mysql://172.17.0.1:3306/chatapp";
                                String USER_NAME = "root";
                                String PASSWORD = "1";
                                Connection conn = null;
                                conn = getConnection(DB_URL, USER_NAME, PASSWORD);

                                //  GET USER ID
                                Statement stmt1 = conn.createStatement();
                                ResultSet rs1 = stmt1.executeQuery("select * from users where username =" + "\'" + username + "\'");
                                if (rs1.next()) {
                                    userId = rs1.getString(4);
                                }

                                // GET FRIEND ID
                                Statement stmt2 = conn.createStatement();
                                ResultSet rs2 = stmt2.executeQuery("select * from users where username =" + "\'" + friendName + "\'");
                                if (rs2.next()) {
                                    friendId = rs2.getString(4);
                                }

                                // ADD USER ID AND FRIEND ID TO FRIENDS TABLE
                                if (userId != null && friendId != null) {
                                    ChatInterface client1 = null;
                                    ChatInterface client2 = null;

                                    HashMap<String, ChatInterface> clients = server.getClients();
                                    for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                                        ChatInterface client = clientMap.getValue();
                                        if (username.equals(client.getName())) {
                                            client1 = client;
                                        }
                                    }
                                    String friendList = null;
                                    // CHECK IF USER ID IS EXISTED IN FRIENDS TABLE
                                    Statement stmt3 = conn.createStatement();
                                    ResultSet rs3 = stmt3.executeQuery("select * from friends where user_id =" + "\'" + userId + "\'");
                                    if (rs3.next()) {
                                        // IF USER ID EXISTED -> CONCAT FRIEND ID TO FRIENDS_ID COLUMN
                                        Statement stmt4 = conn.createStatement();
                                        int rs4 = stmt4.executeUpdate("update friends set friends_id = concat(friends_id," + "\'" + friendId + ";\'" + ")" + " WHERE user_id =" + "\'" + userId + "\'");
                                        // GET UPDATED USER FRIEND LIST
                                        friendList = getUpdatedFriendList(conn, username);
                                        client1.setIsNeedUpdateFriendList(true);
                                        client1.setFriends(friendList);

                                        // CHECK IF FRIEND ID IS EXISTED IN TABLE
                                        Statement stmt5 = conn.createStatement();
                                        ResultSet rs5 = stmt5.executeQuery("select * from friends where user_id =" + "\'" + friendId + "\'");
                                        if (rs5.next()) {
                                            // IF EXISTED -> CONCAT USER ID TO FRIENDS_ID COLUMN
                                            Statement stmt6 = conn.createStatement();
                                            int rs6 = stmt6.executeUpdate("update friends set friends_id = CONCAT(friends_id," + "\'" + userId + ";\'" + ")" + " WHERE user_id =" + "\'" + friendId + "\'");
                                            // GET UPDATED USER'S FRIEND  FRIEND LIST
                                            friendList = getUpdatedFriendList(conn, friendName);
//                                            client2.setIsNeedUpdateFriendList(true);
//                                            client2.setFriends(friendList);
                                        } else {
                                            // IF NOT EXISTED -> INSERT NEW ROW
                                            String SQL = "INSERT INTO friends(user_id,friends_id) " + "VALUES(?,?)";
                                            PreparedStatement preparedStatement3 = conn.prepareStatement(SQL);
                                            preparedStatement3.setString(1, friendId);
                                            preparedStatement3.setString(2, userId + ";");
                                            preparedStatement3.addBatch();
                                            preparedStatement3.executeBatch();
                                            // GET UPDATED USER'S FRIEND  FRIEND LIST
                                            friendList = getUpdatedFriendList(conn, friendName);
//                                            client2.setIsNeedUpdateFriendList(true);
//                                            client2.setFriends(friendList);
                                        }

                                        // SET FRIEND TO ADD NULL WHEN FINISH ADD TO DATABASE
                                        server.setFriendToAdd(null, null);
                                    } else {
                                        // IF USER ID NOT EXISTED IN TABLE -> CREATE NEW ROW FOR USER ID
                                        String SQL = "INSERT INTO friends(user_id,friends_id) " + "VALUES(?,?)";
                                        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
                                        preparedStatement.setString(1, userId);
                                        preparedStatement.setString(2, friendId + ";");
                                        preparedStatement.addBatch();
                                        preparedStatement.executeBatch();
                                        // GET UPDATED USER FRIEND LIST
                                        friendList = getUpdatedFriendList(conn, username);
                                        client1.setIsNeedUpdateFriendList(true);
                                        client1.setFriends(friendList);

                                        // CHECK IF FRIEND ID IS EXISTED IN TABLE
                                        Statement stmt7 = conn.createStatement();
                                        ResultSet rs7 = stmt7.executeQuery("select * from friends where user_id =" + "\'" + friendId + "\'");
                                        if (rs7.next()) {
                                            // IF EXISTED -> CONCAT USER ID TO FRIENDS_ID COLUMN
                                            Statement stmt8 = conn.createStatement();
                                            int rs8 = stmt8.executeUpdate("update friends set friends_id = CONCAT(friends_id," + "\'" + userId + ";\'" + ")" + " WHERE user_id =" + "\'" + friendId + "\'");
                                            // GET UPDATED USER'S FRIEND  FRIEND LIST
                                            friendList = getUpdatedFriendList(conn, friendName);
//                                            client2.setIsNeedUpdateFriendList(true);
//                                            client2.setFriends(friendList);
                                        } else {
                                            PreparedStatement preparedStatement2 = conn.prepareStatement(SQL);
                                            preparedStatement2.setString(1, friendId);
                                            preparedStatement2.setString(2, userId + ";");
                                            preparedStatement2.addBatch();
                                            preparedStatement2.executeBatch();
                                            System.out.println("add friend successfully");
                                            // GET UPDATED USER'S FRIEND  FRIEND LIST
                                            friendList = getUpdatedFriendList(conn, friendName);
//                                            client2.setIsNeedUpdateFriendList(true);
//                                            client2.setFriends(friendList);
                                        }
                                        // SET FRIEND TO ADD NULL WHEN FINISH ADD TO DATABASE
                                        server.setFriendToAdd(null, null);
                                    }
                                }
                                // close connection
                                conn.close();
                            }
                        } catch (RemoteException | SQLException | InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            };
            new Thread(addFriend).start();

            //THREAD TO HANDLE CREATE GROUP
            Runnable createGroup = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                            String friendList = null;
                            //CONNECT TO DATABASE
                            String DB_URL = "jdbc:mysql://172.17.0.1:3306/chatapp";
                            String USER_NAME = "root";
                            String PASSWORD = "1";
                            Connection conn = null;
                            conn = getConnection(DB_URL, USER_NAME, PASSWORD);
                            if (server.getClients() != null) {
                                HashMap<String, ChatInterface> clients = server.getClients();
                                for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                                    ChatInterface client = clientMap.getValue();
                                    if (client.getIsNewGroup()) {
                                        if (client.getGroup() != null) {
                                            String[] group = client.getGroup().split("``");
                                            String groupName = group[0];
                                            StringBuilder membersId = new StringBuilder();
                                            String[] membersNameArray = group[1].split(";");
                                            for (int i = 0; i < membersNameArray.length; i++) {
                                                String memberId = getUserIdByName(conn, membersNameArray[i]);
                                                membersId.append(memberId).append(";");
                                            }
                                            String clientId = getUserIdByName(conn, client.getName());
                                            membersId.append(clientId).append(";");
                                            //ADD TO MESSAGES TABLE
                                            String SQL = "INSERT INTO messages(message,sender_id,receiver_id) " + "VALUES(?,?,?)";
                                            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
                                            preparedStatement.setString(1, "");
                                            preparedStatement.setString(2, "group:" + groupName);
                                            preparedStatement.setString(3, membersId.toString());
                                            preparedStatement.addBatch();
                                            preparedStatement.executeBatch();
                                            client.setIsNewGroup(false);

                                            //ADD TO FRIENDS TABLE
                                            //IF EXISTED -> CONCAT
                                            String[] membersIdArray = membersId.toString().split(";");
                                            for (int j = 0; j < membersIdArray.length; j++) {
                                                String userId = membersIdArray[j];
                                                Statement stmt = conn.createStatement();
                                                ResultSet rs = stmt.executeQuery("select * from friends where user_id =" + "\'" + userId + "\'");
                                                if (rs.next()) {
                                                    Statement stmt2 = conn.createStatement();
                                                    int rs2 = stmt2.executeUpdate("update friends set friends_id = concat(friends_id," + "\'group:" + groupName + ";\'" + ")" + " WHERE user_id =" + "\'" + userId + "\'");
                                                    client.setIsNeedUpdateFriendList(true);
                                                    friendList = getUpdatedFriendList(conn, client.getName());
                                                    client.setFriends(friendList);
                                                } else {
                                                    String SQL2 = "INSERT INTO friends(user_id,friends_id) " + "VALUES(?,?)";
                                                    PreparedStatement preparedStatement2 = conn.prepareStatement(SQL2);
                                                    preparedStatement2.setString(1, userId);
                                                    preparedStatement2.setString(2, "group:" + groupName + ";");
                                                    preparedStatement2.addBatch();
                                                    preparedStatement2.executeBatch();
                                                    client.setIsNeedUpdateFriendList(true);
                                                    friendList = getUpdatedFriendList(conn, client.getName());
                                                    client.setFriends(friendList);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // close connection
                            conn.close();
                        } catch (RemoteException | InterruptedException | SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            new Thread(createGroup).start();

            //THREAD TO HANDLE SEND DIRECT MESSAGE BETWEEN CLIENTS
            Runnable clientsSendMessageToEachOther = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                            boolean isNewMessage = false;
                            //CONNECT TO DATABASE
                            String DB_URL = "jdbc:mysql://172.17.0.1:3306/chatapp";
                            String USER_NAME = "root";
                            String PASSWORD = "1";
                            Connection conn = null;
                            conn = getConnection(DB_URL, USER_NAME, PASSWORD);
                            if (server.getClients() != null) {
                                HashMap<String, ChatInterface> clients = server.getClients();
//                                Thread.sleep(1000);
                                if (clients.size() != 0) {
                                    for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                                        ChatInterface client = clientMap.getValue();
                                        if (client.getDirectMessage() != null) {
                                            String[] directMessage = client.getDirectMessage().split(";");
                                            String senderName = directMessage[0];
                                            String receiverId = directMessage[1];
                                            String message = directMessage[2];
                                            String senderId = null;
                                            message = "[" + senderName + "]: " + message;
                                            if (receiverId.contains("group:")) {
                                                // RECEIVER ID = GROUP NAME AND IN MESSAGES TABLE GROUP NAME IS ALWAYS IN SENDER ID COLUMN
                                                // SAVE MESSAGE TO DB
                                                Statement stmt = conn.createStatement();
                                                ResultSet rs = stmt.executeQuery("select * from messages where sender_id =" + "\'" + receiverId + "\'");
                                                isNewMessage = server.getIsNewMessage();
                                                if (rs.next() && isNewMessage) {
                                                    System.out.println("groupy");
                                                    Statement stmt2 = conn.createStatement();
                                                    int rs2 = stmt2.executeUpdate("update messages set message = CONCAT(message," + "\'" + message + ";\'" + ")" + " WHERE sender_id =" + "\'" + receiverId + "\'");
                                                    server.setIsNewMessage(false);
                                                    //send message to other member
                                                    String[] members = rs.getString(4).split(";");
                                                    senderId = getUserIdByName(conn, senderName);
                                                    for (int i = 0; i < members.length; i++) {
                                                        if (!members[i].equals(senderId)) {
                                                            ChatInterface receiver = server.getClientById(members[i]);
                                                            if (receiver != null) {
                                                                receiver.setDirectMessage(senderId, receiverId, message);
                                                                receiver.setIsNewMessageFromFriend(true);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                Statement stmt1 = conn.createStatement();
                                                ResultSet rs1 = stmt1.executeQuery("select * from users where username =" + "\'" + senderName + "\'");
                                                if (rs1.next()) {
                                                    senderId = rs1.getString(4);
                                                }
                                                isNewMessage = server.getIsNewMessage();
                                                if (senderId != null && isNewMessage) {
                                                    Statement stmt = conn.createStatement();
                                                    // CHECK IF SENDER ID RECEIVER ID EXIST IN DATABASE
                                                    ResultSet rs = stmt.executeQuery("select * from messages where sender_id =" + "\'" + senderId + "\'" + "or receiver_id =" + "\'" + senderId + "\'");
                                                    // IF EXISTED -> UPDATE
                                                    boolean isExistedInDb = false;
                                                    while (rs.next()) {
                                                        String dbSenderId = rs.getString(3);
                                                        String dbReceiverId = rs.getString(4);
                                                        if (receiverId.equals(dbSenderId)) {
                                                            Statement stmt2 = conn.createStatement();
                                                            int rs2 = stmt2.executeUpdate("update messages set message = CONCAT(message," + "\'" + message + ";\'" + ")" + " WHERE sender_id =" + "\'" + receiverId + "\'" + "AND receiver_id=" + "\'" + senderId + "\'");
                                                            server.setIsNewMessage(false);
                                                            isExistedInDb = true;
                                                        } else if (receiverId.equals(dbReceiverId)) {
                                                            Statement stmt2 = conn.createStatement();
                                                            int rs2 = stmt2.executeUpdate("update messages set message = CONCAT(message," + "\'" + message + ";\'" + ")" + " WHERE sender_id =" + "\'" + senderId + "\'" + "AND receiver_id=" + "\'" + receiverId + "\'");
                                                            server.setIsNewMessage(false);
                                                            isExistedInDb = true;
                                                        }
                                                    }
                                                    if (!isExistedInDb) {
                                                        // IF NOT -> CREATE NEW ROW
                                                        String SQL = "INSERT INTO messages(message,sender_id,receiver_id) " + "VALUES(?,?,?)";
                                                        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
                                                        preparedStatement.setString(1, message + ";");
                                                        preparedStatement.setString(2, senderId);
                                                        preparedStatement.setString(3, receiverId);
                                                        preparedStatement.addBatch();
                                                        preparedStatement.executeBatch();
                                                        server.setIsNewMessage(false);
                                                    }
                                                }
                                                ChatInterface receiver = server.getClientById(receiverId);
                                                if (receiver != null) {
                                                    receiver.setDirectMessage(senderId, receiverId, message);
                                                    receiver.setIsNewMessageFromFriend(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // close connection
                            conn.close();
                        } catch (RemoteException | SQLException | InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            };
            new Thread(clientsSendMessageToEachOther).start();

            //THREAD TO UPDATED OUTPUT TEXT WHEN SELECT FRIEND IN FRIEND LIST
            Runnable updateOutputText = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
//                            Thread.sleep(1000);
                            //CONNECT TO DATABASE
                            String DB_URL = "jdbc:mysql://172.17.0.1:3306/chatapp";
                            String USER_NAME = "root";
                            String PASSWORD = "1";
                            Connection conn = null;
                            conn = getConnection(DB_URL, USER_NAME, PASSWORD);
                            if (server.getClients() != null) {
                                HashMap<String, ChatInterface> clients = server.getClients();
                                for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                                    ChatInterface client = clientMap.getValue();
                                    boolean isNeedUpdateOutputText = client.getIsNeedUpdateOutputText();
                                    if (isNeedUpdateOutputText) {
                                        String clientName = client.getName();
                                        String clientId = getUserIdByName(conn, clientName);
                                        String friendId = client.getSelectedFriendId();
                                        if (friendId.contains("group:")) {
                                            Statement stmt = conn.createStatement();
                                            ResultSet rs = stmt.executeQuery("select * from messages where sender_id =" + "\'" + friendId + "\'");
                                            if (rs.next()) {
                                                String message = rs.getString(2);
                                                client.setUpdateOutputText(message);
                                            }
                                        } else {
                                            Statement stmt = conn.createStatement();
                                            ResultSet rs = stmt.executeQuery("select * from messages where sender_id =" + "\'" + clientId + "\'" + "or receiver_id =" + "\'" + clientId + "\'");
                                            while (rs.next()) {
                                                String senderId = rs.getString(3);
                                                String receiverId = rs.getString(4);
                                                if (friendId.equals(senderId) || friendId.equals(receiverId)) {
                                                    String message = rs.getString(2);
                                                    client.setUpdateOutputText(message);
//                                            client.setIsNeedUpdateOutputText(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // close connection
                            conn.close();
                        } catch (RemoteException | SQLException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            };
            new Thread(updateOutputText).start();

            //SEND MESSAGE TO ALL CLIENT
            Scanner s = new Scanner(System.in);
            while (true) {
                String msg = s.nextLine().trim();
                if (server.getClients() != null) {

                    HashMap<String, ChatInterface> clients = server.getClients();
                    for (Map.Entry<String, ChatInterface> clientMap : clients.entrySet()) {
                        ChatInterface client = clientMap.getValue();
                        client.printMsg("[" + server.getName() + "] " + msg);
                        client.setMsg(msg);
                        client.setIsNewMessage(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection(String dbURL, String userName, String password) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, userName, password);
//            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
        }
        return conn;
    }

    private static String getUpdatedFriendList(Connection conn, String username) throws SQLException {
        String friendList = null;
        String userId = null;
        Statement stmt = conn.createStatement();
        Statement stmt1 = conn.createStatement();
        ResultSet rs1 = stmt1.executeQuery("select * from users where username =" + "\'" + username + "\'");
        if (rs1.next()) {
            userId = rs1.getString(4);
            ResultSet rs = stmt.executeQuery("select * from friends where user_id =" + "\'" + userId + "\'");
            if (rs.next()) {
                friendList = rs.getString(3);
            }
        }
        return friendList;
    }


    private static String getUserIdByName(Connection conn, String username) throws SQLException {
        Statement stmt = conn.createStatement();
        String userId = null;
        ResultSet rs1 = stmt.executeQuery("select * from users where username =" + "\'" + username + "\'");
        if (rs1.next()) {
            userId = rs1.getString(4);
        }
        return userId;
    }
}
