import javax.swing.*;
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
            System.setProperty("java.security.policy","server.policy");
            System.setSecurityManager(new SecurityManager());
        }

        //CONNECT TO DATABASE
        String DB_URL = "jdbc:mysql://172.17.0.1:3306/chatapp";
        String USER_NAME = "root";
        String PASSWORD = "1";
        Connection conn = null;
        conn = getConnection(DB_URL, USER_NAME, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from users");
        // show data
        while (rs.next()) {
            System.out.println(rs.getInt(1) + "  " + rs.getString(2)
                    + "  " + rs.getString(3));
        }
        // close connection
        conn.close();
        try {
            String name = "RMIchatapp";
            int port = 6000;
            ChatInterface server = new Chat("RMIchatapp");
            ChatInterface stub = (ChatInterface) UnicastRemoteObject.exportObject(server, port);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, stub);
            System.out.println("Server is ready");

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
