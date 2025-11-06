import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.regex.Pattern;

public class Utils {
    public static final Color PRIMARY_COLOR = new Color(46, 125, 50);
    public static final Color PRIMARY_DARK = new Color(27, 94, 32);
    public static final Color ACCENT_COLOR = new Color(129, 199, 132);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private Utils() {
    }

    public static void showMsg(Component parent, String title, String msg) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static int passwordStrengthScore(String password) {
        if (password == null) {
            return 0;
        }
        int lengthScore = Math.min(40, password.length() * 4);
        int digitScore = Math.min(20, 10 * countMatches(password, "\\d"));
        int upperScore = Math.min(15, 10 * countMatches(password, "[A-Z]"));
        int lowerScore = Math.min(10, 5 * countMatches(password, "[a-z]"));
        int specialScore = Math.min(15, 15 * countMatches(password, "[^A-Za-z0-9]"));
        return Math.min(100, lengthScore + digitScore + upperScore + lowerScore + specialScore);
    }

    public static Color passwordStrengthColor(int score) {
        if (score >= 80) {
            return new Color(46, 204, 113);
        } else if (score >= 60) {
            return new Color(241, 196, 15);
        } else if (score >= 40) {
            return new Color(230, 126, 34);
        }
        return new Color(231, 76, 60);
    }

    public static String passwordStrengthLabel(int score) {
        if (score >= 80) {
            return "Strong";
        } else if (score >= 60) {
            return "Good";
        } else if (score >= 40) {
            return "Fair";
        }
        return "Weak";
    }

    private static int countMatches(String value, String regex) {
        return (int) Pattern.compile(regex).matcher(value).results().count();
    }

    public static void stylePrimaryButton(AbstractButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }

    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1, true),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );
        panel.setBorder(border);
        return panel;
    }
}
