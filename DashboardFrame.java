package acss.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import acss.model.User;
import acss.model.UserRole;

public class DashboardFrame extends JFrame {
    private JTable staffTable, customerTable, carTable, salesmanTable;
    private DefaultTableModel staffTableModel, customerTableModel, carTableModel, salesmanTableModel;
    private JPanel profilePanel;
    private JLabel profileMsgLabel;
    private JButton historyButton;
    private Object loggedInUser;

    private static final String HISTORY_FILE = "user_history.txt";
    private static final String STAFF_FILE = "staff.txt";
    private static final String SALESMAN_FILE = "salesman.txt";
    private static final String CUSTOMER_FILE = "customer.txt";
    private static final String CAR_FILE = "car.txt";
    private static final String FEEDBACK_FILE = "feedback.txt";
    private static final String STAFF_EXTRA_FILE = "staff_extra.txt";
    private static final String SALESMAN_EXTRA_FILE = "salesman_extra.txt";
    private static final String CUSTOMER_EXTRA_FILE = "customer_extra.txt";
    private static final String CAR_EXTRA_FILE = "car_extra.txt";

    // Modern UI constants
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color PANEL_BG = new Color(255, 255, 255);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);

    // --- Add maps to store extra fields for each table row ---
    private java.util.Map<String, String[]> staffExtraFields = new java.util.HashMap<>();
    private java.util.Map<String, String[]> salesmanExtraFields = new java.util.HashMap<>();
    private java.util.Map<String, String[]> customerExtraFields = new java.util.HashMap<>();
    private java.util.Map<String, String[]> carExtraFields = new java.util.HashMap<>();

    public DashboardFrame(Object user) {
        this.loggedInUser = user;
        setTitle("APU Car Sales System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        tabbedPane.addTab("Managing Staff", createStaffPanel());
        tabbedPane.addTab("Salesman", createSalesmanPanel());
        tabbedPane.addTab("Customer", createCustomerPanel());
        tabbedPane.addTab("Car", createCarPanel());
        tabbedPane.addTab("Analysis & Reports", createAnalysisPanel());
        tabbedPane.addTab("User Profile", createUserProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome to the Dashboard!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame();
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.SOUTH);

        // Set background and font for main frame and tabbed pane
        getContentPane().setBackground(BG_COLOR);
        tabbedPane.setBackground(BG_COLOR);

        setVisible(true);
    }

    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add ID");
        JButton deleteBtn = new JButton("Delete");
        JTextField searchField = new JTextField(8);
        JButton searchBtn = new JButton("Search");
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        topPanel.add(new JLabel("Search by ID:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        staffTableModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"Staff ID", "Full Name", "Email", "Phone Number", "Role", "Assigned Branch", "More"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        staffTable = new JTable(staffTableModel);
        staffTable.getColumnModel().getColumn(6).setCellRenderer(new IdWithMoreRenderer());
        staffTable.getColumnModel().getColumn(6).setCellEditor(new IdWithMoreEditor(this, staffTableModel, "staff"));
        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        loadStaffTable();
        addBtn.addActionListener(e -> openAddStaffDialog());
        deleteBtn.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                staffTableModel.removeRow(row);
                saveTableToFile(staffTableModel, STAFF_FILE, 7);
            }
        });
        searchBtn.addActionListener(e -> {
            String searchId = searchField.getText().trim();
            for (int i = 0; i < staffTableModel.getRowCount(); i++) {
                if (searchId.equalsIgnoreCase((String) staffTableModel.getValueAt(i, 0))) {
                    staffTable.setRowSelectionInterval(i, i);
                    staffTable.scrollRectToVisible(staffTable.getCellRect(i, 0, true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "ID not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
        });
        return panel;
    }

    private void openAddStaffDialog() {
        JDialog dialog = new JDialog(this, "Add Staff", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        JComboBox<String> prefixCombo = new JComboBox<>(new String[]{"M"});
        JTextField staffIdField = new JTextField(4); // Only 4 digits
        idPanel.add(prefixCombo);
        idPanel.add(Box.createHorizontalStrut(6));
        idPanel.add(staffIdField);
        JLabel msgLabel = new JLabel("");
        msgLabel.setForeground(Color.RED);
        card.add(new JLabel("Staff ID (M + 4 digits):")); card.add(idPanel);
        card.add(Box.createVerticalStrut(12));
        JTextField fullNameField = new JTextField(18);
        JTextField emailField = new JTextField(18);
        JTextField phoneField = new JTextField(18);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"MANAGING_STAFF"});
        JTextField branchField = new JTextField(18);
        card.add(new JLabel("Full Name:")); card.add(fullNameField);
        card.add(new JLabel("Email:")); card.add(emailField);
        card.add(new JLabel("Phone Number:")); card.add(phoneField);
        card.add(new JLabel("Role:")); card.add(roleCombo);
        card.add(new JLabel("Assigned Branch:")); card.add(branchField);
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(12));
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton returnBtn = new JButton("Return");
        btnPanel.add(addBtn); btnPanel.add(returnBtn);
        card.add(btnPanel);
        addBtn.addActionListener(e -> {
            String id = prefixCombo.getSelectedItem() + staffIdField.getText().trim();
            if (!id.matches("M\\d{4}")) {
                msgLabel.setText("ID must be M followed by 4 digits.");
                return;
            }
            boolean duplicate = false;
            for (int i = 0; i < staffTableModel.getRowCount(); i++) {
                if (id.equals(staffTableModel.getValueAt(i, 0))) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                msgLabel.setText("Staff ID already exists!");
                return;
            }
            staffTableModel.addRow(new Object[]{
                id, fullNameField.getText(), emailField.getText(), phoneField.getText(),
                roleCombo.getSelectedItem(), branchField.getText(), ""
            });
            saveTableToFile(staffTableModel, STAFF_FILE, 7);
            dialog.dispose();
        });
        returnBtn.addActionListener(e -> dialog.dispose());
        dialog.getContentPane().add(card);
        dialog.setVisible(true);
    }

    private JPanel createSalesmanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add ID");
        JButton deleteBtn = new JButton("Delete");
        JTextField searchField = new JTextField(8);
        JButton searchBtn = new JButton("Search");
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        topPanel.add(new JLabel("Search by ID:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        salesmanTableModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"Staff ID", "Full Name", "Email", "Phone Number", "Role", "Assigned Branch", "More"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        salesmanTable = new JTable(salesmanTableModel);
        salesmanTable.getColumnModel().getColumn(6).setCellRenderer(new IdWithMoreRenderer());
        salesmanTable.getColumnModel().getColumn(6).setCellEditor(new IdWithMoreEditor(this, salesmanTableModel, "salesman"));
        panel.add(new JScrollPane(salesmanTable), BorderLayout.CENTER);
        loadSalesmanTable(salesmanTableModel);
        addBtn.addActionListener(e -> openAddSalesmanDialog(salesmanTableModel));
        deleteBtn.addActionListener(e -> {
            int row = salesmanTable.getSelectedRow();
            if (row != -1) {
                salesmanTableModel.removeRow(row);
                saveTableToFile(salesmanTableModel, SALESMAN_FILE, 7);
            }
        });
        searchBtn.addActionListener(e -> {
            String searchId = searchField.getText().trim();
            for (int i = 0; i < salesmanTableModel.getRowCount(); i++) {
                if (searchId.equalsIgnoreCase((String) salesmanTableModel.getValueAt(i, 0))) {
                    salesmanTable.setRowSelectionInterval(i, i);
                    salesmanTable.scrollRectToVisible(salesmanTable.getCellRect(i, 0, true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "ID not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
        });
        return panel;
    }

    private void openAddSalesmanDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add Salesman", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        JComboBox<String> prefixCombo = new JComboBox<>(new String[]{"S"});
        JTextField staffIdField = new JTextField(4); // Only 4 digits
        idPanel.add(prefixCombo);
        idPanel.add(Box.createHorizontalStrut(6));
        idPanel.add(staffIdField);
        JLabel msgLabel = new JLabel("");
        msgLabel.setForeground(Color.RED);
        card.add(new JLabel("Staff ID (S + 4 digits):")); card.add(idPanel);
        card.add(Box.createVerticalStrut(12));
        JTextField fullNameField = new JTextField(18);
        JTextField emailField = new JTextField(18);
        JTextField phoneField = new JTextField(18);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"SALESMAN"});
        JTextField branchField = new JTextField(18);
        card.add(new JLabel("Full Name:")); card.add(fullNameField);
        card.add(new JLabel("Email:")); card.add(emailField);
        card.add(new JLabel("Phone Number:")); card.add(phoneField);
        card.add(new JLabel("Role:")); card.add(roleCombo);
        card.add(new JLabel("Assigned Branch:")); card.add(branchField);
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(12));
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton returnBtn = new JButton("Return");
        btnPanel.add(addBtn); btnPanel.add(returnBtn);
        card.add(btnPanel);
        addBtn.addActionListener(e -> {
            String id = prefixCombo.getSelectedItem() + staffIdField.getText().trim();
            if (!id.matches("S\\d{4}")) {
                msgLabel.setText("ID must be S followed by 4 digits.");
                return;
            }
            boolean duplicate = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (id.equals(model.getValueAt(i, 0))) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                msgLabel.setText("Staff ID already exists!");
                return;
            }
            model.addRow(new Object[]{
                id, fullNameField.getText(), emailField.getText(), phoneField.getText(),
                roleCombo.getSelectedItem(), branchField.getText(), ""
            });
            saveTableToFile(model, SALESMAN_FILE, 7);
            dialog.dispose();
        });
        returnBtn.addActionListener(e -> dialog.dispose());
        dialog.getContentPane().add(card);
        dialog.setVisible(true);
    }

    private void loadSalesmanTable(DefaultTableModel model) {
        loadTableFromFile(model, SALESMAN_FILE, 7);
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add ID");
        JButton deleteBtn = new JButton("Delete");
        JTextField searchField = new JTextField(8);
        JButton searchBtn = new JButton("Search");
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        topPanel.add(new JLabel("Search by ID:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        customerTableModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"Customer ID", "Full Name", "Email", "Phone Number", "Approval Status", "More"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.getColumnModel().getColumn(5).setCellRenderer(new IdWithMoreRenderer());
        customerTable.getColumnModel().getColumn(5).setCellEditor(new IdWithMoreEditor(this, customerTableModel, "customer"));
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        loadCustomerTable();
        addBtn.addActionListener(e -> openAddCustomerDialog());
        deleteBtn.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row != -1) {
                String id = (String) customerTableModel.getValueAt(row, 0);
                String name = (String) customerTableModel.getValueAt(row, 1);
                customerTableModel.removeRow(row);
                logHistory("Remove", "Customer", id, name, "Customer removed");
                saveTableToFile(customerTableModel, CUSTOMER_FILE, 6);
            }
        });
        searchBtn.addActionListener(e -> {
            String searchId = searchField.getText().trim();
            for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                if (searchId.equalsIgnoreCase((String) customerTableModel.getValueAt(i, 0))) {
                    customerTable.setRowSelectionInterval(i, i);
                    customerTable.scrollRectToVisible(customerTable.getCellRect(i, 0, true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "ID not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
        });
        return panel;
    }

    private void openAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add Customer", true);
        dialog.setSize(350, 350);
        dialog.setLocationRelativeTo(this);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        JComboBox<String> prefixCombo = new JComboBox<>(new String[]{"C"});
        JTextField customerIdField = new JTextField(12);
        idPanel.add(prefixCombo);
        idPanel.add(Box.createHorizontalStrut(6));
        idPanel.add(customerIdField);
        JLabel msgLabel = new JLabel("");
        msgLabel.setForeground(Color.RED);
        card.add(new JLabel("Customer ID:")); card.add(idPanel);
        card.add(Box.createVerticalStrut(12));
        JTextField fullNameField = new JTextField(18);
        JTextField emailField = new JTextField(18);
        JTextField phoneField = new JTextField(18);
        JTextField approvalField = new JTextField(18);
        card.add(new JLabel("Full Name:")); card.add(fullNameField);
        card.add(new JLabel("Email:")); card.add(emailField);
        card.add(new JLabel("Phone Number:")); card.add(phoneField);
        card.add(new JLabel("Approval Status:")); card.add(approvalField);
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(12));
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton returnBtn = new JButton("Return");
        btnPanel.add(addBtn); btnPanel.add(returnBtn);
        card.add(btnPanel);
        addBtn.addActionListener(e -> {
            String id = prefixCombo.getSelectedItem() + customerIdField.getText().trim();
            boolean duplicate = false;
            for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                if (id.equals(customerTableModel.getValueAt(i, 0))) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                msgLabel.setText("Customer ID already exists!");
                return;
            }
            customerTableModel.addRow(new Object[]{
                id, fullNameField.getText(), emailField.getText(), phoneField.getText(), approvalField.getText(), ""
            });
            logHistory("Add", "Customer", id, fullNameField.getText(), "Customer added");
            saveTableToFile(customerTableModel, CUSTOMER_FILE, 6);
            dialog.dispose();
        });
        returnBtn.addActionListener(e -> dialog.dispose());
        dialog.getContentPane().add(card);
        dialog.setVisible(true);
    }

    private JPanel createCarPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add ID");
        JButton deleteBtn = new JButton("Delete");
        JTextField searchField = new JTextField(8);
        JButton searchBtn = new JButton("Search");
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        topPanel.add(new JLabel("Search by ID:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        carTableModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"Car ID", "Brand", "Model", "Manufactured", "Price", "Availability Status", "More"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        carTable = new JTable(carTableModel);
        carTable.getColumnModel().getColumn(6).setCellRenderer(new IdWithMoreRenderer());
        carTable.getColumnModel().getColumn(6).setCellEditor(new IdWithMoreEditor(this, carTableModel, "car"));
        panel.add(new JScrollPane(carTable), BorderLayout.CENTER);
        loadCarTable();
        addBtn.addActionListener(e -> openAddCarDialog());
        deleteBtn.addActionListener(e -> {
            int row = carTable.getSelectedRow();
            if (row != -1) {
                String id = (String) carTableModel.getValueAt(row, 0);
                String name = (String) carTableModel.getValueAt(row, 2);
                carTableModel.removeRow(row);
                logHistory("Remove", "Car", id, name, "Car removed");
                saveTableToFile(carTableModel, CAR_FILE, 7);
            }
        });
        searchBtn.addActionListener(e -> {
            String searchId = searchField.getText().trim();
            for (int i = 0; i < carTableModel.getRowCount(); i++) {
                if (searchId.equalsIgnoreCase((String) carTableModel.getValueAt(i, 0))) {
                    carTable.setRowSelectionInterval(i, i);
                    carTable.scrollRectToVisible(carTable.getCellRect(i, 0, true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "ID not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
        });
        return panel;
    }

    private void openAddCarDialog() {
        JDialog dialog = new JDialog(this, "Add Car", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        JComboBox<String> prefixCombo = new JComboBox<>(new String[]{"CAR"});
        JTextField carIdField = new JTextField(12);
        idPanel.add(prefixCombo);
        idPanel.add(Box.createHorizontalStrut(6));
        idPanel.add(carIdField);
        JLabel msgLabel = new JLabel("");
        msgLabel.setForeground(Color.RED);
        card.add(new JLabel("Car ID:")); card.add(idPanel);
        card.add(Box.createVerticalStrut(12));
        JTextField brandField = new JTextField(18);
        JTextField modelField = new JTextField(18);
        JTextField manufacturedField = new JTextField(18);
        JTextField priceField = new JTextField(18);
        JTextField statusField = new JTextField(18);
        card.add(new JLabel("Brand:")); card.add(brandField);
        card.add(new JLabel("Model:")); card.add(modelField);
        card.add(new JLabel("Manufactured Date:")); card.add(manufacturedField);
        card.add(new JLabel("Price:")); card.add(priceField);
        card.add(new JLabel("Availability Status:")); card.add(statusField);
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(12));
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton returnBtn = new JButton("Return");
        btnPanel.add(addBtn); btnPanel.add(returnBtn);
        card.add(btnPanel);
        addBtn.addActionListener(e -> {
            String id = prefixCombo.getSelectedItem() + carIdField.getText().trim();
            boolean duplicate = false;
            for (int i = 0; i < carTableModel.getRowCount(); i++) {
                if (id.equals(carTableModel.getValueAt(i, 0))) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                msgLabel.setText("Car ID already exists!");
                return;
            }
            carTableModel.addRow(new Object[]{
                id, brandField.getText(), modelField.getText(), manufacturedField.getText(), priceField.getText(), statusField.getText(), ""
            });
            logHistory("Add", "Car", id, modelField.getText(), "Car added");
            saveTableToFile(carTableModel, CAR_FILE, 7);
            dialog.dispose();
        });
        returnBtn.addActionListener(e -> dialog.dispose());
        dialog.getContentPane().add(card);
        dialog.setVisible(true);
    }

    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Analysis Section
        JPanel analysisPanel = new JPanel();
        analysisPanel.setLayout(new BoxLayout(analysisPanel, BoxLayout.Y_AXIS));
        analysisPanel.setBorder(BorderFactory.createTitledBorder("Analysis"));
        analysisPanel.setBackground(Color.WHITE);
        analysisPanel.add(createAnalysisTableButton("Managing Staff"));
        analysisPanel.add(Box.createVerticalStrut(8));
        analysisPanel.add(createAnalysisTableButton("Salesman"));
        analysisPanel.add(Box.createVerticalStrut(8));
        analysisPanel.add(createAnalysisTableButton("Customer"));

        // Reports Section
        JPanel reportsPanel = new JPanel();
        reportsPanel.setLayout(new BoxLayout(reportsPanel, BoxLayout.Y_AXIS));
        reportsPanel.setBorder(BorderFactory.createTitledBorder("Reports"));
        reportsPanel.setBackground(Color.WHITE);
        reportsPanel.add(createAnalysisTableButton("Sales Report"));
        reportsPanel.add(Box.createVerticalStrut(8));
        reportsPanel.add(createAnalysisTableButton("Car Inventory Report"));
        reportsPanel.add(Box.createVerticalStrut(8));
        reportsPanel.add(createAnalysisTableButton("Customer Report"));
        reportsPanel.add(Box.createVerticalStrut(8));
        reportsPanel.add(createAnalysisTableButton("Feedback Reports"));

        panel.add(analysisPanel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(reportsPanel);
        panel.add(Box.createVerticalStrut(16));

        return panel;
    }

    private JPanel createAnalysisTableButton(String title) {
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(Color.WHITE);
        subPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.addActionListener(e -> showAnalysisTableDialog(title));
        subPanel.add(btn);
        return subPanel;
    }

    private void showAnalysisTableDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.getContentPane().setBackground(PANEL_BG);
        dialog.setMinimumSize(new Dimension(400, 300));
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(10, 10));
        JTable table;
        DefaultTableModel model;
        if (title.equals("Managing Staff")) {
            String[] columns = {"Staff ID", "Full Name", "Role", "Department", "Salary"};
            model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < staffTableModel.getRowCount(); i++) {
                String id = (String) staffTableModel.getValueAt(i, 0);
                String name = (String) staffTableModel.getValueAt(i, 1);
                String role = (String) staffTableModel.getValueAt(i, 4);
                String department = "";
                String salary = "";
                String[] extras = staffExtraFields.get(id);
                if (extras != null) {
                    department = extras.length > 2 ? extras[2] : "";
                    salary = extras.length > 4 ? extras[4] : "";
                }
                model.addRow(new Object[]{id, name, role, department, salary});
            }
            table = new JTable(model);
            table.getModel().addTableModelListener(e -> {
                int row = e.getFirstRow();
                String id = (String) model.getValueAt(row, 0);
                String department = (String) model.getValueAt(row, 3);
                String salary = (String) model.getValueAt(row, 4);
                String[] extras = staffExtraFields.getOrDefault(id, new String[5]);
                if (extras.length < 5) {
                    String[] newExtras = new String[5];
                    System.arraycopy(extras, 0, newExtras, 0, extras.length);
                    extras = newExtras;
                }
                extras[2] = department;
                extras[4] = salary;
                staffExtraFields.put(id, extras);
                logHistory("Edit", "Staff", id, (String) model.getValueAt(row, 1), "Department/Salary updated in Analysis");
                // Persist changes
                saveTableToFile(staffTableModel, STAFF_FILE, 7);
            });
        } else if (title.equals("Salesman")) {
            String[] columns = {"Staff ID", "Full Name", "Role", "Department", "Salary"};
            model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < salesmanTableModel.getRowCount(); i++) {
                String id = (String) salesmanTableModel.getValueAt(i, 0);
                String name = (String) salesmanTableModel.getValueAt(i, 1);
                String role = (String) salesmanTableModel.getValueAt(i, 4);
                String department = "";
                String salary = "";
                String[] extras = salesmanExtraFields.get(id);
                if (extras != null) {
                    department = extras.length > 3 ? extras[3] : "";
                    salary = extras.length > 5 ? extras[5] : "";
                }
                model.addRow(new Object[]{id, name, role, department, salary});
            }
            table = new JTable(model);
            table.getModel().addTableModelListener(e -> {
                int row = e.getFirstRow();
                String id = (String) model.getValueAt(row, 0);
                String department = (String) model.getValueAt(row, 3);
                String salary = (String) model.getValueAt(row, 4);
                String[] extras = salesmanExtraFields.getOrDefault(id, new String[6]);
                if (extras.length < 6) {
                    String[] newExtras = new String[6];
                    System.arraycopy(extras, 0, newExtras, 0, extras.length);
                    extras = newExtras;
                }
                extras[3] = department;
                extras[5] = salary;
                salesmanExtraFields.put(id, extras);
                logHistory("Edit", "Salesman", id, (String) model.getValueAt(row, 1), "Department/Salary updated in Analysis");
                // Persist changes
                saveTableToFile(salesmanTableModel, SALESMAN_FILE, 7);
            });
        } else if (title.equals("Customer")) {
            String[] columns = {"Customer ID", "Full Name", "Membership Registered Date", "Birth of Date"};
            model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                String id = (String) customerTableModel.getValueAt(i, 0);
                String name = (String) customerTableModel.getValueAt(i, 1);
                String regDate = "";
                String birthDate = "";
                String[] extras = customerExtraFields.get(id);
                if (extras != null) {
                    regDate = extras.length > 0 ? extras[0] : "";
                    birthDate = extras.length > 1 ? extras[1] : "";
                }
                model.addRow(new Object[]{id, name, regDate, birthDate});
            }
            table = new JTable(model);
            table.getModel().addTableModelListener(e -> {
                int row = e.getFirstRow();
                String id = (String) model.getValueAt(row, 0);
                String regDate = (String) model.getValueAt(row, 2);
                String birthDate = (String) model.getValueAt(row, 3);
                String[] extras = customerExtraFields.getOrDefault(id, new String[3]);
                if (extras.length < 3) {
                    String[] newExtras = new String[3];
                    System.arraycopy(extras, 0, newExtras, 0, extras.length);
                    extras = newExtras;
                }
                extras[0] = regDate;
                extras[1] = birthDate;
                customerExtraFields.put(id, extras);
                logHistory("Edit", "Customer", id, (String) model.getValueAt(row, 1), "Membership/Birth updated in Analysis");
                // Persist changes
                saveTableToFile(customerTableModel, CUSTOMER_FILE, 6);
            });
        } else if (title.equals("Sales Report")) {
            String[] columns = {"Car Paid ID", "Customer ID", "Price Paid"};
            model = new DefaultTableModel(columns, 0);
            // Placeholder: you may want to use a real sales list
            // For now, just show cars with status "Available" == "No" or similar
            for (int i = 0; i < carTableModel.getRowCount(); i++) {
                String carId = (String) carTableModel.getValueAt(i, 0);
                String price = carTableModel.getValueAt(i, 4).toString();
                String status = (String) carTableModel.getValueAt(i, 5);
                if ("Available".equalsIgnoreCase(status)) continue;
                String customerId = ""; // You may want to link this to a real sale
                model.addRow(new Object[]{carId, customerId, price});
            }
            table = new JTable(model);
        } else if (title.equals("Car Inventory Report")) {
            JPanel carPanel = new JPanel();
            carPanel.setLayout(new BoxLayout(carPanel, BoxLayout.Y_AXIS));
            // Cars that left (bought)
            String[] leftColumns = {"Car ID", "Brand", "Model", "Price"};
            DefaultTableModel leftModel = new DefaultTableModel(leftColumns, 0);
            // Cars in storage (status = Available)
            String[] storageColumns = {"Car ID", "Brand", "Model", "Price"};
            DefaultTableModel storageModel = new DefaultTableModel(storageColumns, 0);
            for (int i = 0; i < carTableModel.getRowCount(); i++) {
                String carId = (String) carTableModel.getValueAt(i, 0);
                String brand = (String) carTableModel.getValueAt(i, 1);
                String modelStr = (String) carTableModel.getValueAt(i, 2);
                String price = carTableModel.getValueAt(i, 4).toString();
                String status = (String) carTableModel.getValueAt(i, 5);
                if ("Available".equalsIgnoreCase(status)) {
                    storageModel.addRow(new Object[]{carId, brand, modelStr, price});
                } else {
                    leftModel.addRow(new Object[]{carId, brand, modelStr, price});
                }
            }
            JTable leftTable = new JTable(leftModel);
            JTable storageTable = new JTable(storageModel);
            JLabel leftLabel = new JLabel("Cars Already Purchased");
            leftLabel.setFont(HEADER_FONT);
            leftLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
            JLabel storageLabel = new JLabel("Cars in Inventory");
            storageLabel.setFont(HEADER_FONT);
            storageLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 4, 0));
            carPanel.add(leftLabel);
            carPanel.add(new JScrollPane(leftTable));
            carPanel.add(storageLabel);
            carPanel.add(new JScrollPane(storageTable));
            dialog.getContentPane().add(carPanel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            return;
        } else if (title.equals("Customer Report")) {
            String[] columns = {"Customer ID", "Full Name", "Car Purchased ID", "Paid Approval"};
            model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                String id = (String) customerTableModel.getValueAt(i, 0);
                String name = (String) customerTableModel.getValueAt(i, 1);
                String carId = ""; // Link to real purchase if available
                String paidApproval = "Processing";
                model.addRow(new Object[]{id, name, carId, paidApproval});
            }
            table = new JTable(model);
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = table.getSelectedRow();
                    if (row != -1 && evt.getClickCount() == 2) {
                        JTextField idField = new JTextField((String) model.getValueAt(row, 0));
                        JTextField nameField = new JTextField((String) model.getValueAt(row, 1));
                        JTextField carIdField = new JTextField((String) model.getValueAt(row, 2));
                        JComboBox<String> paidCombo = new JComboBox<>(new String[]{"Paid", "Processing", "Denied"});
                        paidCombo.setSelectedItem(model.getValueAt(row, 3));
                        JPanel editPanel = new JPanel(new java.awt.GridLayout(0, 1, 8, 8));
                        editPanel.add(new JLabel("Customer ID:")); editPanel.add(idField);
                        editPanel.add(new JLabel("Full Name:")); editPanel.add(nameField);
                        editPanel.add(new JLabel("Car Purchased ID:")); editPanel.add(carIdField);
                        editPanel.add(new JLabel("Paid Approval:")); editPanel.add(paidCombo);
                        int result = JOptionPane.showConfirmDialog(dialog, editPanel, "Edit Customer Report", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (result == JOptionPane.OK_OPTION) {
                            model.setValueAt(idField.getText(), row, 0);
                            model.setValueAt(nameField.getText(), row, 1);
                            model.setValueAt(carIdField.getText(), row, 2);
                            model.setValueAt(paidCombo.getSelectedItem(), row, 3);
                            // Optionally, persist changes if needed
                        }
                    }
                }
            });
        } else if (title.equals("Feedback Reports")) {
            String[] columns = {"Customer ID", "Full Name", "Feedback Ratings"};
            model = new DefaultTableModel(columns, 0);
            int feedbackSum = 0, feedbackCount = 0;
            for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                String id = (String) customerTableModel.getValueAt(i, 0);
                String name = (String) customerTableModel.getValueAt(i, 1);
                String[] extras = customerExtraFields.get(id);
                String feedback = (extras != null && extras.length > 2) ? extras[2] : null;
                model.addRow(new Object[]{id, name, feedback});
                if (feedback != null && feedback.matches("[1-5]")) {
                    feedbackSum += Integer.parseInt(feedback);
                    feedbackCount++;
                }
            }
            table = new JTable(model) {
                public boolean isCellEditable(int row, int col) { return col == 2; }
            };
            // Use JComboBox for feedback selection
            JComboBox<String> feedbackCombo = new JComboBox<>(new String[]{"", "1", "2", "3", "4", "5"});
            table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(feedbackCombo));
            // Count and average label
            JLabel avgLabel = new JLabel();
            Runnable updateAvg = () -> {
                int sum = 0, count = 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    Object val = model.getValueAt(i, 2);
                    if (val != null && val.toString().matches("[1-5]")) {
                        sum += Integer.parseInt(val.toString());
                        count++;
                    }
                }
                avgLabel.setText("Overall Feedback Ratings: " + (count > 0 ? (sum * 1.0 / count) : 0) + "/5 (" + count + " ratings)");
            };
            updateAvg.run();
            model.addTableModelListener(e -> updateAvg.run());
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(avgLabel, BorderLayout.WEST);
            content.add(topPanel, BorderLayout.NORTH);
        } else {
            model = new DefaultTableModel(new String[]{"No Data"}, 0);
            table = new JTable(model);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(scrollPane, BorderLayout.CENTER);
        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createUserProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        acss.model.User user = (acss.model.User) loggedInUser;
        JLabel title = new JLabel("User Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panel.add(title);
        panel.add(Box.createVerticalStrut(18));
        // Show info in table form, centered and narrow
        String[][] data = {
            {"Staff ID", user.getStaffId()},
            {"Full Name", user.getFullName()},
            {"Email", user.getEmail()},
            {"Phone Number", user.getContactNumber()},
            {"Assigned Branch", user.getAssignedBranch() == null ? "" : user.getAssignedBranch()},
            {"Role", user.getRole().toString()}
        };
        JTable infoTable = new JTable(data, new String[]{"Field", "Value"});
        infoTable.setEnabled(false);
        infoTable.setRowHeight(28);
        infoTable.setShowGrid(false);
        infoTable.setIntercellSpacing(new Dimension(0, 0));
        infoTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        infoTable.setPreferredScrollableViewportSize(new Dimension(320, infoTable.getRowHeight() * data.length));
        JScrollPane infoScroll = new JScrollPane(infoTable);
        infoScroll.setBorder(BorderFactory.createEmptyBorder(8, 32, 8, 32));
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
        tablePanel.setOpaque(false);
        tablePanel.add(Box.createHorizontalGlue());
        tablePanel.add(infoScroll);
        tablePanel.add(Box.createHorizontalGlue());
        panel.add(tablePanel);
        panel.add(Box.createVerticalStrut(24));
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton editBtn = new JButton("Edit");
        JButton historyBtn = new JButton("History Check");
        btnPanel.add(editBtn);
        btnPanel.add(historyBtn);
        panel.add(btnPanel);
        editBtn.addActionListener(e -> openEditProfileDialog(user));
        historyBtn.addActionListener(e -> showUserHistoryDialog());
        return panel;
    }

    private void openEditProfileDialog(acss.model.User user) {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(36, 36, 36, 36)
        ));
        card.setMaximumSize(new Dimension(320, 600));
        card.setPreferredSize(new Dimension(320, 600));

        JLabel titleLabel = new JLabel("Edit Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        card.add(titleLabel);

        JTextField fullNameField = new JTextField(user.getFullName(), 18);
        fullNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(new JLabel("Full Name:"));
        card.add(fullNameField);
        card.add(Box.createVerticalStrut(14));

        JTextField staffIdField = new JTextField(user.getStaffId(), 18);
        staffIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        staffIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(new JLabel("Staff ID:"));
        card.add(staffIdField);
        card.add(Box.createVerticalStrut(14));

        JPasswordField passwordField = new JPasswordField(user.getPassword(), 18);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(new JLabel("Password:"));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));

        JTextField emailField = new JTextField(user.getEmail(), 18);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(new JLabel("Email Address:"));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        JTextField contactField = new JTextField(user.getContactNumber(), 18);
        contactField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        contactField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(new JLabel("Contact Number:"));
        card.add(contactField);
        card.add(Box.createVerticalStrut(14));

        JTextField branchField = new JTextField(user.getAssignedBranch(), 18);
        branchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        branchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        card.add(new JLabel("Assigned Branch:"));
        card.add(branchField);
        card.add(Box.createVerticalStrut(14));

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"MANAGING_STAFF", "SALESMAN", "CUSTOMER"});
        roleCombo.setSelectedItem(user.getRole().toString());
        card.add(new JLabel("Role:"));
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(18));

        JLabel msgLabel = new JLabel("");
        msgLabel.setForeground(new Color(0, 128, 0));
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(msgLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        JButton updateBtn = new JButton("Update");
        JButton backBtn = new JButton("Return");
        btnPanel.add(updateBtn);
        btnPanel.add(backBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(btnPanel);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        updateBtn.addActionListener(e -> {
            user.setRole(acss.model.UserRole.valueOf((String) roleCombo.getSelectedItem()));
            user.setAssignedBranch(branchField.getText());
            user.setPassword(new String(passwordField.getPassword()));
            msgLabel.setText("Profile updated (placeholder)");
        });
        backBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showUserHistoryDialog() {
        // Load last 15 logs from file
        java.util.List<String[]> historyRows = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(HISTORY_FILE))) {
            java.util.List<String> allLines = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                allLines.add(line);
            }
            int start = Math.max(0, allLines.size() - 15);
            for (int i = start; i < allLines.size(); i++) {
                String[] parts = allLines.get(i).split("\\|");
                if (parts.length == 6) {
                    historyRows.add(parts);
                }
            }
        } catch (Exception e) {}
        String[] columns = {"Action", "Type", "ID", "Name", "Date & Time", "Description"};
        Object[][] data = new Object[historyRows.size()][6];
        for (int i = 0; i < historyRows.size(); i++) {
            data[i] = historyRows.get(i);
        }
        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new java.awt.Dimension(700, 180));
        JButton returnBtn = new JButton("Return");
        JPanel btnPanel = new JPanel();
        btnPanel.add(returnBtn);
        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout(10, 10));
        panel.add(scrollPane, java.awt.BorderLayout.CENTER);
        panel.add(btnPanel, java.awt.BorderLayout.SOUTH);
        JDialog dialog = new JDialog(this, "History Changes", true);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        returnBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void loadStaffTable() {
        loadTableFromFile(staffTableModel, STAFF_FILE, 7);
    }
    private void loadCustomerTable() {
        loadTableFromFile(customerTableModel, CUSTOMER_FILE, 6);
    }
    private void loadCarTable() {
        loadTableFromFile(carTableModel, CAR_FILE, 7);
    }

    private void loadTableFromFile(DefaultTableModel model, String file, int colCount) {
        model.setRowCount(0);
        File f = new File(file);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= colCount) {
                    Object[] row = new Object[colCount];
                    for (int i = 0; i < colCount; i++) row[i] = parts[i];
                    model.addRow(row);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void logHistory(String action, String type, String id, String name, String desc) {
        try (java.io.FileWriter fw = new java.io.FileWriter(HISTORY_FILE, true)) {
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = java.time.LocalDateTime.now().format(dtf);
            fw.write(action + "|" + type + "|" + id + "|" + name + "|" + now + "|" + desc + "\n");
        } catch (Exception e) {}
    }

    // --- Custom renderer/editor for ID + More button ---
    class IdWithMoreRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JLabel idLabel = new JLabel();
        private JButton moreBtn = new JButton("More");
        public IdWithMoreRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(idLabel);
            add(Box.createHorizontalStrut(8));
            add(moreBtn);
            setOpaque(true);
            moreBtn.setFocusable(false);
            moreBtn.setVisible(true); // Always visible
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            idLabel.setText(value == null ? "" : value.toString());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
    class IdWithMoreEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel();
        private JLabel idLabel = new JLabel();
        private JButton moreBtn = new JButton("More");
        private DashboardFrame parent;
        private DefaultTableModel model;
        private String type;
        private int row;
        public IdWithMoreEditor(DashboardFrame parent, DefaultTableModel model, String type) {
            super(new JTextField());
            this.parent = parent;
            this.model = model;
            this.type = type;
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(idLabel);
            panel.add(Box.createHorizontalStrut(8));
            panel.add(moreBtn);
            moreBtn.addActionListener(e -> showMoreDialog(row));
        }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            idLabel.setText(value == null ? "" : value.toString());
            return panel;
        }
        public Object getCellEditorValue() { return idLabel.getText(); }
        private void showMoreDialog(int row) {
            showMoreDialog(row, false);
        }
        private void showMoreDialog(int row, boolean editMode) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JDialog dialog = new JDialog(parent, editMode ? "Edit Details" : "Details", true);
            JTextField[] fields = new JTextField[10];
            JComboBox<String> clearanceCombo = null, engineTypeCombo = null, transmissionCombo = null, fuelTypeCombo = null;
            String idKey = (String) model.getValueAt(row, 0);
            if (type.equals("staff")) {
                String staffId = (String) model.getValueAt(row, 0);
                String fullName = (String) model.getValueAt(row, 1);
                String email = (String) model.getValueAt(row, 2);
                String phone = (String) model.getValueAt(row, 3);
                String[] extras = staffExtraFields.getOrDefault(staffId, new String[]{"", "", "", "1"});
                fields[0] = new JTextField(staffId, 15);
                fields[1] = new JTextField(fullName, 15);
                fields[2] = new JTextField(email, 15);
                fields[3] = new JTextField(phone, 15);
                fields[4] = new JTextField(extras[0], 15); // PIN
                fields[5] = new JTextField(extras[1], 15); // Date of Hire
                fields[6] = new JTextField(extras[2], 15); // Department
                clearanceCombo = new JComboBox<>(new String[]{"1","2","3","4","5"});
                clearanceCombo.setSelectedItem(extras[3]);
                panel.add(new JLabel("Staff ID:")); panel.add(editMode ? fields[0] : new JLabel(staffId));
                panel.add(new JLabel("Full Name:")); panel.add(editMode ? fields[1] : new JLabel(fullName));
                panel.add(new JLabel("Email Address:")); panel.add(editMode ? fields[2] : new JLabel(email));
                panel.add(new JLabel("Contact Number:")); panel.add(editMode ? fields[3] : new JLabel(phone));
                panel.add(new JLabel("Personal Identification Number:")); panel.add(editMode ? fields[4] : new JLabel(extras[0]));
                panel.add(new JLabel("Role: Managing Staff"));
                panel.add(new JLabel("Date of Hire:")); panel.add(editMode ? fields[5] : new JLabel(extras[1]));
                panel.add(new JLabel("Department:")); panel.add(editMode ? fields[6] : new JLabel(extras[2]));
                panel.add(new JLabel("Clearance Level:")); panel.add(editMode ? clearanceCombo : new JLabel(extras[3]));
            } else if (type.equals("salesman")) {
                String staffId = (String) model.getValueAt(row, 0);
                String fullName = (String) model.getValueAt(row, 1);
                String email = (String) model.getValueAt(row, 2);
                String phone = (String) model.getValueAt(row, 3);
                String[] extras = salesmanExtraFields.getOrDefault(staffId, new String[]{"", "", "", "", "1"});
                fields[0] = new JTextField(staffId, 15);
                fields[1] = new JTextField(fullName, 15);
                fields[2] = new JTextField(email, 15);
                fields[3] = new JTextField(phone, 15);
                fields[4] = new JTextField(extras[0], 15); // PIN
                fields[5] = new JTextField(extras[1], 15); // License ID
                fields[6] = new JTextField(extras[2], 15); // Date of Hire
                fields[7] = new JTextField(extras[3], 15); // Department
                clearanceCombo = new JComboBox<>(new String[]{"1","2","3","4","5"});
                clearanceCombo.setSelectedItem(extras[4]);
                panel.add(new JLabel("Staff ID:")); panel.add(editMode ? fields[0] : new JLabel(staffId));
                panel.add(new JLabel("Full Name:")); panel.add(editMode ? fields[1] : new JLabel(fullName));
                panel.add(new JLabel("Email Address:")); panel.add(editMode ? fields[2] : new JLabel(email));
                panel.add(new JLabel("Phone Number:")); panel.add(editMode ? fields[3] : new JLabel(phone));
                panel.add(new JLabel("Personal Identification Number:")); panel.add(editMode ? fields[4] : new JLabel(extras[0]));
                panel.add(new JLabel("Salesman License ID:")); panel.add(editMode ? fields[5] : new JLabel(extras[1]));
                panel.add(new JLabel("Role: Salesman"));
                panel.add(new JLabel("Date of Hire:")); panel.add(editMode ? fields[6] : new JLabel(extras[2]));
                panel.add(new JLabel("Department:")); panel.add(editMode ? fields[7] : new JLabel(extras[3]));
                panel.add(new JLabel("Clearance Level:")); panel.add(editMode ? clearanceCombo : new JLabel(extras[4]));
            } else if (type.equals("customer")) {
                String customerId = (String) model.getValueAt(row, 0);
                String fullName = (String) model.getValueAt(row, 1);
                String phone = (String) model.getValueAt(row, 3);
                String email = (String) model.getValueAt(row, 2);
                String[] extras = customerExtraFields.getOrDefault(customerId, new String[]{""});
                fields[0] = new JTextField(customerId, 15);
                fields[1] = new JTextField(fullName, 15);
                fields[2] = new JTextField(phone, 15);
                fields[3] = new JTextField(email, 15);
                fields[4] = new JTextField(extras[0], 15); // Membership Date
                panel.add(new JLabel("Customer ID:")); panel.add(editMode ? fields[0] : new JLabel(customerId));
                panel.add(new JLabel("Full Name:")); panel.add(editMode ? fields[1] : new JLabel(fullName));
                panel.add(new JLabel("Phone Number:")); panel.add(editMode ? fields[2] : new JLabel(phone));
                panel.add(new JLabel("Email Address:")); panel.add(editMode ? fields[3] : new JLabel(email));
                panel.add(new JLabel("Membership Date of Registered:")); panel.add(editMode ? fields[4] : new JLabel(extras[0]));
            } else if (type.equals("car")) {
                String carId = (String) model.getValueAt(row, 0);
                String modelStr = (String) model.getValueAt(row, 2);
                String brand = (String) model.getValueAt(row, 1);
                String year = (String) model.getValueAt(row, 3);
                String carManuId = String.format("%012d", (long)(Math.random()*1_000_000_000_000L));
                String[] extras = carExtraFields.getOrDefault(carId, new String[]{carManuId, "", "", "Petrol", "Automatic", "", "Petrol"});
                fields[0] = new JTextField(carId, 15);
                fields[1] = new JTextField(modelStr, 15);
                fields[2] = new JTextField(brand, 15);
                fields[3] = new JTextField(year, 15);
                fields[4] = new JTextField(extras[0], 15);
                fields[5] = new JTextField(extras[1], 15); // Insurance Policy
                javax.swing.JFormattedTextField insuranceExpiryField = new javax.swing.JFormattedTextField(new java.text.SimpleDateFormat("dd/MM/yyyy"));
                insuranceExpiryField.setColumns(15);
                insuranceExpiryField.setText(extras[2]);
                engineTypeCombo = new JComboBox<>(new String[]{"Petrol","Diesel","Electric","Hybrid"});
                engineTypeCombo.setSelectedItem(extras[3]);
                transmissionCombo = new JComboBox<>(new String[]{"Automatic","Manual"});
                transmissionCombo.setSelectedItem(extras[4]);
                fields[7] = new JTextField(extras[5], 10); // Engine Size
                fuelTypeCombo = new JComboBox<>(new String[]{"Petrol","Diesel"});
                fuelTypeCombo.setSelectedItem(extras[6]);
                panel.add(new JLabel("Car ID:")); panel.add(editMode ? fields[0] : new JLabel(carId));
                panel.add(new JLabel("Model:")); panel.add(editMode ? fields[1] : new JLabel(modelStr));
                panel.add(new JLabel("Brand:")); panel.add(editMode ? fields[2] : new JLabel(brand));
                panel.add(new JLabel("Year of Manufactured:")); panel.add(editMode ? fields[3] : new JLabel(year));
                panel.add(new JLabel("Car Manufactured ID:")); panel.add(editMode ? fields[4] : new JLabel(extras[0]));
                panel.add(new JLabel("Insurance Policy Number:")); panel.add(editMode ? fields[5] : new JLabel(extras[1]));
                panel.add(new JLabel("Insurance Expiry Date:")); panel.add(editMode ? insuranceExpiryField : new JLabel(extras[2]));
                panel.add(new JLabel("Engine Type:")); panel.add(editMode ? engineTypeCombo : new JLabel(extras[3]));
                panel.add(new JLabel("Transmission:")); panel.add(editMode ? transmissionCombo : new JLabel(extras[4]));
                panel.add(new JLabel("Engine Size:")); panel.add(editMode ? fields[7] : new JLabel(extras[5]));
                panel.add(new JLabel("Fuel Type:")); panel.add(editMode ? fuelTypeCombo : new JLabel(extras[6]));
            }
            panel.add(Box.createVerticalStrut(10));
            JPanel btnPanel = new JPanel();
            if (!editMode) {
                JButton editBtn = new JButton("Edit");
                JButton returnBtn = new JButton("Return");
                btnPanel.add(editBtn);
                btnPanel.add(returnBtn);
                editBtn.addActionListener(e -> {
                    dialog.dispose();
                    showMoreDialog(row, true);
                });
                returnBtn.addActionListener(e -> dialog.dispose());
            } else {
                final JTextField[] fFields = fields;
                final JComboBox<String> fClearanceCombo = clearanceCombo;
                final JComboBox<String> fEngineTypeCombo = engineTypeCombo;
                final JComboBox<String> fTransmissionCombo = transmissionCombo;
                final JComboBox<String> fFuelTypeCombo = fuelTypeCombo;
                JButton updateBtn = new JButton("Update");
                JButton backBtn = new JButton("Back");
                btnPanel.add(updateBtn);
                btnPanel.add(backBtn);
                updateBtn.addActionListener(e -> {
                    if (type.equals("staff")) {
                        model.setValueAt(fFields[0].getText(), row, 0);
                        model.setValueAt(fFields[1].getText(), row, 1);
                        model.setValueAt(fFields[2].getText(), row, 2);
                        model.setValueAt(fFields[3].getText(), row, 3);
                        staffExtraFields.put(fFields[0].getText(), new String[]{fFields[4].getText(), fFields[5].getText(), fFields[6].getText(), fClearanceCombo.getSelectedItem().toString()});
                        logHistory("Edit", "Staff", fFields[0].getText(), fFields[1].getText(), "Staff info updated");
                    } else if (type.equals("salesman")) {
                        model.setValueAt(fFields[0].getText(), row, 0);
                        model.setValueAt(fFields[1].getText(), row, 1);
                        model.setValueAt(fFields[2].getText(), row, 2);
                        model.setValueAt(fFields[3].getText(), row, 3);
                        salesmanExtraFields.put(fFields[0].getText(), new String[]{fFields[4].getText(), fFields[5].getText(), fFields[6].getText(), fFields[7].getText(), fClearanceCombo.getSelectedItem().toString()});
                        logHistory("Edit", "Salesman", fFields[0].getText(), fFields[1].getText(), "Salesman info updated");
                    } else if (type.equals("customer")) {
                        model.setValueAt(fFields[0].getText(), row, 0);
                        model.setValueAt(fFields[1].getText(), row, 1);
                        model.setValueAt(fFields[2].getText(), row, 3);
                        model.setValueAt(fFields[3].getText(), row, 2);
                        customerExtraFields.put(fFields[0].getText(), new String[]{fFields[4].getText()});
                        logHistory("Edit", "Customer", fFields[0].getText(), fFields[1].getText(), "Customer info updated");
                    } else if (type.equals("car")) {
                        model.setValueAt(fFields[0].getText(), row, 0);
                        model.setValueAt(fFields[1].getText(), row, 2);
                        model.setValueAt(fFields[2].getText(), row, 1);
                        model.setValueAt(fFields[3].getText(), row, 3);
                        carExtraFields.put(fFields[0].getText(), new String[]{fFields[4].getText(), fFields[5].getText(), ((javax.swing.JFormattedTextField)fFields[6]).getText(), fEngineTypeCombo.getSelectedItem().toString(), fTransmissionCombo.getSelectedItem().toString(), fFields[7].getText(), fFuelTypeCombo.getSelectedItem().toString()});
                        logHistory("Edit", "Car", fFields[0].getText(), fFields[1].getText(), "Car info updated");
                    }
                    dialog.dispose();
                    showMoreDialog(row, false);
                });
                backBtn.addActionListener(e -> {
                    dialog.dispose();
                    showMoreDialog(row, false);
                });
            }
            panel.add(btnPanel);
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(350, 500));
            dialog.getContentPane().add(scrollPane);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }
    }

    private void saveTableToFile(DefaultTableModel model, String file, int colCount) {
        try (java.io.FileWriter fw = new java.io.FileWriter(file, false)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < colCount; j++) {
                    fw.write(model.getValueAt(i, j).toString());
                    if (j < colCount - 1) fw.write("|");
                }
                fw.write("\n");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveExtraFieldsToFile(java.util.Map<String, String[]> map, String file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String key : map.keySet()) {
                String[] arr = map.get(key);
                StringBuilder sb = new StringBuilder(key);
                for (String s : arr) {
                    sb.append("|").append(s == null ? "" : s.replace("|", "/"));
                }
                pw.println(sb.toString());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadExtraFieldsFromFile(java.util.Map<String, String[]> map, String file, int arrLen) {
        map.clear();
        File f = new File(file);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 2) {
                    String[] arr = new String[arrLen];
                    for (int i = 0; i < arrLen && i + 1 < parts.length; i++) arr[i] = parts[i + 1];
                    map.put(parts[0], arr);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
} 