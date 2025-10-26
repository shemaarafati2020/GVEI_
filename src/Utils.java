import javax.swing.*;
import java.awt.*;

public class Utils {
    public static void showMsg(Component parent, String title, String msg) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
