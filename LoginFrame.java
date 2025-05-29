package acss.view;

import acss.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private JLabel messageLabel;
    private static final String REMEMBER_FILE = "rememberme.dat";

    public LoginFrame() {
        setTitle("APU Car Sales System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 500);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Card panel for the form
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(36, 36, 36, 36)
        ));
        card.setMaximumSize(new Dimension(320, 400));
        card.setPreferredSize(new Dimension(320, 400));

        // Title
        JLabel titleLabel = new JLabel("APU Car Sales System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        card.add(titleLabel);

        // Username
        JLabel userLabel = new JLabel("Username or Email:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.add(userLabel);
        usernameField = new JTextField(18);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(18));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.add(passLabel);
        passwordField = new JPasswordField(18);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        // Remember Me and Login button row
        JPanel loginRow = new JPanel(new BorderLayout());
        loginRow.setOpaque(false);
        loginRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rememberPanel.setOpaque(false);
        rememberMeCheckBox = new JCheckBox("Remember Me");
        rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rememberMeCheckBox.setBackground(Color.WHITE);
        rememberMeCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rememberPanel.add(rememberMeCheckBox);
        loginRow.add(rememberPanel, BorderLayout.WEST);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 36, 8, 36));
        JPanel loginBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginBtnPanel.setOpaque(false);
        loginBtnPanel.add(loginButton);
        loginRow.add(loginBtnPanel, BorderLayout.EAST);

        card.add(loginRow);
        card.add(Box.createVerticalStrut(24));

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setForeground(new Color(200, 0, 0));
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.add(messageLabel);

        // Add card to center
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);

        // Sign Up button at bottom left
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signUpButton.setBorderPainted(false);
        signUpButton.setContentAreaFilled(false);
        signUpButton.setForeground(new Color(0, 120, 215));
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(signUpButton, BorderLayout.WEST);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(bottomPanel, gbc);

        // Listeners
        loginButton.addActionListener(e -> handleLogin());
        signUpButton.addActionListener(e -> {
            this.dispose();
            new RegisterFrame();
        });

        loadRememberedUsername();
        setVisible(true);
    }

    private void handleLogin() {
        String usernameOrEmail = usernameField.getText();
        String password = new String(passwordField.getPassword());
        User user = UserStore.findUser(usernameOrEmail, password);
        if (user != null) {
            messageLabel.setText("");
            if (rememberMeCheckBox.isSelected()) {
                saveRememberedUsername(usernameOrEmail);
            } else {
                clearRememberedUsername();
            }
            this.dispose();
            new DashboardFrame(user); // For now, always open managing staff dashboard
        } else {
            messageLabel.setText("Invalid username/email or password.");
        }
    }

    private void saveRememberedUsername(String username) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REMEMBER_FILE))) {
            oos.writeObject(username);
        } catch (IOException e) {
            // ignore
        }
    }

    private void loadRememberedUsername() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(REMEMBER_FILE))) {
            String remembered = (String) ois.readObject();
            usernameField.setText(remembered);
            rememberMeCheckBox.setSelected(true);
        } catch (Exception e) {
            // ignore
        }
    }

    private void clearRememberedUsername() {
        File f = new File(REMEMBER_FILE);
        if (f.exists()) f.delete();
    }
} 