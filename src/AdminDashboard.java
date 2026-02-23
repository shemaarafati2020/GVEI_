import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private int adminId;
    private String adminName;
    private DefaultTableModel tableModel;
    private JTable offersTable;
    private JComboBox<String> statusFilter;
    private JTextField searchField;

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

        statusFilter = new JComboBox<>(new String[]{"all", "pending", "approved", "rejected"});
        searchField = new JTextField(14);
        JButton bRefresh = new JButton("Refresh Offers");
        JButton bApplyFilter = new JButton("Apply Filters");
        JButton bReset = new JButton("Reset");
        JButton bApprove = new JButton("Approve");
        JButton bReject = new JButton("Reject");
        JButton bExport = new JButton("Export Offers CSV");
        JButton bReport = new JButton("Show Stats");
        top.add(new JLabel("Status:"));
        top.add(statusFilter);
        top.add(new JLabel("Search (plate/owner):"));
        top.add(searchField);
        top.add(bApplyFilter);
        top.add(bReset);
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
        offersTable = new JTable(tableModel);
        offersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(offersTable), BorderLayout.CENTER);

        // Button actions
        bRefresh.addActionListener(e -> refreshOffers());
        bApplyFilter.addActionListener(e -> refreshOffers());
        bReset.addActionListener(e -> {
            statusFilter.setSelectedItem("all");
            searchField.setText("");
            refreshOffers();
        });
        bApprove.addActionListener(e -> changeStatus("approved"));
        bReject.addActionListener(e -> changeStatus("rejected"));
        bExport.addActionListener(e -> exportOffersCSV());
        bReport.addActionListener(e -> showStats());

        refreshOffers();

        setVisible(true);
    }

    private void refreshOffers() {
        tableModel.setRowCount(0);
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String keyword = searchField.getText();
        List<Map<String,Object>> offers = OfferDAO.listOffersFiltered(selectedStatus, keyword);
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

    private void changeStatus(String target) {
        int[] rows = offersTable.getSelectedRows();
        if (rows.length < 1) {
            Utils.showMsg(this, "Error", "Select at least one offer");
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (int row : rows) {
            ids.add((Integer) offersTable.getValueAt(row, 0));
        }
        int updated = OfferDAO.updateStatusBulk(ids, target);
        if (updated > 0) {
            Utils.showMsg(this, "OK", "Updated " + updated + " offer(s) to " + target);
            refreshOffers();
        } else {
            Utils.showMsg(this, "Error", "Failed update");
        }
    }

    private void exportOffersCSV() {
        if (tableModel.getRowCount() == 0) { Utils.showMsg(this, "Export", "No offers to export."); return; }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("offers.csv"));
        int res = fc.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("offer_id,vehicle_id,plate_no,owner_name,exchange_value,subsidy_percent,status");
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pw.printf("%d,%d,%s,%s,%.2f,%.2f,%s%n",
                        (Integer)tableModel.getValueAt(i, 0),
                        (Integer)tableModel.getValueAt(i, 1),
                        String.valueOf(tableModel.getValueAt(i, 2)),
                        String.valueOf(tableModel.getValueAt(i, 3)),
                        ((Number)tableModel.getValueAt(i, 4)).doubleValue(),
                        ((Number)tableModel.getValueAt(i, 5)).doubleValue(),
                        String.valueOf(tableModel.getValueAt(i, 6)));
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
