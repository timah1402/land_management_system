package views;

import dao.*;
import models.User;
import utils.SessionManager;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Admin Dashboard - Main interface for administrators
 */
public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private UserDAO userDAO;
    private CitizenDAO citizenDAO;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;

    private JTable userTable;
    private javax.swing.table.DefaultTableModel userTableModel;

    private JTable parcelTable;
    private javax.swing.table.DefaultTableModel parcelTableModel;

    private JTable transactionTable;
    private javax.swing.table.DefaultTableModel transactionTableModel;

    public AdminDashboard() {
        userDAO = new UserDAO();
        citizenDAO = new CitizenDAO();
        parcelDAO = new ParcelDAO();
        transactionDAO = new TransactionDAO();
        disputeDAO = new DisputeDAO();

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - " + Constants.APP_NAME);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Parcels", createParcelManagementPanel());
        tabbedPane.addTab("Transactions", createTransactionManagementPanel());
        tabbedPane.addTab("Disputes", createDisputeManagementPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(44, 62, 80));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(44, 62, 80));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> logout());

        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        rightPanel.add(logoutButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("System Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(1100, 300));

        // Get statistics
        int totalUsers = userDAO.getAllUsers().size();
        int pendingUsers = userDAO.getUsersByStatus(User.AccountStatus.PENDING).size();
        int totalParcels = parcelDAO.getParcelCount();
        int totalTransactions = transactionDAO.getAllTransactions().size();
        int totalDisputes = disputeDAO.getDisputeCount();
        int activeCitizens = userDAO.getUsersByRole(User.UserRole.CITIZEN).size();

        // Create stat cards
        statsPanel.add(createStatCard("Total Users", String.valueOf(totalUsers), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Pending Approvals", String.valueOf(pendingUsers), new Color(230, 126, 34)));
        statsPanel.add(createStatCard("Total Parcels", String.valueOf(totalParcels), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Transactions", String.valueOf(totalTransactions), new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Disputes", String.valueOf(totalDisputes), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Active Citizens", String.valueOf(activeCitizens), new Color(26, 188, 156)));

        panel.add(statsPanel);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);

        return card;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("User Management - Pending Approvals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshUserTable());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table for pending users
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Role", "Status", "Date"};

        userTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Load data
        loadPendingUsers();

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton approveButton = new JButton("Approve Selected");
        approveButton.setFont(new Font("Arial", Font.BOLD, 14));
        approveButton.setBackground(new Color(39, 174, 96));
        approveButton.setForeground(Color.WHITE);
        approveButton.setFocusPainted(false);
        approveButton.setOpaque(true);
        approveButton.setBorderPainted(false);
        approveButton.setPreferredSize(new Dimension(150, 40));
        approveButton.addActionListener(e -> approveSelectedUser());

        JButton rejectButton = new JButton("Reject Selected");
        rejectButton.setFont(new Font("Arial", Font.BOLD, 14));
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusPainted(false);
        rejectButton.setOpaque(true);
        rejectButton.setBorderPainted(false);
        rejectButton.setPreferredSize(new Dimension(150, 40));
        rejectButton.addActionListener(e -> rejectSelectedUser());

        JButton viewAllButton = new JButton("View All Users");
        viewAllButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewAllButton.setPreferredSize(new Dimension(150, 40));
        viewAllButton.addActionListener(e -> viewAllUsers());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(viewAllButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPendingUsers() {
        userTableModel.setRowCount(0);
        var pendingUsers = userDAO.getUsersByStatus(User.AccountStatus.PENDING);

        for (User user : pendingUsers) {
            Object[] row = {
                    user.getUserId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.getAccountStatus(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString().substring(0, 10) : ""
            };
            userTableModel.addRow(row);
        }
    }

    private void refreshUserTable() {
        loadPendingUsers();
        // Also refresh dashboard stats
        refreshDashboardStats();
    }

    private void approveSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to approve", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve user: " + userName + "?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.updateAccountStatus(userId, User.AccountStatus.ACTIVE);
            if (success) {
                JOptionPane.showMessageDialog(this, "User approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to approve user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reject", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reject user: " + userName + "?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.updateAccountStatus(userId, User.AccountStatus.REJECTED);
            if (success) {
                JOptionPane.showMessageDialog(this, "User rejected", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reject user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAllUsers() {
        // Create a new dialog to show all users
        JDialog dialog = new JDialog(this, "All Users", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"ID", "Name", "Email", "Role", "Status", "Created"};
        javax.swing.table.DefaultTableModel allUsersModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        var allUsers = userDAO.getAllUsers();
        for (User user : allUsers) {
            Object[] row = {
                    user.getUserId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getAccountStatus(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString().substring(0, 10) : ""
            };
            allUsersModel.addRow(row);
        }

        JTable table = new JTable(allUsersModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void refreshDashboardStats() {
        // This will be called to refresh the dashboard tab
        // We'll update it when the user switches back to dashboard
    }

    private JPanel createParcelManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Parcel Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBackground(Color.WHITE);

        JButton addParcelButton = new JButton("Add New Parcel");
        addParcelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addParcelButton.setBackground(new Color(46, 204, 113));
        addParcelButton.setForeground(Color.WHITE);
        addParcelButton.setOpaque(true);
        addParcelButton.setBorderPainted(false);
        addParcelButton.addActionListener(e -> showAddParcelDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadParcels());

        buttonGroup.add(addParcelButton);
        buttonGroup.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonGroup, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table for parcels
        String[] columnNames = {"ID", "Parcel #", "Land Title", "Area (ha)", "Type", "Status", "Owner", "Region"};

        parcelTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        parcelTable = new JTable(parcelTableModel);
        parcelTable.setFont(new Font("Arial", Font.PLAIN, 12));
        parcelTable.setRowHeight(30);
        parcelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadParcels();

        JScrollPane scrollPane = new JScrollPane(parcelTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewParcelDetails());

        JButton editButton = new JButton("Edit Parcel");
        editButton.addActionListener(e -> editParcel());

        JButton deleteButton = new JButton("Delete Parcel");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteParcel());

        bottomPanel.add(viewButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadParcels() {
        parcelTableModel.setRowCount(0);
        var parcels = parcelDAO.getAllParcels();

        for (var parcel : parcels) {
            Object[] row = {
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                    parcel.getArea(),
                    parcel.getLandType(),
                    parcel.getStatus(),
                    parcel.getCurrentOwnerId() > 0 ? "Citizen #" + parcel.getCurrentOwnerId() : "Unassigned",
                    parcel.getRegion()
            };
            parcelTableModel.addRow(row);
        }
    }

    private void showAddParcelDialog() {
        JDialog dialog = new JDialog(this, "Add New Parcel", true);
        dialog.setSize(600, 750);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Parcel Number
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Parcel Number:"), gbc);
        JTextField parcelNumberField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(parcelNumberField, gbc);

        // Land Title
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Land Title:"), gbc);
        JTextField landTitleField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(landTitleField, gbc);

        // Area
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Area (hectares):"), gbc);
        JTextField areaField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(areaField, gbc);

        // Land Type
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Land Type:"), gbc);
        JComboBox<String> landTypeCombo = new JComboBox<>(new String[]{"RESIDENTIAL", "COMMERCIAL", "AGRICULTURAL", "INDUSTRIAL", "MIXED"});
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(landTypeCombo, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Address:"), gbc);
        JTextField addressField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(addressField, gbc);

        // Region
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Region:"), gbc);
        JComboBox<String> regionCombo = new JComboBox<>(Constants.REGIONS);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(regionCombo, gbc);

        // GPS Coordinates
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("GPS Coordinates:"), gbc);
        JTextField gpsField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(gpsField, gbc);

        // Owner Selection - NEW!
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Owner (Citizen):"), gbc);

        // Get all active citizens
        var allCitizens = citizenDAO.getAllCitizens();
        String[] citizenOptions = new String[allCitizens.size() + 1];
        citizenOptions[0] = "-- Select Owner (Optional) --";

        for (int i = 0; i < allCitizens.size(); i++) {
            var citizen = allCitizens.get(i);
            citizenOptions[i + 1] = String.format("ID: %d - %s %s (%s)",
                    citizen.getCitizenId(),
                    citizen.getFirstName(),
                    citizen.getLastName(),
                    citizen.getEmail()
            );
        }

        JComboBox<String> ownerCombo = new JComboBox<>(citizenOptions);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(ownerCombo, gbc);

        // Estimated Value
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Estimated Value (XOF):"), gbc);
        JTextField valueField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(valueField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> {
            // Validate and save parcel
            if (parcelNumberField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill required fields (Parcel Number & Address)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                models.Parcel parcel = new models.Parcel();
                parcel.setParcelNumber(parcelNumberField.getText().trim());
                parcel.setLandTitle(landTitleField.getText().trim());
                parcel.setArea(Double.parseDouble(areaField.getText().trim()));
                parcel.setAreaUnit(models.Parcel.AreaUnit.HECTARE);
                parcel.setLandType(models.Parcel.LandType.valueOf((String) landTypeCombo.getSelectedItem()));
                parcel.setAddress(addressField.getText().trim());
                parcel.setRegion((String) regionCombo.getSelectedItem());
                parcel.setGpsCoordinates(gpsField.getText().trim());

                // Set owner if selected
                int selectedOwnerIndex = ownerCombo.getSelectedIndex();
                if (selectedOwnerIndex > 0) {
                    var selectedCitizen = allCitizens.get(selectedOwnerIndex - 1);
                    parcel.setCurrentOwnerId(selectedCitizen.getCitizenId());
                    parcel.setStatus(models.Parcel.ParcelStatus.OCCUPIED);
                } else {
                    parcel.setStatus(models.Parcel.ParcelStatus.AVAILABLE);
                }

                // Set estimated value if provided
                if (!valueField.getText().trim().isEmpty()) {
                    parcel.setEstimatedValue(new BigDecimal(valueField.getText().trim()));
                }

                if (parcelDAO.createParcel(parcel)) {
                    JOptionPane.showMessageDialog(dialog, "Parcel created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadParcels();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create parcel", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void viewParcelDetails() {
        int selectedRow = parcelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a parcel", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int parcelId = (int) parcelTableModel.getValueAt(selectedRow, 0);
        var parcel = parcelDAO.getParcelById(parcelId);

        if (parcel != null) {
            String details = String.format(
                    "Parcel Details:\n\n" +
                            "ID: %d\n" +
                            "Parcel Number: %s\n" +
                            "Land Title: %s\n" +
                            "Area: %.2f %s\n" +
                            "Type: %s\n" +
                            "Status: %s\n" +
                            "Address: %s\n" +
                            "Region: %s\n" +
                            "GPS: %s\n" +
                            "Value: %.2f XOF",
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                    parcel.getArea(),
                    parcel.getAreaUnit(),
                    parcel.getLandType(),
                    parcel.getStatus(),
                    parcel.getAddress(),
                    parcel.getRegion(),
                    parcel.getGpsCoordinates() != null ? parcel.getGpsCoordinates() : "N/A",
                    parcel.getEstimatedValue() != null ? parcel.getEstimatedValue() : 0.0
            );

            JOptionPane.showMessageDialog(this, details, "Parcel Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editParcel() {
        int selectedRow = parcelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a parcel to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Edit functionality coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteParcel() {
        int selectedRow = parcelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a parcel to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int parcelId = (int) parcelTableModel.getValueAt(selectedRow, 0);
        String parcelNumber = (String) parcelTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete parcel: " + parcelNumber + "?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (parcelDAO.deleteParcel(parcelId)) {
                JOptionPane.showMessageDialog(this, "Parcel deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadParcels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete parcel", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createTransactionManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Transaction Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTransactions());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table for transactions
        String[] columnNames = {"ID", "Parcel #", "Type", "Amount (XOF)", "Date", "Status", "From", "To"};

        transactionTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionTable.setRowHeight(30);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadTransactions();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton approveButton = new JButton("Approve Transaction");
        approveButton.setBackground(new Color(39, 174, 96));
        approveButton.setForeground(Color.WHITE);
        approveButton.setOpaque(true);
        approveButton.setBorderPainted(false);
        approveButton.addActionListener(e -> approveTransaction());

        JButton rejectButton = new JButton("Reject Transaction");
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setOpaque(true);
        rejectButton.setBorderPainted(false);
        rejectButton.addActionListener(e -> rejectTransaction());

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewTransactionDetails());

        bottomPanel.add(approveButton);
        bottomPanel.add(rejectButton);
        bottomPanel.add(viewButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadTransactions() {
        transactionTableModel.setRowCount(0);
        var transactions = transactionDAO.getAllTransactions();

        for (var transaction : transactions) {
            Object[] row = {
                    transaction.getTransactionId(),
                    "Parcel #" + transaction.getParcelId(),
                    transaction.getType(),
                    String.format("%.2f", transaction.getAmount() != null ? transaction.getAmount() : 0.0),
                    transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString() : "N/A",
                    transaction.getStatus(),
                    transaction.getPreviousOwnerId() != null ? "Citizen #" + transaction.getPreviousOwnerId() : "N/A",
                    "Citizen #" + transaction.getNewOwnerId()
            };
            transactionTableModel.addRow(row);
        }
    }

    private void approveTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve this transaction?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Get current admin's agent ID (would need to be set properly)
            if (transactionDAO.approveTransaction(transactionId, 1)) { // Using 1 as placeholder
                JOptionPane.showMessageDialog(this, "Transaction approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTransactions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to approve transaction", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reject this transaction?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (transactionDAO.rejectTransaction(transactionId, 1)) { // Using 1 as placeholder
                JOptionPane.showMessageDialog(this, "Transaction rejected", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTransactions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reject transaction", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewTransactionDetails() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);
        var transaction = transactionDAO.getTransactionById(transactionId);

        if (transaction != null) {
            String details = String.format(
                    "Transaction Details:\n\n" +
                            "ID: %d\n" +
                            "Parcel ID: %d\n" +
                            "Type: %s\n" +
                            "Amount: %.2f %s\n" +
                            "Date: %s\n" +
                            "Status: %s\n" +
                            "From Citizen: %s\n" +
                            "To Citizen: %d\n" +
                            "Transaction Fee: %s\n" +
                            "Tax: %s",
                    transaction.getTransactionId(),
                    transaction.getParcelId(),
                    transaction.getType(),
                    transaction.getAmount() != null ? transaction.getAmount() : BigDecimal.ZERO,
                    transaction.getCurrency(),
                    transaction.getTransactionDate(),
                    transaction.getStatus(),
                    transaction.getPreviousOwnerId() != null ? String.valueOf(transaction.getPreviousOwnerId()) : "N/A",
                    transaction.getNewOwnerId(),
                    transaction.getTransactionFees() != null ? transaction.getTransactionFees().toString() : "0.00",
                    transaction.getApplicableTax() != null ? transaction.getApplicableTax().toString() : "0.00"
            );

            JOptionPane.showMessageDialog(this, details, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createDisputeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Dispute Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDisputes());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table for disputes
        String[] columnNames = {"ID", "Parcel", "Type", "Status", "Priority", "Complainant", "Opened Date", "Assigned Agent"};

        javax.swing.table.DefaultTableModel disputeTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable disputeTable = new JTable(disputeTableModel);
        disputeTable.setFont(new Font("Arial", Font.PLAIN, 12));
        disputeTable.setRowHeight(30);
        disputeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Load disputes
        var disputes = disputeDAO.getAllDisputes();
        for (var dispute : disputes) {
            Object[] row = {
                    dispute.getDisputeId(),
                    "Parcel #" + dispute.getParcelId(),
                    dispute.getType(),
                    dispute.getStatus(),
                    dispute.getPriority(),
                    "Citizen #" + dispute.getComplainantId(),
                    dispute.getOpenedDate() != null ? dispute.getOpenedDate().toString() : "N/A",
                    dispute.getAssignedAgentId() != null ? "Agent #" + dispute.getAssignedAgentId() : "Unassigned"
            };
            disputeTableModel.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(disputeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> {
            int selectedRow = disputeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a dispute", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);
            var dispute = disputeDAO.getDisputeById(disputeId);

            if (dispute != null) {
                String details = String.format(
                        "Dispute Details:\n\n" +
                                "ID: %d\n" +
                                "Parcel: %d\n" +
                                "Type: %s\n" +
                                "Status: %s\n" +
                                "Priority: %s\n" +
                                "Complainant: Citizen #%d\n" +
                                "Defendant: %s\n" +
                                "Description: %s\n" +
                                "Opened: %s\n" +
                                "Resolution: %s",
                        dispute.getDisputeId(),
                        dispute.getParcelId(),
                        dispute.getType(),
                        dispute.getStatus(),
                        dispute.getPriority(),
                        dispute.getComplainantId(),
                        dispute.getDefendantId() != null ? "Citizen #" + dispute.getDefendantId() : "N/A",
                        dispute.getDescription(),
                        dispute.getOpenedDate(),
                        dispute.getResolution() != null ? dispute.getResolution() : "Pending"
                );

                JOptionPane.showMessageDialog(this, details, "Dispute Details", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton assignButton = new JButton("Assign to Agent");
        assignButton.addActionListener(e -> {
            int selectedRow = disputeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a dispute", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String agentIdStr = JOptionPane.showInputDialog(this, "Enter Agent ID to assign:");
            if (agentIdStr != null && !agentIdStr.trim().isEmpty()) {
                try {
                    int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);
                    int agentId = Integer.parseInt(agentIdStr.trim());

                    if (disputeDAO.assignAgent(disputeId, agentId)) {
                        JOptionPane.showMessageDialog(this, "Agent assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadDisputes();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to assign agent", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Agent ID", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton resolveButton = new JButton("Resolve Dispute");
        resolveButton.setBackground(new Color(39, 174, 96));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setOpaque(true);
        resolveButton.setBorderPainted(false);
        resolveButton.addActionListener(e -> {
            int selectedRow = disputeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a dispute", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String resolution = JOptionPane.showInputDialog(this, "Enter resolution details:");
            if (resolution != null && !resolution.trim().isEmpty()) {
                int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);

                if (disputeDAO.resolveDispute(disputeId, resolution.trim())) {
                    JOptionPane.showMessageDialog(this, "Dispute resolved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDisputes();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to resolve dispute", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bottomPanel.add(viewButton);
        bottomPanel.add(assignButton);
        bottomPanel.add(resolveButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadDisputes() {
        // Refresh disputes - would need to implement similar to loadParcels
        tabbedPane.setComponentAt(4, createDisputeManagementPanel());
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().endSession();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // For testing - set a dummy admin session
            User admin = new User();
            admin.setUserId(1);
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.UserRole.ADMIN);
            SessionManager.getInstance().startSession(admin);

            new AdminDashboard().setVisible(true);
        });
    }
}