import java.sql.*;
import java.util.*;

public class OfferDAO {
    public static boolean createOffer(int vehicleId, double exchangeValue, double subsidyPercent) {
        String sql = "INSERT INTO exchange_offers(vehicle_id,exchange_value,subsidy_percent) VALUES(?,?,?)";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, vehicleId);
            p.setDouble(2, exchangeValue);
            p.setDouble(3, subsidyPercent);
            p.executeUpdate();
            return true;
        } catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    public static java.util.List<Map<String,Object>> listAllOffers() {
        java.util.List<Map<String,Object>> out = new ArrayList<>();
        String sql = "SELECT o.*, v.plate_no, u.name FROM exchange_offers o JOIN vehicles v ON o.vehicle_id=v.vehicle_id JOIN users u ON v.owner_id=u.user_id ORDER BY o.created_at DESC";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("offer_id", r.getInt("offer_id"));
                m.put("vehicle_id", r.getInt("vehicle_id"));
                m.put("plate_no", r.getString("plate_no"));
                m.put("owner_name", r.getString("name"));
                m.put("exchange_value", r.getDouble("exchange_value"));
                m.put("subsidy_percent", r.getDouble("subsidy_percent"));
                m.put("status", r.getString("status"));
                out.add(m);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return out;
    }

    public static boolean updateStatus(int offerId, String status) {
        String sql = "UPDATE exchange_offers SET status=? WHERE offer_id=?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, status);
            p.setInt(2, offerId);
            p.executeUpdate();
            return true;
        } catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    public static Map<String,Double> stats() {
        Map<String,Double> m = new HashMap<>();
        String totalOffers = "SELECT COUNT(*) AS cnt FROM exchange_offers WHERE status='approved'";
        String totalSub = "SELECT SUM(exchange_value * subsidy_percent / 100) AS total_sub FROM exchange_offers WHERE status='approved'";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p1 = c.prepareStatement(totalOffers);
             ResultSet r1 = p1.executeQuery()) {
            if (r1.next()) m.put("approved_count", r1.getDouble("cnt"));
        } catch (Exception ex) { ex.printStackTrace(); }
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p2 = c.prepareStatement(totalSub);
             ResultSet r2 = p2.executeQuery()) {
            if (r2.next()) m.put("total_subsidy", r2.getDouble("total_sub"));
        } catch (Exception ex) { ex.printStackTrace(); }
        double cnt = m.getOrDefault("approved_count", 0.0);
        m.put("carbon_reduction_tons", cnt * 2.5);
        return m;
    }
}
