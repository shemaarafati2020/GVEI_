import java.awt.*;
import java.sql.*;
import java.util.Map;

public class ChartCanvas extends Canvas {
    private Map<String,Double> stats;
    public ChartCanvas(Map<String,Double> stats) {
        this.stats = stats;
    }

    @Override
    public void paint(Graphics g) {
        int approved = stats.getOrDefault("approved_count", 0.0).intValue();
        int pending = 0;
        try {
            String q = "SELECT COUNT(*) AS cnt FROM exchange_offers WHERE status='pending'";
            try (Connection c = DBConfig.getConnection();
                 PreparedStatement p = c.prepareStatement(q);
                 ResultSet r = p.executeQuery()) {
                if (r.next()) pending = r.getInt("cnt");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        int w = getWidth(), h = getHeight();
        int base = 50;
        g.setColor(Color.BLACK);
        g.drawString("Approved", 100, base+150);
        g.drawString("Pending", 300, base+150);
        int max = Math.max(1, Math.max(approved,pending));
        int ah = (int)((double)approved / max * 120);
        int ph = (int)((double)pending / max * 120);
        g.setColor(Color.BLUE);
        g.fillRect(90, base+150-ah, 60, ah);
        g.setColor(Color.ORANGE);
        g.fillRect(290, base+150-ph, 60, ph);
    }
}
