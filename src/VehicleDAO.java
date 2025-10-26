import java.sql.*;
import java.util.*;

public class VehicleDAO {
    public static boolean registerVehicle(int ownerId, String plate, String vtype, String fuel, int year, int mileage) {
        String sql = "INSERT INTO vehicles(owner_id,plate_no,vehicle_type,fuel_type,year,mileage) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, ownerId);
            p.setString(2, plate);
            p.setString(3, vtype);
            p.setString(4, fuel);
            p.setInt(5, year);
            p.setInt(6, mileage);
            p.executeUpdate();
            return true;
        } catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    public static java.util.List<Map<String,Object>> listByOwner(int ownerId) {
        java.util.List<Map<String,Object>> out = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id=?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, ownerId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("vehicle_id", r.getInt("vehicle_id"));
                    m.put("plate_no", r.getString("plate_no"));
                    m.put("vehicle_type", r.getString("vehicle_type"));
                    m.put("fuel_type", r.getString("fuel_type"));
                    m.put("year", r.getInt("year"));
                    m.put("mileage", r.getInt("mileage"));
                    out.add(m);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return out;
    }

    public static java.util.List<Map<String,Object>> listAll() {
        java.util.List<Map<String,Object>> out = new ArrayList<>();
        String sql = "SELECT v.*, u.name FROM vehicles v JOIN users u ON v.owner_id=u.user_id";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("vehicle_id", r.getInt("vehicle_id"));
                m.put("plate_no", r.getString("plate_no"));
                m.put("vehicle_type", r.getString("vehicle_type"));
                m.put("fuel_type", r.getString("fuel_type"));
                m.put("year", r.getInt("year"));
                m.put("mileage", r.getInt("mileage"));
                m.put("owner_name", r.getString("name"));
                out.add(m);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return out;
    }
}
