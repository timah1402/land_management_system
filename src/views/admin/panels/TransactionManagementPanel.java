package views.admin.panels;

import dao.TransactionDAO;
import models.Transaction;
import views.admin.dialogs.TransactionDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * IMPROVED Transaction Management Panel with modern UI
 */
public class TransactionManagementPanel extends JPanel {

    private TransactionDAO transactionDAO;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public TransactionManagementPanel(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;

        initializeUI();
        loadTransactions();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(236, 240, 241));

        // Header with gradient
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content
        add(createMainPanel(), BorderLayout.CENTER);

        // Action buttons at bottom
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), w, 0, new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));

        // Title
        JLabel titleLabel = new JLabel("💰 Transaction Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 20));

        // Refresh button
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(46, 204, 113));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> loadTransactions());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search and filter panel
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);

        // Table
        mainPanel.add(createModernTable(), BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Search icon and field
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Arial", Font.PLAIN, 16));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterTransactions());

        // Status filter
        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 13));

        statusFilter = new JComboBox<>(new String[]{
                "All Status", "PENDING", "APPROVED", "REJECTED", "CANCELLED"
        });
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        statusFilter.setPreferredSize(new Dimension(150, 35));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.addActionListener(e -> filterTransactions());

        // Search button
        JButton searchBtn = createStyledButton("Search", new Color(52, 152, 219));
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchBtn.addActionListener(e -> filterTransactions());

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(filterLabel);
        searchPanel.add(statusFilter);
        searchPanel.add(searchBtn);

        return searchPanel;
    }

    private JScrollPane createModernTable() {
        String[] columnNames = {"ID", "Parcel #", "Type", "Amount (FCFA)", "Date", "Status", "From", "To"};

        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 13));
        transactionTable.setRowHeight(45);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setShowGrid(false);
        transactionTable.setIntercellSpacing(new Dimension(0, 0));
        transactionTable.setSelectionBackground(new Color(52, 152, 219, 40));
        transactionTable.setSelectionForeground(Color.BLACK);

        // Alternating row colors
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }

                // Status column - render as badge
                if (column == 5 && value != null) {
                    String status = value.toString();
                    JLabel badge = createStatusBadge(status);
                    return badge;
                }

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        // Modern table header
        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Double-click to view details
        transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewTransactionDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status);
        badge.setOpaque(true);
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        switch (status) {
            case "PENDING":
                badge.setBackground(new Color(255, 193, 7));
                badge.setForeground(new Color(102, 77, 3));
                break;
            case "APPROVED":
                badge.setBackground(new Color(76, 175, 80));
                badge.setForeground(Color.WHITE);
                break;
            case "REJECTED":
                badge.setBackground(new Color(244, 67, 54));
                badge.setForeground(Color.WHITE);
                break;
            case "CANCELLED":
                badge.setBackground(new Color(158, 158, 158));
                badge.setForeground(Color.WHITE);
                break;
            default:
                badge.setBackground(new Color(189, 195, 199));
                badge.setForeground(Color.WHITE);
        }

        return badge;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        // View Details button
        JButton viewBtn = createStyledButton("👁️ View Details", new Color(52, 152, 219));
        viewBtn.setPreferredSize(new Dimension(160, 45));
        viewBtn.addActionListener(e -> viewTransactionDetails());

        // Approve button
        JButton approveBtn = createStyledButton("✅ Approve", new Color(46, 204, 113));
        approveBtn.setPreferredSize(new Dimension(160, 45));
        approveBtn.addActionListener(e -> approveTransaction());

        // Reject button
        JButton rejectBtn = createStyledButton("❌ Reject", new Color(231, 76, 60));
        rejectBtn.setPreferredSize(new Dimension(160, 45));
        rejectBtn.addActionListener(e -> rejectTransaction());

        actionPanel.add(viewBtn);
        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);

        return actionPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void filterTransactions() {
        String searchText = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();

        transactionTableModel.setRowCount(0);
        var transactions = transactionDAO.getAllTransactions();

        for (var transaction : transactions) {
            // Status filter
            if (!"All Status".equals(status) && !transaction.getStatus().toString().equals(status)) {
                continue;
            }

            // Search filter
            String searchableText = (
                    transaction.getTransactionId() + " " +
                            transaction.getParcelId() + " " +
                            transaction.getType() + " " +
                            transaction.getStatus()
            ).toLowerCase();

            if (!searchText.isEmpty() && !searchableText.contains(searchText)) {
                continue;
            }

            addTransactionRow(transaction);
        }
    }

    public void loadTransactions() {
        transactionTableModel.setRowCount(0);
        var transactions = transactionDAO.getAllTransactions();

        for (var transaction : transactions) {
            addTransactionRow(transaction);
        }
    }

    private void addTransactionRow(Transaction transaction) {
        Object[] row = {
                transaction.getTransactionId(),
                "Parcel #" + transaction.getParcelId(),
                transaction.getType(),
                String.format("%,.0f", transaction.getAmount() != null ? transaction.getAmount().doubleValue() : 0.0),
                transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString() : "N/A",
                transaction.getStatus(),
                transaction.getPreviousOwnerId() != null ? "Citizen #" + transaction.getPreviousOwnerId() : "N/A",
                "Citizen #" + transaction.getNewOwnerId()
        };
        transactionTableModel.addRow(row);
    }

    private void approveTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a transaction to approve", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);
        String status = transactionTableModel.getValueAt(selectedRow, 5).toString();

        if ("APPROVED".equals(status)) {
            showStyledMessage("This transaction is already approved!", "Already Approved", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to approve this transaction?\n\nTransaction ID: " + transactionId,
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (transactionDAO.approveTransaction(transactionId, 1)) {
                showStyledMessage("✓ Transaction approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTransactions();
            } else {
                showStyledMessage("✗ Failed to approve transaction.\nCheck console for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a transaction to reject", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);
        String status = transactionTableModel.getValueAt(selectedRow, 5).toString();

        if ("REJECTED".equals(status)) {
            showStyledMessage("This transaction is already rejected!", "Already Rejected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reject this transaction?\n\nTransaction ID: " + transactionId,
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (transactionDAO.rejectTransaction(transactionId, 1)) {
                showStyledMessage("Transaction rejected successfully", "Rejected", JOptionPane.INFORMATION_MESSAGE);
                loadTransactions();
            } else {
                showStyledMessage("Failed to reject transaction", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewTransactionDetails() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a transaction to view", "No Selection", JOptionPane.WARNING_MESSAGE);
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

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}