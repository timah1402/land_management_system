package views.admin.panels;

import dao.DisputeDAO;
import models.Dispute;
import views.admin.dialogs.DisputeDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * IMPROVED Dispute Management Panel with modern UI
 */
public class DisputeManagementPanel extends JPanel {

    private DisputeDAO disputeDAO;
    private JTable disputeTable;
    private DefaultTableModel disputeTableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;

    public DisputeManagementPanel(DisputeDAO disputeDAO) {
        this.disputeDAO = disputeDAO;

        initializeUI();
        loadDisputes();
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
                // Red to orange gradient for disputes (urgent feel)
                GradientPaint gp = new GradientPaint(0, 0, new Color(231, 76, 60), w, 0, new Color(230, 126, 34));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));

        // Title
        JLabel titleLabel = new JLabel("⚖️ Dispute Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 20));

        // Refresh button
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(46, 204, 113));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> loadDisputes());

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

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterDisputes());

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        statusFilter = new JComboBox<>(new String[]{
                "All Status", "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED", "ESCALATED"
        });
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        statusFilter.setPreferredSize(new Dimension(140, 35));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.addActionListener(e -> filterDisputes());

        // Priority filter
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 13));

        priorityFilter = new JComboBox<>(new String[]{
                "All Priority", "LOW", "MEDIUM", "HIGH", "CRITICAL"
        });
        priorityFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        priorityFilter.setPreferredSize(new Dimension(140, 35));
        priorityFilter.setBackground(Color.WHITE);
        priorityFilter.addActionListener(e -> filterDisputes());

        // Search button
        JButton searchBtn = createStyledButton("Search", new Color(231, 76, 60));
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchBtn.addActionListener(e -> filterDisputes());

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(statusLabel);
        searchPanel.add(statusFilter);
        searchPanel.add(priorityLabel);
        searchPanel.add(priorityFilter);
        searchPanel.add(searchBtn);

        return searchPanel;
    }

    private JScrollPane createModernTable() {
        String[] columnNames = {"ID", "Parcel #", "Type", "Status", "Priority", "Complainant", "Opened Date", "Agent"};

        disputeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        disputeTable = new JTable(disputeTableModel);
        disputeTable.setFont(new Font("Arial", Font.PLAIN, 13));
        disputeTable.setRowHeight(45);
        disputeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        disputeTable.setShowGrid(false);
        disputeTable.setIntercellSpacing(new Dimension(0, 0));
        disputeTable.setSelectionBackground(new Color(231, 76, 60, 40));
        disputeTable.setSelectionForeground(Color.BLACK);

        // Alternating row colors and custom rendering
        disputeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }

                // Status column - render as badge
                if (column == 3 && value != null) {
                    return createStatusBadge(value.toString());
                }

                // Priority column - render as badge
                if (column == 4 && value != null) {
                    return createPriorityBadge(value.toString());
                }

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        // Modern table header
        JTableHeader header = disputeTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column widths
        disputeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        disputeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        disputeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        disputeTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        disputeTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        disputeTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        disputeTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        disputeTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        // Double-click to view details
        disputeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewDisputeDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(disputeTable);
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
            case "OPEN":
                badge.setBackground(new Color(52, 152, 219));
                badge.setForeground(Color.WHITE);
                break;
            case "IN_PROGRESS":
                badge.setBackground(new Color(255, 193, 7));
                badge.setForeground(new Color(102, 77, 3));
                break;
            case "RESOLVED":
                badge.setBackground(new Color(76, 175, 80));
                badge.setForeground(Color.WHITE);
                break;
            case "CLOSED":
                badge.setBackground(new Color(158, 158, 158));
                badge.setForeground(Color.WHITE);
                break;
            case "ESCALATED":
                badge.setBackground(new Color(156, 39, 176));
                badge.setForeground(Color.WHITE);
                break;
            default:
                badge.setBackground(new Color(189, 195, 199));
                badge.setForeground(Color.WHITE);
        }

        return badge;
    }

    private JLabel createPriorityBadge(String priority) {
        JLabel badge = new JLabel(priority);
        badge.setOpaque(true);
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        switch (priority) {
            case "CRITICAL":
                badge.setBackground(new Color(211, 47, 47));
                badge.setForeground(Color.WHITE);
                badge.setText("🔥 " + priority);
                break;
            case "HIGH":
                badge.setBackground(new Color(244, 67, 54));
                badge.setForeground(Color.WHITE);
                break;
            case "MEDIUM":
                badge.setBackground(new Color(255, 152, 0));
                badge.setForeground(Color.WHITE);
                break;
            case "LOW":
                badge.setBackground(new Color(76, 175, 80));
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
        viewBtn.addActionListener(e -> viewDisputeDetails());

        // Assign Agent button
        JButton assignBtn = createStyledButton("👤 Assign Agent", new Color(155, 89, 182));
        assignBtn.setPreferredSize(new Dimension(160, 45));
        assignBtn.addActionListener(e -> assignAgent());

        // Resolve button
        JButton resolveBtn = createStyledButton("✅ Resolve", new Color(46, 204, 113));
        resolveBtn.setPreferredSize(new Dimension(160, 45));
        resolveBtn.addActionListener(e -> resolveDispute());

        // Close button
        JButton closeBtn = createStyledButton("🔒 Close", new Color(149, 165, 166));
        closeBtn.setPreferredSize(new Dimension(160, 45));
        closeBtn.addActionListener(e -> closeDispute());

        actionPanel.add(viewBtn);
        actionPanel.add(assignBtn);
        actionPanel.add(resolveBtn);
        actionPanel.add(closeBtn);

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

    private void filterDisputes() {
        String searchText = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();
        String priority = (String) priorityFilter.getSelectedItem();

        disputeTableModel.setRowCount(0);
        var disputes = disputeDAO.getAllDisputes();

        for (var dispute : disputes) {
            // Status filter
            if (!"All Status".equals(status) && !dispute.getStatus().toString().equals(status)) {
                continue;
            }

            // Priority filter
            if (!"All Priority".equals(priority) && !dispute.getPriority().toString().equals(priority)) {
                continue;
            }

            // Search filter
            String searchableText = (
                    dispute.getDisputeId() + " " +
                            dispute.getParcelId() + " " +
                            dispute.getType() + " " +
                            dispute.getStatus() + " " +
                            dispute.getPriority()
            ).toLowerCase();

            if (!searchText.isEmpty() && !searchableText.contains(searchText)) {
                continue;
            }

            addDisputeRow(dispute);
        }
    }

    public void loadDisputes() {
        disputeTableModel.setRowCount(0);
        var disputes = disputeDAO.getAllDisputes();

        for (var dispute : disputes) {
            addDisputeRow(dispute);
        }
    }

    private void addDisputeRow(Dispute dispute) {
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

    private void viewDisputeDetails() {
        int selectedRow = disputeTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a dispute to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);
        Dispute dispute = disputeDAO.getDisputeById(disputeId);

        if (dispute != null) {
            DisputeDetailsDialog dialog = new DisputeDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    dispute
            );
            dialog.setVisible(true);
        }
    }

    private void assignAgent() {
        int selectedRow = disputeTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a dispute to assign", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = disputeTableModel.getValueAt(selectedRow, 3).toString();
        if ("CLOSED".equals(status) || "RESOLVED".equals(status)) {
            showStyledMessage("Cannot assign agent to " + status.toLowerCase() + " dispute",
                    "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String agentIdStr = JOptionPane.showInputDialog(
                this,
                "Enter Agent ID to assign to this dispute:",
                "Assign Agent",
                JOptionPane.QUESTION_MESSAGE
        );

        if (agentIdStr != null && !agentIdStr.trim().isEmpty()) {
            try {
                int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);
                int agentId = Integer.parseInt(agentIdStr.trim());

                if (disputeDAO.assignAgent(disputeId, agentId)) {
                    showStyledMessage("✓ Agent assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDisputes();
                } else {
                    showStyledMessage("✗ Failed to assign agent", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showStyledMessage("Please enter a valid Agent ID (number)", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resolveDispute() {
        int selectedRow = disputeTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a dispute to resolve", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = disputeTableModel.getValueAt(selectedRow, 3).toString();
        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            showStyledMessage("This dispute is already " + status.toLowerCase(),
                    "Already " + status, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea resolutionArea = new JTextArea(5, 30);
        resolutionArea.setLineWrap(true);
        resolutionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resolutionArea);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Enter Resolution Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String resolution = resolutionArea.getText().trim();
            if (!resolution.isEmpty()) {
                int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);

                if (disputeDAO.resolveDispute(disputeId, resolution)) {
                    showStyledMessage("✓ Dispute resolved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDisputes();
                } else {
                    showStyledMessage("✗ Failed to resolve dispute", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showStyledMessage("Resolution details cannot be empty", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void closeDispute() {
        int selectedRow = disputeTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a dispute to close", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = disputeTableModel.getValueAt(selectedRow, 3).toString();
        if ("CLOSED".equals(status)) {
            showStyledMessage("This dispute is already closed", "Already Closed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to close this dispute?\n\nDispute ID: " + disputeId,
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Assuming there's a closeDispute method in DAO
            // For now, we'll use resolveDispute with "CLOSED" status
            if (disputeDAO.resolveDispute(disputeId, "Dispute closed by administrator")) {
                showStyledMessage("✓ Dispute closed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDisputes();
            } else {
                showStyledMessage("✗ Failed to close dispute", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}