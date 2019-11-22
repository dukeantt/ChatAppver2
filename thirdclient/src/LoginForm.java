import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LoginForm extends JFrame{
    private JPanel panelMain;
    private JButton loginRegisterButton;
    private JTextField textField1;
    private JTextField textField2;

    public LoginForm() {
        loginRegisterButton.addActionListener(login);

    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        LoginForm f = new LoginForm();

        JFrame frame = new JFrame("Login");
        frame.setPreferredSize(new Dimension(300, 200));
        frame.setContentPane(new LoginForm().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }

    ActionListener login = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
//                String[] args = new String[0];
//                testform.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
