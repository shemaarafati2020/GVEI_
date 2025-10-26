import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private int adminId;
    private String adminName;
    private DefaultTableModel tableModel;

    public AdminDashboard(int adminId, String adminName) {
        this.adminId = adminId;
        this.adminName = adminName;

        setTitle("Admin - " + adminName);
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // prevents JVM exit on logout

        // Top panel with left-aligned buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Admin: " + adminName));
        JButton bRefresh = new JButton("Refresh Offers");
        JButton bApprove = new JButton("Approve");
        JButton bReject = new JButton("Reject");
        JButton bExport = new JButton("Export Offers CSV");
        JButton bReport = new JButton("Show Stats");
        top.add(bRefresh);
        top.add(bApprove);
        top.add(bReject);
        top.add(bExport);
        top.add(bReport);

        // Logout button - top-right
        JButton bLogout = new JButton("Logout");
        bLogout.addActionListener(e -> {
            dispose();          // close current dashboard only
            new LoginFrame();   // open login frame
        });

        // Wrap top panel in BorderLayout to separate left buttons and logout right
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(top, BorderLayout.WEST);
        topWrapper.add(bLogout, BorderLayout.EAST);
        add(topWrapper, BorderLayout.NORTH);

        // Table for offers
        tableModel = new DefaultTableModel(
                new String[]{"offer_id","vehicle_id","plate_no","owner_name","exchange_value","subsidy_percent","status"},0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button actions
        bRefresh.addActionListener(e -> refreshOffers());
        bApprove.addActionListener(e -> changeStatus(table, "approved"));
        bReject.addActionListener(e -> changeStatus(table, "rejected"));
        bExport.addActionListener(e -> exportOffersCSV());
        bReport.addActionListener(e -> showStats());

        refreshOffers();

        setVisible(true);
    }

    private void refreshOffers() {
        tableModel.setRowCount(0);
        List<Map<String,Object>> offers = OfferDAO.listAllOffers();
        for (Map<String,Object> o : offers) {
            tableModel.addRow(new Object[]{
                    o.get("offer_id"),
                    o.get("vehicle_id"),
                    o.get("plate_no"),
                    o.get("owner_name"),
                    o.get("exchange_value"),
                    o.get("subsidy_percent"),
                    o.get("status")
            });
        }
    }

    private void changeStatus(JTable table, String target) {
        int row = table.getSelectedRow();
        if (row < 0) { Utils.showMsg(this, "Error", "Select an offer"); return; }
        int id = (Integer)table.getValueAt(row, 0);
        boolean ok = OfferDAO.updateStatus(id, target);
        if (ok) {
            Utils.showMsg(this, "OK", "Status updated to " + target);
            refreshOffers();
        } else {
            Utils.showMsg(this, "Error", "Failed update");
        }
    }

    private void exportOffersCSV() {
        List<Map<String,Object>> offers = OfferDAO.listAllOffers();
        if (offers.isEmpty()) { Utils.showMsg(this, "Export", "No offers to export."); return; }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("offers.csv"));
        int res = fc.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("offer_id,vehicle_id,plate_no,owner_name,exchange_value,subsidy_percent,status");
            for (Map<String,Object> o : offers) {
                pw.printf("%d,%d,%s,%s,%.2f,%.2f,%s%n",
                        (Integer)o.get("offer_id"),
                        (Integer)o.get("vehicle_id"),
                        o.get("plate_no"),
                        o.get("owner_name"),
                        (Double)o.get("exchange_value"),
                        (Double)o.get("subsidy_percent"),
                        (String)o.get("status"));
            }
            Utils.showMsg(this, "Export", "CSV exported successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.showMsg(this, "Error", "Export failed.");
        }
    }

    private void showStats() {
        Map<String,Double> s = OfferDAO.stats();
        JFrame f = new JFrame("Statistics");
        f.setSize(600,400);
        f.setLocationRelativeTo(this);
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.append("Approved exchanges: " + s.getOrDefault("approved_count",0.0).intValue() + "\n");
        ta.append("Total subsidy paid: " + String.format("%.2f", s.getOrDefault("total_subsidy",0.0)) + "\n");
        ta.append("Estimated carbon reduction (tons/year): " + String.format("%.2f", s.getOrDefault("carbon_reduction_tons",0.0)) + "\n");
        f.add(new JScrollPane(ta), BorderLayout.CENTER);

        // AWT Canvas for simple chart
        ChartCanvas chart = new ChartCanvas(s);
        chart.setPreferredSize(new Dimension(600,220));
        f.add(chart, BorderLayout.SOUTH);

        f.setVisible(true);
    }
}
