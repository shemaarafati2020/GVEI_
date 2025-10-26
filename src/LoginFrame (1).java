import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField pwdField;

    public LoginFrame() {
        setTitle("GVEI - Login");
        setSize(420,220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(4,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JLabel("Email:"));
        emailField = new JTextField();
        p.add(emailField);
        p.add(new JLabel("Password:"));
        pwdField = new JPasswordField();
        p.add(pwdField);

        JButton bLogin = new JButton("Login");
        JButton bReg = new JButton("Register");
        p.add(bLogin);
        p.add(bReg);

        add(p);

        bLogin.addActionListener(e -> {
            String em = emailField.getText().trim();
            String pw = new String(pwdField.getPassword()).trim();
            if (em.isEmpty() || pw.isEmpty()) { Utils.showMsg(this, "Error", "Enter email and password"); return; }
            Map<String,Object> user = UserDAO.login(em, pw);
            if (user == null) { Utils.showMsg(this, "Error", "Invalid credentials"); return; }
            int uid = (Integer)user.get("user_id");
            String name = (String)user.get("name");
            String role = (String)user.get("role");
            dispose();
            if ("admin".equals(role)) new AdminDashboard(uid, name).setVisible(true);
            else new CitizenDashboard(uid, name).setVisible(true);
        });

        bReg.addActionListener(e -> new RegisterFrame().setVisible(true));
    }
}
