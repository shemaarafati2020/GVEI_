import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField pwdField;
    private JComboBox<String> roleCombo;

    public RegisterFrame() {
        setTitle("Register");
        setSize(420,300);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(5,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JLabel("Full name:")); nameField = new JTextField(); p.add(nameField);
        p.add(new JLabel("Email:")); emailField = new JTextField(); p.add(emailField);
        p.add(new JLabel("Password:")); pwdField = new JPasswordField(); p.add(pwdField);
        p.add(new JLabel("Role:")); roleCombo = new JComboBox<>(new String[]{"citizen","admin"}); p.add(roleCombo);

        JButton bCreate = new JButton("Create"); p.add(bCreate);
        add(p);

        bCreate.addActionListener(e -> {
            String n = nameField.getText().trim();
            String em = emailField.getText().trim();
            String pw = new String(pwdField.getPassword()).trim();
            String r = (String)roleCombo.getSelectedItem();
            if (n.isEmpty() || em.isEmpty() || pw.isEmpty()) { Utils.showMsg(this, "Error", "All fields required"); return; }
            boolean ok = UserDAO.register(n, em, pw, r);
            if (ok) { Utils.showMsg(this, "OK", "User registered"); dispose(); } else Utils.showMsg(this, "Error", "Registration failed");
        });
    }
}
