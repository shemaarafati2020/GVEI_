import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField pwdField;
    private JPasswordField confirmPwdField;
    private JComboBox<String> roleCombo;
    private final JProgressBar strengthBar;
    private final JLabel strengthLabel;

    public RegisterFrame() {
        setTitle("Create an account");
        setSize(520, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel background = new LoginFrame.GradientPanel();
        background.setLayout(new GridBagLayout());
        background.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel card = Utils.createCardPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel title = new JLabel("Join the GVEI community");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(Utils.PRIMARY_DARK);
        gbc.gridy = 0;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Create an account to register vehicles and manage exchange offers.");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));
        subtitle.setForeground(new Color(97, 97, 97));
        gbc.gridy = 1;
        card.add(subtitle, gbc);

        gbc.gridy = 2;
        card.add(createLabeledField("Full name", nameField = new JTextField(24)), gbc);

        gbc.gridy = 3;
        card.add(createLabeledField("Email address", emailField = new JTextField(24)), gbc);

        gbc.gridy = 4;
        pwdField = new JPasswordField(24);
        card.add(createPasswordField("Password", pwdField), gbc);

        gbc.gridy = 5;
        confirmPwdField = new JPasswordField(24);
        card.add(createPasswordField("Confirm password", confirmPwdField), gbc);

        gbc.gridy = 6;
        JPanel strengthPanel = new JPanel(new BorderLayout(6, 0));
        strengthPanel.setOpaque(false);
        strengthLabel = new JLabel("Password strength: Weak");
        strengthLabel.setForeground(new Color(66, 66, 66));
        strengthPanel.add(strengthLabel, BorderLayout.NORTH);
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(false);
        strengthBar.setValue(0);
        strengthBar.setPreferredSize(new Dimension(220, 10));
        strengthBar.setForeground(Utils.passwordStrengthColor(0));
        strengthBar.setBackground(new Color(224, 224, 224));
        strengthPanel.add(strengthBar, BorderLayout.CENTER);
        card.add(strengthPanel, gbc);

        gbc.gridy = 7;
        JPanel rolePanel = new JPanel(new BorderLayout(6, 0));
        rolePanel.setOpaque(false);
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(roleLabel.getFont().deriveFont(Font.BOLD));
        roleLabel.setForeground(new Color(66, 66, 66));
        rolePanel.add(roleLabel, BorderLayout.NORTH);
        roleCombo = new JComboBox<>(new String[]{"citizen", "admin"});
        rolePanel.add(roleCombo, BorderLayout.CENTER);
        card.add(rolePanel, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(16, 0, 0, 0);
        JButton createBtn = new JButton("Create account");
        Utils.stylePrimaryButton(createBtn);
        card.add(createBtn, gbc);

        GridBagConstraints containerConstraints = new GridBagConstraints();
        containerConstraints.gridx = 0;
        containerConstraints.gridy = 0;
        background.add(card, containerConstraints);

        add(background);

        createBtn.addActionListener(e -> registerUser());
        pwdField.getDocument().addDocumentListener(passwordListener());
        confirmPwdField.getDocument().addDocumentListener(passwordListener());
        updateStrength();
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(pwdField.getPassword()).trim();
        String confirmPassword = new String(confirmPwdField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Utils.showError(this, "All fields are required.");
            return;
        }

        if (!Utils.isValidEmail(email)) {
            Utils.showError(this, "Please enter a valid email address.");
            emailField.requestFocusInWindow();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Utils.showError(this, "Passwords do not match.");
            confirmPwdField.requestFocusInWindow();
            return;
        }

        int strength = Utils.passwordStrengthScore(password);
        if (strength < 50) {
            Utils.showError(this, "Please choose a stronger password (at least 8 characters including numbers and symbols).");
            pwdField.requestFocusInWindow();
            return;
        }

        boolean ok = UserDAO.register(name, email, password, role);
        if (ok) {
            Utils.showInfo(this, "Account created successfully! You can now sign in.");
            dispose();
        } else {
            Utils.showError(this, "Registration failed. Please try again.");
        }
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

    private JPanel createPasswordField(String labelText, JPasswordField field) {
        JPanel panel = createLabeledField(labelText, field);
        return panel;
    }

    private DocumentListener passwordListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStrength();
            }
        };
    }

    private void updateStrength() {
        String password = new String(pwdField.getPassword());
        int score = Utils.passwordStrengthScore(password);
        strengthBar.setValue(score);
        strengthBar.setForeground(Utils.passwordStrengthColor(score));
        strengthLabel.setText("Password strength: " + Utils.passwordStrengthLabel(score));
    }
}
