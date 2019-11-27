import javax.swing.*;

public class Loading extends JFrame {
    private JPanel panelMain;
    private JLabel loadingGIf;
    private JLabel title;

    public Loading() {
        add(panelMain);
        setTitle("Loading");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
