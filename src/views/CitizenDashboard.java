package views;

import dao.*;
import models.*;
import utils.SessionManager;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Citizen Dashboard - Main interface for citizens
 */
public class CitizenDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;
    private CitizenDAO citizenDAO;
    private Citizen currentCitizen;

    private JTable parcelTable;
    private javax.swing.table.DefaultTableModel parcelTableModel;

    private JTable transactionTable;
    private javax.swing.table.DefaultTableModel transactionTableModel;

    private JTable disputeTable;
    private javax.swing.table.DefaultTableModel disputeTableModel;

    public CitizenDashboard() {
        parcelDAO = new ParcelDAO();
        transactionDAO = new TransactionDAO();
        disputeDAO = new DisputeDAO();
        citizenDAO = new CitizenDAO();

        // Get current citizen info
        User currentUser = SessionManager.getInstance().getCurrentUser();
        currentCitizen = citizenDAO.getCitizenByUserId(currentUser.getUserId());

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Citizen Dashboard - " + Constants.APP_NAME);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("My Parcels", createParcelsPanel());
        tabbedPane.addTab("My Transactions", createTransactionsPanel());
        tabbedPane.addTab("My Disputes", createDisputesPanel());
        tabbedPane.addTab("My Profile", createProfilePanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 188, 156));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(26, 188, 156));

        JLabel titleLabel = new JLabel("Citizen Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(26, 188, 156));

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

        JLabel titleLabel = new JLabel("My Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(1100, 300));

        // Get statistics
        int myParcels = currentCitizen != null ?
                parcelDAO.getParcelsByOwner(currentCitizen.getCitizenId()).size() : 0;
        int myTransactions = currentCitizen != null ?
                transactionDAO.getTransactionsByCitizen(currentCitizen.getCitizenId()).size() : 0;
        int myDisputes = currentCitizen != null ?
                disputeDAO.getDisputesByCitizen(currentCitizen.getCitizenId()).size() : 0;
        int pendingTransactions = currentCitizen != null ?
                (int) transactionDAO.getTransactionsByCitizen(currentCitizen.getCitizenId()).stream()
                        .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING).count() : 0;

        // Create stat cards
        statsPanel.add(createStatCard("My Parcels", String.valueOf(myParcels), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("My Transactions", String.valueOf(myTransactions), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("My Disputes", String.valueOf(myDisputes), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Pending Approvals", String.valueOf(pendingTransactions), new Color(230, 126, 34)));

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

    private JPanel createParcelsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Parcels");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyParcels());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Parcel #", "Land Title", "Area", "Type", "Status", "Region", "Value (XOF)"};

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

        loadMyParcels();

        JScrollPane scrollPane = new JScrollPane(parcelTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewParcelDetails());

        bottomPanel.add(viewButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMyParcels() {
        parcelTableModel.setRowCount(0);

        if (currentCitizen != null) {
            var parcels = parcelDAO.getParcelsByOwner(currentCitizen.getCitizenId());

            for (var parcel : parcels) {
                Object[] row = {
                        parcel.getParcelId(),
                        parcel.getParcelNumber(),
                        parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                        parcel.getArea() + " " + parcel.getAreaUnit(),
                        parcel.getLandType(),
                        parcel.getStatus(),
                        parcel.getRegion(),
                        parcel.getEstimatedValue() != null ? String.format("%.2f", parcel.getEstimatedValue()) : "N/A"
                };
                parcelTableModel.addRow(row);
            }
        }
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
                            "Estimated Value: %.2f XOF\n" +
                            "Acquisition Date: %s",
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
                    parcel.getEstimatedValue() != null ? parcel.getEstimatedValue() : 0.0,
                    parcel.getAcquisitionDate() != null ? parcel.getAcquisitionDate().toString() : "N/A"
            );

            JOptionPane.showMessageDialog(this, details, "Parcel Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBackground(Color.WHITE);

        JButton newTransactionButton = new JButton("New Transaction");
        newTransactionButton.setBackground(new Color(46, 204, 113));
        newTransactionButton.setForeground(Color.WHITE);
        newTransactionButton.setOpaque(true);
        newTransactionButton.setBorderPainted(false);
        newTransactionButton.addActionListener(e -> showNewTransactionDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyTransactions());

        buttonGroup.add(newTransactionButton);
        buttonGroup.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonGroup, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Parcel #", "Type", "Amount (XOF)", "Date", "Status", "From"};

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

        loadMyTransactions();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewTransactionDetails());

        bottomPanel.add(viewButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMyTransactions() {
        transactionTableModel.setRowCount(0);

        if (currentCitizen != null) {
            var transactions = transactionDAO.getTransactionsByCitizen(currentCitizen.getCitizenId());

            for (var transaction : transactions) {
                Object[] row = {
                        transaction.getTransactionId(),
                        "Parcel #" + transaction.getParcelId(),
                        transaction.getType(),
                        transaction.getAmount() != null ? String.format("%.2f", transaction.getAmount()) : "0.00",
                        transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString() : "N/A",
                        transaction.getStatus(),
                        transaction.getPreviousOwnerId() != null ? "Citizen #" + transaction.getPreviousOwnerId() : "N/A"
                };
                transactionTableModel.addRow(row);
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
                            "Amount: %s XOF\n" +
                            "Date: %s\n" +
                            "Status: %s\n" +
                            "From: %s\n" +
                            "To: Citizen #%d\n" +
                            "Notes: %s",
                    transaction.getTransactionId(),
                    transaction.getParcelId(),
                    transaction.getType(),
                    transaction.getAmount() != null ? transaction.getAmount().toString() : "0.00",
                    transaction.getTransactionDate(),
                    transaction.getStatus(),
                    transaction.getPreviousOwnerId() != null ? "Citizen #" + transaction.getPreviousOwnerId() : "N/A",
                    transaction.getNewOwnerId(),
                    transaction.getNotes() != null ? transaction.getNotes() : "None"
            );

            JOptionPane.showMessageDialog(this, details, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showNewTransactionDialog() {
        JDialog dialog = new JDialog(this, "New Transaction Request", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Parcel Selection - DROPDOWN
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Select Parcel:"), gbc);

        var allParcels = parcelDAO.getAllParcels();
        String[] parcelOptions = new String[allParcels.size() + 1];
        parcelOptions[0] = "-- Select Parcel --";

        for (int i = 0; i < allParcels.size(); i++) {
            var parcel = allParcels.get(i);
            parcelOptions[i + 1] = String.format("ID: %d - %s (%s, %s)",
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getLandType(),
                    parcel.getRegion()
            );
        }

        JComboBox<String> parcelCombo = new JComboBox<>(parcelOptions);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(parcelCombo, gbc);

        // Transaction Type
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Type:"), gbc);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PURCHASE", "SALE", "TRANSFER", "INHERITANCE", "DONATION", "EXCHANGE"});
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(typeCombo, gbc);

        // Previous Owner Selection - DROPDOWN
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("From Citizen (optional):"), gbc);

        var allCitizens = citizenDAO.getAllCitizens();
        String[] citizenOptions = new String[allCitizens.size() + 1];
        citizenOptions[0] = "-- Select Previous Owner (Optional) --";

        for (int i = 0; i < allCitizens.size(); i++) {
            var citizen = allCitizens.get(i);
            citizenOptions[i + 1] = String.format("ID: %d - %s %s",
                    citizen.getCitizenId(),
                    citizen.getFirstName(),
                    citizen.getLastName()
            );
        }

        JComboBox<String> previousOwnerCombo = new JComboBox<>(citizenOptions);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(previousOwnerCombo, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Amount (XOF):"), gbc);
        JTextField amountField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(amountField, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Notes:"), gbc);
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(notesScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(e -> {
            try {
                if (parcelCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Please select a parcel", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transaction transaction = new Transaction();

                // Get selected parcel from dropdown
                int selectedParcelIndex = parcelCombo.getSelectedIndex();
                var selectedParcel = allParcels.get(selectedParcelIndex - 1);
                transaction.setParcelId(selectedParcel.getParcelId());

                transaction.setType(Transaction.TransactionType.valueOf((String) typeCombo.getSelectedItem()));

                // Get selected previous owner from dropdown
                int selectedOwnerIndex = previousOwnerCombo.getSelectedIndex();
                if (selectedOwnerIndex > 0) {
                    var selectedCitizen = allCitizens.get(selectedOwnerIndex - 1);
                    transaction.setPreviousOwnerId(selectedCitizen.getCitizenId());
                }

                transaction.setNewOwnerId(currentCitizen.getCitizenId());
                transaction.setAmount(new BigDecimal(amountField.getText().trim()));
                transaction.setCurrency("XOF");
                transaction.setTransactionDate(Date.valueOf(LocalDate.now()));
                transaction.setStatus(Transaction.TransactionStatus.PENDING);
                transaction.setNotes(notesArea.getText().trim());

                if (transactionDAO.createTransaction(transaction)) {
                    JOptionPane.showMessageDialog(dialog, "Transaction submitted successfully!\nPending agent approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMyTransactions();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to submit transaction", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private JPanel createDisputesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Disputes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBackground(Color.WHITE);

        JButton newDisputeButton = new JButton("File New Dispute");
        newDisputeButton.setBackground(new Color(231, 76, 60));
        newDisputeButton.setForeground(Color.WHITE);
        newDisputeButton.setOpaque(true);
        newDisputeButton.setBorderPainted(false);
        newDisputeButton.addActionListener(e -> showNewDisputeDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyDisputes());

        buttonGroup.add(newDisputeButton);
        buttonGroup.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonGroup, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Parcel", "Type", "Status", "Priority", "Opened", "Resolution"};

        disputeTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        disputeTable = new JTable(disputeTableModel);
        disputeTable.setFont(new Font("Arial", Font.PLAIN, 12));
        disputeTable.setRowHeight(30);
        disputeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadMyDisputes();

        JScrollPane scrollPane = new JScrollPane(disputeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewDisputeDetails());

        bottomPanel.add(viewButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMyDisputes() {
        disputeTableModel.setRowCount(0);

        if (currentCitizen != null) {
            var disputes = disputeDAO.getDisputesByCitizen(currentCitizen.getCitizenId());

            for (var dispute : disputes) {
                Object[] row = {
                        dispute.getDisputeId(),
                        "Parcel #" + dispute.getParcelId(),
                        dispute.getType(),
                        dispute.getStatus(),
                        dispute.getPriority(),
                        dispute.getOpenedDate() != null ? dispute.getOpenedDate().toString() : "N/A",
                        dispute.getResolution() != null ? "Resolved" : "Pending"
                };
                disputeTableModel.addRow(row);
            }
        }
    }

    private void viewDisputeDetails() {
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
                            "Description: %s\n" +
                            "Opened: %s\n" +
                            "Assigned Agent: %s\n" +
                            "Resolution: %s\n" +
                            "Resolved On: %s",
                    dispute.getDisputeId(),
                    dispute.getParcelId(),
                    dispute.getType(),
                    dispute.getStatus(),
                    dispute.getPriority(),
                    dispute.getDescription(),
                    dispute.getOpenedDate(),
                    dispute.getAssignedAgentId() != null && dispute.getAssignedAgentId() > 0 ? "Agent #" + dispute.getAssignedAgentId() : "Not assigned",
                    dispute.getResolution() != null ? dispute.getResolution() : "Not yet resolved",
                    dispute.getResolutionDate() != null ? dispute.getResolutionDate().toString() : "N/A"
            );

            JOptionPane.showMessageDialog(this, details, "Dispute Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showNewDisputeDialog() {
        JDialog dialog = new JDialog(this, "File New Dispute", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Parcel Selection - DROPDOWN
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Select Parcel:"), gbc);

        var allParcels = parcelDAO.getAllParcels();
        String[] parcelOptions = new String[allParcels.size() + 1];
        parcelOptions[0] = "-- Select Parcel --";

        for (int i = 0; i < allParcels.size(); i++) {
            var parcel = allParcels.get(i);
            parcelOptions[i + 1] = String.format("ID: %d - %s (%s)",
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getRegion()
            );
        }

        JComboBox<String> parcelCombo = new JComboBox<>(parcelOptions);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(parcelCombo, gbc);

        // Dispute Type
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Type:"), gbc);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"OWNERSHIP", "BOUNDARY", "USAGE", "INHERITANCE", "OTHER"});
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(typeCombo, gbc);

        // Defendant Selection - DROPDOWN
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Defendant (optional):"), gbc);

        var allCitizens = citizenDAO.getAllCitizens();
        String[] citizenOptions = new String[allCitizens.size() + 1];
        citizenOptions[0] = "-- Select Defendant (Optional) --";

        for (int i = 0; i < allCitizens.size(); i++) {
            var citizen = allCitizens.get(i);
            citizenOptions[i + 1] = String.format("ID: %d - %s %s",
                    citizen.getCitizenId(),
                    citizen.getFirstName(),
                    citizen.getLastName()
            );
        }

        JComboBox<String> defendantCombo = new JComboBox<>(citizenOptions);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(defendantCombo, gbc);

        // Priority
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Priority:"), gbc);
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH", "URGENT"});
        priorityCombo.setSelectedIndex(1); // Default to MEDIUM
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(priorityCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Description:"), gbc);
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(descScroll, gbc);

        // Evidence
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Evidence/Notes:"), gbc);
        JTextArea evidenceArea = new JTextArea(3, 20);
        evidenceArea.setLineWrap(true);
        JScrollPane evidenceScroll = new JScrollPane(evidenceArea);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(evidenceScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("File Dispute");
        submitButton.setBackground(new Color(231, 76, 60));
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(e -> {
            try {
                if (parcelCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Please select a parcel", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (descriptionArea.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please provide a description", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Dispute dispute = new Dispute();

                // Get selected parcel from dropdown
                int selectedParcelIndex = parcelCombo.getSelectedIndex();
                var selectedParcel = allParcels.get(selectedParcelIndex - 1);
                dispute.setParcelId(selectedParcel.getParcelId());

                dispute.setComplainantId(currentCitizen.getCitizenId());

                // Get selected defendant from dropdown
                int selectedDefendantIndex = defendantCombo.getSelectedIndex();
                if (selectedDefendantIndex > 0) {
                    var selectedCitizen = allCitizens.get(selectedDefendantIndex - 1);
                    dispute.setDefendantId(selectedCitizen.getCitizenId());
                }

                dispute.setType(Dispute.DisputeType.valueOf((String) typeCombo.getSelectedItem()));
                dispute.setDescription(descriptionArea.getText().trim());
                dispute.setStatus(Dispute.DisputeStatus.OPEN);
                dispute.setPriority(Dispute.Priority.valueOf((String) priorityCombo.getSelectedItem()));
                dispute.setOpenedDate(Date.valueOf(LocalDate.now()));
                dispute.setEvidenceProvided(evidenceArea.getText().trim());

                if (disputeDAO.createDispute(dispute)) {
                    JOptionPane.showMessageDialog(dialog, "Dispute filed successfully!\nAn agent will be assigned soon.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMyDisputes();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to file dispute", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        if (currentCitizen != null) {
            JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.setMaximumSize(new Dimension(800, 400));

            addProfileField(infoPanel, "Full Name:", currentCitizen.getFullName());
            addProfileField(infoPanel, "Email:", currentCitizen.getEmail());
            addProfileField(infoPanel, "Phone:", currentCitizen.getPhone());
            addProfileField(infoPanel, "ID Card Number:", currentCitizen.getIdCardNumber() != null ? currentCitizen.getIdCardNumber() : "N/A");
            addProfileField(infoPanel, "Date of Birth:", currentCitizen.getDateOfBirth() != null ? currentCitizen.getDateOfBirth().toString() : "N/A");
            addProfileField(infoPanel, "Place of Birth:", currentCitizen.getPlaceOfBirth() != null ? currentCitizen.getPlaceOfBirth() : "N/A");
            addProfileField(infoPanel, "Address:", currentCitizen.getFullAddress() != null ? currentCitizen.getFullAddress() : "N/A");
            addProfileField(infoPanel, "Occupation:", currentCitizen.getOccupation() != null ? currentCitizen.getOccupation() : "N/A");
            addProfileField(infoPanel, "Account Status:", currentCitizen.getAccountStatus().toString());
            addProfileField(infoPanel, "Member Since:", currentCitizen.getCreatedAt() != null ? currentCitizen.getCreatedAt().toString().substring(0, 10) : "N/A");

            panel.add(infoPanel);
        } else {
            JLabel errorLabel = new JLabel("Unable to load profile information");
            errorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(errorLabel);
        }

        return panel;
    }

    private void addProfileField(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(labelComponent);
        panel.add(valueComponent);
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

            // For testing
            User citizen = new User();
            citizen.setUserId(1);
            citizen.setFirstName("Test");
            citizen.setLastName("Citizen");
            citizen.setRole(User.UserRole.CITIZEN);
            SessionManager.getInstance().startSession(citizen);

            new CitizenDashboard().setVisible(true);
        });
    }
}