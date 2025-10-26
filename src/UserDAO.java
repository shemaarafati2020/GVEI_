import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    public static boolean register(String name, String email, String password, String role) {
        String sql = "INSERT INTO users(name,email,password,role) VALUES(?,?,?,?)";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, name);
            p.setString(2, email);
            p.setString(3, password);
            p.setString(4, role);
            p.executeUpdate();
            return true;
        } catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    public static Map<String,Object> login(String email, String password) {
        String sql = "SELECT user_id,name,role FROM users WHERE email=? AND password=?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, email);
            p.setString(2, password);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("user_id", r.getInt("user_id"));
                    m.put("name", r.getString("name"));
                    m.put("role", r.getString("role"));
                    return m;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }
}
