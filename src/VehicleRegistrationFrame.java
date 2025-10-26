import javax.swing.*;
import java.awt.*;

public class VehicleRegistrationFrame extends JFrame {
    private JTextField plateField, yearField, mileageField;
    private JComboBox<String> typeCombo, fuelCombo;
    private int ownerId;

    public VehicleRegistrationFrame(int ownerId) {
        this.ownerId = ownerId;
        setTitle("Register Vehicle");
        setSize(420,350);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(6,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JLabel("Plate no:")); plateField = new JTextField(); p.add(plateField);
        p.add(new JLabel("Vehicle type:")); typeCombo = new JComboBox<>(new String[]{"car","bus","motorcycle"}); p.add(typeCombo);
        p.add(new JLabel("Fuel type:")); fuelCombo = new JComboBox<>(new String[]{"petrol","diesel","other"}); p.add(fuelCombo);
        p.add(new JLabel("Manufacture year:")); yearField = new JTextField(); p.add(yearField);
        p.add(new JLabel("Mileage (km):")); mileageField = new JTextField(); p.add(mileageField);
        JButton b = new JButton("Register"); p.add(b);
        add(p);

        b.addActionListener(e -> {
            try {
                String pl = plateField.getText().trim();
                String vt = (String)typeCombo.getSelectedItem();
                String fu = (String)fuelCombo.getSelectedItem();
                int y = Integer.parseInt(yearField.getText().trim());
                int m = Integer.parseInt(mileageField.getText().trim());
                boolean ok = VehicleDAO.registerVehicle(ownerId, pl, vt, fu, y, m);
                if (ok) { Utils.showMsg(this, "OK", "Vehicle registered."); dispose(); } else Utils.showMsg(this, "Error", "Failed.");
            } catch (Exception ex) { Utils.showMsg(this, "Error", "Invalid input."); }
        });
    }
}
