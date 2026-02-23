import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CitizenDashboard extends JFrame {
    private int userId;
    private String userName;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel summaryLabel;

    public CitizenDashboard(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        setTitle("Citizen - " + userName);
        setSize(900,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // important: prevents JVM exit

        // Top panel with left-aligned buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Logged as: " + userName));
        JButton bRegVehicle = new JButton("Register Vehicle");
        JButton bMyVehicles = new JButton("My Vehicles");
        JButton bApplyOffer = new JButton("Apply for Exchange");
        JButton bExport = new JButton("Export My Vehicles CSV");
        JButton bSearch = new JButton("Search");
        JButton bReset = new JButton("Reset");
        searchField = new JTextField(12);
        top.add(bRegVehicle);
        top.add(bMyVehicles);
        top.add(bApplyOffer);
        top.add(bExport);
        top.add(new JLabel("Filter (plate/type/fuel):"));
        top.add(searchField);
        top.add(bSearch);
        top.add(bReset);

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

        // Table for vehicles
        tableModel = new DefaultTableModel(
                new String[]{"vehicle_id","plate_no","type","fuel","year","mileage"},0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        summaryLabel = new JLabel(" ");
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(summaryLabel, BorderLayout.SOUTH);

        // Button actions
        bRegVehicle.addActionListener(e -> new VehicleRegistrationFrame(userId).setVisible(true));
        bMyVehicles.addActionListener(e -> refreshList());
        bApplyOffer.addActionListener(e -> new OfferApplicationFrame(userId).setVisible(true));
        bExport.addActionListener(e -> exportCSV());
        bSearch.addActionListener(e -> refreshList());
        bReset.addActionListener(e -> {
            searchField.setText("");
            refreshList();
        });

        // Populate initial list
        refreshList();

        setVisible(true);
    }

    // Refresh vehicle list
    private void refreshList() {
        tableModel.setRowCount(0);
        List<Map<String,Object>> list = VehicleDAO.listByOwner(userId);
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        int total = 0;
        int eligible = 0;
        int mileageSum = 0;
        for (Map<String,Object> v : list) {
            String plate = String.valueOf(v.get("plate_no"));
            String type = String.valueOf(v.get("vehicle_type"));
            String fuel = String.valueOf(v.get("fuel_type"));
            int year = (Integer) v.get("year");
            int mileage = (Integer) v.get("mileage");

            if (!keyword.isEmpty()) {
                String hay = (plate + " " + type + " " + fuel).toLowerCase();
                if (!hay.contains(keyword)) {
                    continue;
                }
            }

            total++;
            mileageSum += mileage;
            if (Helper.isEligible(fuel, year)) {
                eligible++;
            }

            tableModel.addRow(new Object[]{
                    v.get("vehicle_id"),
                    plate,
                    type,
                    fuel,
                    year,
                    mileage
            });
        }

        if (total == 0) {
            summaryLabel.setText("No matching vehicles. Clear the filter or register a new vehicle.");
        } else {
            double avgMileage = (double) mileageSum / total;
            summaryLabel.setText(String.format("Showing %d vehicle(s) | Eligible for exchange: %d | Avg mileage: %.0f km", total, eligible, avgMileage));
        }
    }

    // Export vehicles to CSV
    private void exportCSV() {
        List<Map<String,Object>> list = VehicleDAO.listByOwner(userId);
        if (list.isEmpty()) { Utils.showMsg(this, "Export", "No vehicles to export."); return; }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("vehicles.csv"));
        int res = fc.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("vehicle_id,plate_no,vehicle_type,fuel_type,year,mileage");
            for (Map<String,Object> v : list) {
                pw.printf("%d,%s,%s,%s,%d,%d%n",
                        (Integer)v.get("vehicle_id"),
                        v.get("plate_no"),
                        v.get("vehicle_type"),
                        v.get("fuel_type"),
                        (Integer)v.get("year"),
                        (Integer)v.get("mileage"));
            }
            Utils.showMsg(this, "Export", "CSV exported successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.showMsg(this, "Error", "Export failed.");
        }
    }
}
