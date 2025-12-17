package views.citizen.panels;

import dao.*;
import models.*;
import views.citizen.dialogs.NewTransactionDialog;
import views.citizen.dialogs.TransactionDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for displaying and managing citizen's transactions
 */
public class TransactionsPanel extends JPanel {

    private Citizen currentCitizen;
    private TransactionDAO transactionDAO;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;

    public TransactionsPanel(Citizen currentCitizen, TransactionDAO transactionDAO,
                             ParcelDAO parcelDAO, CitizenDAO citizenDAO) {
        this.currentCitizen = currentCitizen;
        this.transactionDAO = transactionDAO;
        this.parcelDAO = parcelDAO;
        this.citizenDAO = citizenDAO;

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
        refreshButton.addActionListener(e -> loadTransactions());

        buttonGroup.add(newTransactionButton);
        buttonGroup.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonGroup, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel #", "Type", "Amount (XOF)", "Date", "Status", "From"};

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

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewTransactionDetails());

        bottomPanel.add(viewButton);

        return bottomPanel;
    }

    public void loadTransactions() {
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
        Transaction transaction = transactionDAO.getTransactionById(transactionId);

        if (transaction != null) {
            TransactionDetailsDialog dialog = new TransactionDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), transaction);
            dialog.setVisible(true);
        }
    }

    private void showNewTransactionDialog() {
        NewTransactionDialog dialog = new NewTransactionDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                currentCitizen,
                parcelDAO,
                citizenDAO,
                transactionDAO,
                this::loadTransactions
        );
        dialog.setVisible(true);
    }
}