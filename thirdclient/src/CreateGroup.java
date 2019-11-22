import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class CreateGroup extends JFrame {
    private JPanel panelMain;
    private JButton doneButton;
    private JLabel groupNameLabel;
    private JLabel membersLabel;
    private JTextField groupNameTextField;
    private JTextField membersTextField;
    private JLabel Note;
    private ChatInterface server;
    private ChatInterface client;

    public CreateGroup(ChatInterface client, ChatInterface server) {
        add(panelMain);
        setTitle("Create group");
        setSize(500, 200);
        setLocationRelativeTo(null);
        this.server = server;
        this.client = client;
        doneButton.addActionListener(doneCreateGroup);
    }

    private ActionListener doneCreateGroup = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        String groupName = groupNameTextField.getText();
                        String members = membersTextField.getText();
                        System.out.println(groupName);
                        System.out.println(members);
                        client.setGroup(groupName, members);
                        client.setIsNewGroup(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    dispose();
                }
            });

        }
    };
}
