import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found. Add driver to classpath.");
            return;
        }
        EventQueue.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}
