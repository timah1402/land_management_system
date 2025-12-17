package views;

import dao.*;
import models.*;
import utils.SessionManager;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Agent Dashboard - Main interface for land agents
 */
public class AgentDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private UserDAO userDAO;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;
    private CitizenDAO citizenDAO;
    private LandAgent currentAgent;

    private JTable transactionTable;
    private javax.swing.table.DefaultTableModel transactionTableModel;

    private JTable disputeTable;
    private javax.swing.table.DefaultTableModel disputeTableModel;

    private JTable parcelTable;
    private javax.swing.table.DefaultTableModel parcelTableModel;

    public AgentDashboard() {
        userDAO = new UserDAO();
        parcelDAO = new ParcelDAO();
        transactionDAO = new TransactionDAO();
        disputeDAO = new DisputeDAO();
        citizenDAO = new CitizenDAO();

        // Get current agent info
        User currentUser = SessionManager.getInstance().getCurrentUser();
        LandAgentDAO agentDAO = new LandAgentDAO();
        currentAgent = agentDAO.getAgentByUserId(currentUser.getUserId());

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Agent Dashboard - " + Constants.APP_NAME);
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
        tabbedPane.addTab("My Transactions", createTransactionPanel());
        tabbedPane.addTab("My Disputes", createDisputePanel());
        tabbedPane.addTab("Parcels in Region", createParcelPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("Agent Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(41, 128, 185));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        JLabel userLabel = new JLabel("Agent: " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        if (currentAgent != null) {
            JLabel regionLabel = new JLabel(" | Region: " + currentAgent.getRegion());
            regionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            regionLabel.setForeground(Color.WHITE);
            rightPanel.add(regionLabel);
        }

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
        int myTransactions = currentAgent != null ?
                transactionDAO.getTransactionsByStatus(Transaction.TransactionStatus.PENDING).size() : 0;
        int myDisputes = currentAgent != null && currentAgent.getAgentId() > 0 ?
                disputeDAO.getDisputesByAgent(currentAgent.getAgentId()).size() : 0;
        int regionParcels = currentAgent != null ?
                parcelDAO.getParcelsByRegion(currentAgent.getRegion()).size() : 0;
        int pendingApprovals = transactionDAO.getTransactionsByStatus(Transaction.TransactionStatus.PENDING).size();

        // Create stat cards
        statsPanel.add(createStatCard("Pending Transactions", String.valueOf(pendingApprovals), new Color(230, 126, 34)));
        statsPanel.add(createStatCard("My Assigned Disputes", String.valueOf(myDisputes), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Parcels in My Region", String.valueOf(regionParcels), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("My Region", currentAgent != null ? currentAgent.getRegion() : "N/A", new Color(52, 152, 219)));

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

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Pending Transactions - Awaiting My Approval");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPendingTransactions());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Parcel #", "Type", "Amount (XOF)", "Date", "From", "To", "Status"};

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

        loadPendingTransactions();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton approveButton = new JButton("Approve Transaction");
        approveButton.setBackground(new Color(39, 174, 96));
        approveButton.setForeground(Color.WHITE);
        approveButton.setOpaque(true);
        approveButton.setBorderPainted(false);
        approveButton.setPreferredSize(new Dimension(180, 40));
        approveButton.addActionListener(e -> approveTransaction());

        JButton rejectButton = new JButton("Reject Transaction");
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setOpaque(true);
        rejectButton.setBorderPainted(false);
        rejectButton.setPreferredSize(new Dimension(180, 40));
        rejectButton.addActionListener(e -> rejectTransaction());

        JButton viewButton = new JButton("View Details");
        viewButton.setPreferredSize(new Dimension(150, 40));
        viewButton.addActionListener(e -> viewTransactionDetails());

        bottomPanel.add(approveButton);
        bottomPanel.add(rejectButton);
        bottomPanel.add(viewButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPendingTransactions() {
        transactionTableModel.setRowCount(0);
        var transactions = transactionDAO.getTransactionsByStatus(Transaction.TransactionStatus.PENDING);

        for (var transaction : transactions) {
            Object[] row = {
                    transaction.getTransactionId(),
                    "Parcel #" + transaction.getParcelId(),
                    transaction.getType(),
                    transaction.getAmount() != null ? String.format("%.2f", transaction.getAmount()) : "0.00",
                    transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString() : "N/A",
                    transaction.getPreviousOwnerId() != null ? "Citizen #" + transaction.getPreviousOwnerId() : "N/A",
                    "Citizen #" + transaction.getNewOwnerId(),
                    transaction.getStatus()
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

        if (confirm == JOptionPane.YES_OPTION && currentAgent != null) {
            if (transactionDAO.approveTransaction(transactionId, currentAgent.getAgentId())) {
                JOptionPane.showMessageDialog(this, "Transaction approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingTransactions();
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

        if (confirm == JOptionPane.YES_OPTION && currentAgent != null) {
            if (transactionDAO.rejectTransaction(transactionId, currentAgent.getAgentId())) {
                JOptionPane.showMessageDialog(this, "Transaction rejected", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingTransactions();
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
                            "Amount: %s XOF\n" +
                            "Date: %s\n" +
                            "Status: %s\n" +
                            "From: %s\n" +
                            "To: Citizen #%d",
                    transaction.getTransactionId(),
                    transaction.getParcelId(),
                    transaction.getType(),
                    transaction.getAmount() != null ? transaction.getAmount().toString() : "0.00",
                    transaction.getTransactionDate(),
                    transaction.getStatus(),
                    transaction.getPreviousOwnerId() != null ? "Citizen #" + transaction.getPreviousOwnerId() : "N/A",
                    transaction.getNewOwnerId()
            );

            JOptionPane.showMessageDialog(this, details, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createDisputePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Assigned Disputes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyDisputes());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Parcel", "Type", "Status", "Priority", "Complainant", "Opened"};

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

        JButton resolveButton = new JButton("Resolve Dispute");
        resolveButton.setBackground(new Color(39, 174, 96));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setOpaque(true);
        resolveButton.setBorderPainted(false);
        resolveButton.addActionListener(e -> resolveDispute());

        bottomPanel.add(viewButton);
        bottomPanel.add(resolveButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMyDisputes() {
        disputeTableModel.setRowCount(0);

        if (currentAgent != null && currentAgent.getAgentId() > 0) {
            var disputes = disputeDAO.getDisputesByAgent(currentAgent.getAgentId());

            for (var dispute : disputes) {
                Object[] row = {
                        dispute.getDisputeId(),
                        "Parcel #" + dispute.getParcelId(),
                        dispute.getType(),
                        dispute.getStatus(),
                        dispute.getPriority(),
                        "Citizen #" + dispute.getComplainantId(),
                        dispute.getOpenedDate() != null ? dispute.getOpenedDate().toString() : "N/A"
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
                    dispute.getResolution() != null ? dispute.getResolution() : "Not yet resolved"
            );

            JOptionPane.showMessageDialog(this, details, "Dispute Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resolveDispute() {
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
                loadMyDisputes();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to resolve dispute", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createParcelPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Parcels in My Region: " + (currentAgent != null ? currentAgent.getRegion() : "N/A"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadRegionParcels());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"ID", "Parcel #", "Land Title", "Area", "Type", "Status", "Owner"};

        parcelTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        parcelTable = new JTable(parcelTableModel);
        parcelTable.setFont(new Font("Arial", Font.PLAIN, 12));
        parcelTable.setRowHeight(30);

        loadRegionParcels();

        JScrollPane scrollPane = new JScrollPane(parcelTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadRegionParcels() {
        parcelTableModel.setRowCount(0);

        if (currentAgent != null) {
            var parcels = parcelDAO.getParcelsByRegion(currentAgent.getRegion());

            for (var parcel : parcels) {
                Object[] row = {
                        parcel.getParcelId(),
                        parcel.getParcelNumber(),
                        parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                        parcel.getArea() + " " + parcel.getAreaUnit(),
                        parcel.getLandType(),
                        parcel.getStatus(),
                        parcel.getCurrentOwnerId() > 0 ? "Citizen #" + parcel.getCurrentOwnerId() : "Unassigned"
                };
                parcelTableModel.addRow(row);
            }
        }
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
            User agent = new User();
            agent.setUserId(1);
            agent.setFirstName("Agent");
            agent.setLastName("Test");
            agent.setRole(User.UserRole.AGENT);
            SessionManager.getInstance().startSession(agent);

            new AgentDashboard().setVisible(true);
        });
    }
}