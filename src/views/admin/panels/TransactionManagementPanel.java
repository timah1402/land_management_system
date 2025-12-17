package views.admin.panels;

import dao.TransactionDAO;
import models.Transaction;
import views.admin.dialogs.TransactionDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for managing transactions
 */
public class TransactionManagementPanel extends JPanel {

    private TransactionDAO transactionDAO;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;

    public TransactionManagementPanel(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;

        initializeUI();
        loadTransactions();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        add(createTopPanel(), BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Bottom panel
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Transaction Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTransactions());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel #", "Type", "Amount (XOF)", "Date", "Status", "From", "To"};

        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionTable.setRowHeight(30);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(transactionTable);
    }

    private JPanel createBottomPanel() {
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

        return bottomPanel;
    }

    public void loadTransactions() {
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
        Transaction transaction = transactionDAO.getTransactionById(transactionId);

        if (transaction != null) {
            TransactionDetailsDialog dialog = new TransactionDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    transaction
            );
            dialog.setVisible(true);
        }
    }
}