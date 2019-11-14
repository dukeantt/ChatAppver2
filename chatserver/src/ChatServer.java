import javax.swing.*;
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
//                            System.out.println(server.getClientsToValidate().size());
                            if (server.getClientsToValidate().size() != 0) {
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
                                                System.out.println("correct");
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
                                    System.out.println(userId);
                                }

                                // GET FRIEND ID
                                Statement stmt2 = conn.createStatement();
                                ResultSet rs2 = stmt2.executeQuery("select * from users where username =" + "\'" + friendName + "\'");
                                if (rs2.next()) {
                                    friendId = rs2.getString(4);
                                    System.out.println(friendId);
                                }

                                // ADD USER ID AND FRIEND IF TO FRIENDS TABLE
                                if (userId != null) {

                                    // CHECK IF USER ID IS EXISTED IN FRIENDS TABLE
                                    Statement stmt3 = conn.createStatement();
                                    ResultSet rs3 = stmt3.executeQuery("select * from friends where user_id =" + "\'" + userId + "\'");
                                    if (rs3.next()) {
                                        // IF USER ID EXISTED -> CONCAT FRIEND ID TO FRIENDS_ID COLUMN
                                        Statement stmt4 = conn.createStatement();
                                        int rs4 = stmt4.executeUpdate("update friends set friends_id = concat(friends_id," + "\'" + friendId + ";\'" + ")" + " WHERE user_id =" + "\'" + userId + "\'");

                                        // CHECK IF FRIEND ID IS EXISTED IN TABLE
                                        Statement stmt5 = conn.createStatement();
                                        ResultSet rs5 = stmt5.executeQuery("select * from friends where user_id =" + "\'" + friendId + "\'");
                                        if (rs5.next()) {
                                            // IF EXISTED -> CONCAT USER ID TO FRIENDS_ID COLUMN
                                            Statement stmt6 = conn.createStatement();
                                            int rs6 = stmt6.executeUpdate("update friends set friends_id = CONCAT(friends_id," + "\'" + userId + ";\'" + ")" + " WHERE user_id =" + "\'" + friendId + "\'");
                                        } else {
                                            // IF NOT EXISTED -> INSERT NEW ROW
                                            String SQL = "INSERT INTO friends(user_id,friends_id) " + "VALUES(?,?)";
                                            PreparedStatement preparedStatement3 = conn.prepareStatement(SQL);
                                            preparedStatement3.setString(1, friendId);
                                            preparedStatement3.setString(2, userId + ";");
                                            preparedStatement3.addBatch();
                                            preparedStatement3.executeBatch();
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

                                        // SHOULD CHECK  IF FRIEND ID EXISTED
                                        // IF EXISTED -> CONCAT
                                        // IF NOT -> NEW ROW
                                        // FIX THIS NEXT TIME
                                        PreparedStatement preparedStatement2 = conn.prepareStatement(SQL);
                                        preparedStatement2.setString(1, friendId);
                                        preparedStatement2.setString(2, userId + ";");
                                        preparedStatement2.addBatch();
                                        preparedStatement2.executeBatch();
                                        System.out.println("add friend successfully");

                                        // SET FRIEND TO ADD NULL WHEN FINISH ADD TO DATABASE
                                        server.setFriendToAdd(null, null);
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
            new Thread(addFriend).start();

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

    private static Connection getConnection(String dbURL, String userName,
                                            String password) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, userName, password);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
        }
        return conn;
    }
}
