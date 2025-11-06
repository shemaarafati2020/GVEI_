import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.prefs.Preferences;

public class LoginFrame extends JFrame {
    private static final String PREF_REMEMBER_EMAIL = "remember_email";

    private final JTextField emailField;
    private final JPasswordField pwdField;
    private final JCheckBox rememberEmail;
    private final JLabel feedbackLabel;
    private final Preferences preferences;
    private char defaultEchoChar;

    public LoginFrame() {
        setTitle("GVEI - Login");
        setSize(520, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        preferences = Preferences.userNodeForPackage(LoginFrame.class);

        JPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());
        background.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel card = Utils.createCardPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel title = new JLabel("Welcome back");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(Utils.PRIMARY_DARK);
        gbc.gridy = 0;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to continue to the GVEI portal");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));
        subtitle.setForeground(new Color(97, 97, 97));
        gbc.gridy = 1;
        card.add(subtitle, gbc);

        gbc.gridy = 2;
        JPanel emailPanel = createLabeledField("Email address", emailField = new JTextField(24));
        card.add(emailPanel, gbc);

        gbc.gridy = 3;
        pwdField = new JPasswordField(24);
        defaultEchoChar = pwdField.getEchoChar();
        JPanel pwdPanel = createPasswordPanel();
        card.add(pwdPanel, gbc);

        gbc.gridy = 4;
        rememberEmail = new JCheckBox("Remember my email on this device");
        rememberEmail.setOpaque(false);
        rememberEmail.setForeground(new Color(90, 90, 90));
        card.add(rememberEmail, gbc);

        gbc.gridy = 5;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setForeground(new Color(198, 40, 40));
        card.add(feedbackLabel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(12, 0, 0, 0);
        JButton loginBtn = new JButton("Sign in");
        Utils.stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(this::handleLogin);
        card.add(loginBtn, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        JButton registerBtn = new JButton("Create a new account");
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(Utils.PRIMARY_DARK);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createLineBorder(Utils.PRIMARY_COLOR));
        registerBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
        card.add(registerBtn, gbc);

        GridBagConstraints containerConstraints = new GridBagConstraints();
        containerConstraints.gridx = 0;
        containerConstraints.gridy = 0;
        containerConstraints.fill = GridBagConstraints.NONE;
        background.add(card, containerConstraints);

        add(background);

        loginBtn.registerKeyboardAction(this::handleLogin, KeyStroke.getKeyStroke("ENTER"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        pwdField.addActionListener(this::handleLogin);

        loadRememberedEmail();
    }

    private void handleLogin(ActionEvent event) {
        feedbackLabel.setText(" ");
        String email = emailField.getText().trim();
        String password = new String(pwdField.getPassword()).trim();

        if (!Utils.isValidEmail(email)) {
            feedbackLabel.setText("Please provide a valid email address.");
            emailField.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            feedbackLabel.setText("Password cannot be empty.");
            pwdField.requestFocusInWindow();
            return;
        }

        Map<String, Object> user = UserDAO.login(email, password);
        if (user == null) {
            feedbackLabel.setText("We couldn't find a matching account.");
            return;
        }

        if (rememberEmail.isSelected()) {
            preferences.put(PREF_REMEMBER_EMAIL, email);
        } else {
            preferences.remove(PREF_REMEMBER_EMAIL);
        }

        int uid = (Integer) user.get("user_id");
        String name = (String) user.get("name");
        String role = (String) user.get("role");
        dispose();
        if ("admin".equals(role)) {
            new AdminDashboard(uid, name).setVisible(true);
        } else {
            new CitizenDashboard(uid, name).setVisible(true);
        }
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel("Password");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setForeground(new Color(66, 66, 66));

        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.add(pwdField, BorderLayout.CENTER);

        JCheckBox showPassword = new JCheckBox("Show");
        showPassword.setOpaque(false);
        showPassword.setForeground(new Color(90, 90, 90));
        showPassword.addActionListener(e -> pwdField.setEchoChar(showPassword.isSelected() ? 0 : defaultEchoChar));
        fieldPanel.add(showPassword, BorderLayout.EAST);

        panel.add(label, BorderLayout.NORTH);
        panel.add(fieldPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setForeground(new Color(66, 66, 66));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void loadRememberedEmail() {
        String savedEmail = preferences.get(PREF_REMEMBER_EMAIL, null);
        if (savedEmail != null && !savedEmail.isBlank()) {
            emailField.setText(savedEmail);
            rememberEmail.setSelected(true);
            pwdField.requestFocusInWindow();
        }
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, Utils.PRIMARY_COLOR, getWidth(), getHeight(), Utils.ACCENT_COLOR));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
