import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/gvei?serverTimezone=UTC";
    public static final String USER = "root"; // or 'gveiuser'
    public static final String PASS = "";     // root empty password or 'pass123'

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
