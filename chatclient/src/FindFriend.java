import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindFriend extends JFrame{
    private JPanel panelMain;
    private JButton doneButton;
    private JTextField userNameTextField;
    private JLabel userNameLabel;

    public FindFriend() {
        add(panelMain);
        setTitle("Find more friends");
        setSize(250, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        doneButton.addActionListener(doneAddingFriend);
    }

    private ActionListener doneAddingFriend = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setVisible(false);
                }
            });
        }
    };

}
