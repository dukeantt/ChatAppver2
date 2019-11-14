import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class FindFriend extends JFrame {
    private JPanel panelMain;
    private JButton doneButton;
    private JTextField userNameTextField;
    private JLabel userNameLabel;
    private ChatInterface server;
    private ChatInterface client;

    public FindFriend(ChatInterface client, ChatInterface server) {
        add(panelMain);
        setTitle("Find more friends");
        setSize(250, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.server = server;
        this.client = client;
        doneButton.addActionListener(doneAddingFriend);
    }

    private ActionListener doneAddingFriend = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        String friendName = userNameTextField.getText();
                        String username = client.getName();
                        server.setFriendToAdd(username, friendName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    dispose();
                }
            });
        }
    };

}
