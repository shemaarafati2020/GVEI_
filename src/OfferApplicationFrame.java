import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfferApplicationFrame extends JFrame {
    private JComboBox<String> vehicleCombo;
    private Map<Integer, Map<String,Object>> map = new HashMap<>();
    private int ownerId;

    public OfferApplicationFrame(int ownerId) {
        this.ownerId = ownerId;
        setTitle("Apply for Exchange");
        setSize(520,300);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(5,1,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JLabel("Choose your vehicle:"));
        vehicleCombo = new JComboBox<>();
        List<Map<String,Object>> list = VehicleDAO.listByOwner(ownerId);
        for (Map<String,Object> v : list) {
            int id = (Integer)v.get("vehicle_id");
            String s = id + " - " + v.get("plate_no") + " (" + v.get("fuel_type") + " " + v.get("year") + ")";
            vehicleCombo.addItem(s);
            map.put(id, v);
        }
        if (list.isEmpty()) {
            p.add(new JLabel("No vehicles registered. Register first."));
            JButton ok = new JButton("OK"); ok.addActionListener(e -> dispose()); p.add(ok);
            add(p); return;
        } else p.add(vehicleCombo);

        JButton bCalc = new JButton("Calculate Offer"); p.add(bCalc);
        JLabel result = new JLabel(""); p.add(result);
        JButton bApply = new JButton("Apply"); p.add(bApply);
        add(p);

        bCalc.addActionListener(e -> {
            String sel = (String)vehicleCombo.getSelectedItem();
            int vid = Integer.parseInt(sel.split(" - ")[0]);
            Map<String,Object> v = map.get(vid);
            int y = (Integer)v.get("year");
            String fuel = (String)v.get("fuel_type");
            int m = (Integer)v.get("mileage");
            boolean eligible = Helper.isEligible(fuel, y);
            if (!eligible) result.setText("Not eligible for exchange based on rules.");
            else {
                double val = Helper.calculateExchangeValue(y, m);
                double sub = Helper.calculateSubsidyPercent(y);
                result.setText(String.format("Exchange value: %.2f   Subsidy: %.2f%%", val, sub));
            }
        });

        bApply.addActionListener(e -> {
            String sel = (String)vehicleCombo.getSelectedItem();
            int vid = Integer.parseInt(sel.split(" - ")[0]);
            Map<String,Object> v = map.get(vid);
            int y = (Integer)v.get("year");
            String fuel = (String)v.get("fuel_type");
            int m = (Integer)v.get("mileage");
            if (!Helper.isEligible(fuel, y)) { Utils.showMsg(this, "Error", "Vehicle not eligible."); return; }
            double val = Helper.calculateExchangeValue(y, m);
            double sub = Helper.calculateSubsidyPercent(y);
            boolean ok = OfferDAO.createOffer(vid, val, sub);
            if (ok) Utils.showMsg(this, "OK", "Offer request created. Await admin review."); else Utils.showMsg(this, "Error", "Failed.");
        });
    }
}
