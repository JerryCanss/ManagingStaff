package acss.view;

import acss.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegisterFrame extends JFrame {
    private JTextField fullNameField, staffIdField, emailField, contactField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public RegisterFrame() {
        setTitle("Register New Staff");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 600);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(36, 36, 36, 36)
        ));
        card.setMaximumSize(new Dimension(320, 500));
        card.setPreferredSize(new Dimension(320, 500));

        JLabel titleLabel = new JLabel("Register New Staff", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        card.add(titleLabel);

        // Full Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        fullNameField = new JTextField(18);
        fullNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(fullNameField);
        card.add(Box.createVerticalStrut(14));

        // Staff ID
        JLabel staffIdLabel = new JLabel("Staff ID:");
        staffIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        staffIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(staffIdLabel);
        staffIdField = new JTextField(18);
        staffIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        staffIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(staffIdField);
        card.add(Box.createVerticalStrut(14));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLabel);
        passwordField = new JPasswordField(18);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));

        // Email
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emailLabel);
        emailField = new JTextField(18);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        // Contact Number
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(contactLabel);
        contactField = new JTextField(18);
        contactField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        contactField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(contactField);
        card.add(Box.createVerticalStrut(18));

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setForeground(new Color(200, 0, 0));
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(10));

        // Register and Back buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        card.add(btnPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);

        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private void handleRegister() {
        String fullName = fullNameField.getText();
        String staffId = staffIdField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String contact = contactField.getText();
        if (fullName.isEmpty() || staffId.isEmpty() || password.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            messageLabel.setText("Please fill all fields.");
            return;
        }
        if (UserStore.usernameExists(staffId)) {
            messageLabel.setText("Staff ID already exists.");
            return;
        }
        if (UserStore.emailExists(email)) {
            messageLabel.setText("Email already exists.");
            return;
        }
        User user = new User(fullName, staffId, password, email, contact, "", UserRole.MANAGING_STAFF);
        UserStore.addUser(user);
        // Append to user_history.txt
        try (FileWriter fw = new FileWriter("user_history.txt", true)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = LocalDateTime.now().format(dtf);
            fw.write(fullName + "|" + staffId + "|" + email + "|" + contact + "|" + now + "\n");
        } catch (IOException ex) {
            // ignore
        }
        messageLabel.setForeground(new Color(0, 128, 0));
        messageLabel.setText("Registration successful! You can now log in.");
    }
} 