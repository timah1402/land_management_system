package views.admin.panels;

import dao.UserDAO;
import models.User;
import views.admin.dialogs.AllUsersDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * IMPROVED User Management Panel with modern UI
 */
public class UserManagementPanel extends JPanel {

    private UserDAO userDAO;
    private Runnable onChangeCallback;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;

    public UserManagementPanel(UserDAO userDAO, Runnable onChangeCallback) {
        this.userDAO = userDAO;
        this.onChangeCallback = onChangeCallback;

        initializeUI();
        loadUsers();
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
                // Purple gradient for user management
                GradientPaint gp = new GradientPaint(0, 0, new Color(155, 89, 182), w, 0, new Color(142, 68, 173));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));

        // Title
        JLabel titleLabel = new JLabel("👥 User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 20));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        buttonPanel.setOpaque(false);

        JButton allUsersButton = createStyledButton("📋 All Users", new Color(52, 152, 219));
        allUsersButton.setPreferredSize(new Dimension(130, 40));
        allUsersButton.addActionListener(e -> viewAllUsers());

        JButton refreshButton = createStyledButton("🔄 Refresh", Color.WHITE);
        refreshButton.setForeground(new Color(155, 89, 182));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> refreshTable());

        buttonPanel.add(allUsersButton);
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
        searchField.addActionListener(e -> filterUsers());

        // Role filter
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 13));

        roleFilter = new JComboBox<>(new String[]{
                "All Roles", "ADMIN", "LAND_AGENT", "CITIZEN"
        });
        roleFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        roleFilter.setPreferredSize(new Dimension(140, 35));
        roleFilter.setBackground(Color.WHITE);
        roleFilter.addActionListener(e -> filterUsers());

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        statusFilter = new JComboBox<>(new String[]{
                "All Status", "PENDING", "ACTIVE", "SUSPENDED", "REJECTED"
        });
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        statusFilter.setPreferredSize(new Dimension(140, 35));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.addActionListener(e -> filterUsers());

        // Search button
        JButton searchBtn = createStyledButton("Search", new Color(155, 89, 182));
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchBtn.addActionListener(e -> filterUsers());

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(roleLabel);
        searchPanel.add(roleFilter);
        searchPanel.add(statusLabel);
        searchPanel.add(statusFilter);
        searchPanel.add(searchBtn);

        return searchPanel;
    }

    private JScrollPane createModernTable() {
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Role", "Status", "Created"};

        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(45);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.setSelectionBackground(new Color(155, 89, 182, 40));
        userTable.setSelectionForeground(Color.BLACK);

        // Alternating row colors and custom rendering
        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }

                // Role column - render as badge
                if (column == 4 && value != null) {
                    return createRoleBadge(value.toString());
                }

                // Status column - render as badge
                if (column == 5 && value != null) {
                    return createStatusBadge(value.toString());
                }

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        // Modern table header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JLabel createRoleBadge(String role) {
        JLabel badge = new JLabel(role);
        badge.setOpaque(true);
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        switch (role) {
            case "ADMIN":
                badge.setBackground(new Color(211, 84, 0));
                badge.setForeground(Color.WHITE);
                badge.setText("👑 " + role);
                break;
            case "LAND_AGENT":
                badge.setBackground(new Color(52, 152, 219));
                badge.setForeground(Color.WHITE);
                badge.setText("🏢 AGENT");
                break;
            case "CITIZEN":
                badge.setBackground(new Color(149, 165, 166));
                badge.setForeground(Color.WHITE);
                badge.setText("👤 " + role);
                break;
            default:
                badge.setBackground(new Color(189, 195, 199));
                badge.setForeground(Color.WHITE);
        }

        return badge;
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
                badge.setText("⏳ " + status);
                break;
            case "ACTIVE":
                badge.setBackground(new Color(76, 175, 80));
                badge.setForeground(Color.WHITE);
                badge.setText("✓ " + status);
                break;
            case "SUSPENDED":
                badge.setBackground(new Color(255, 152, 0));
                badge.setForeground(Color.WHITE);
                badge.setText("⏸ " + status);
                break;
            case "REJECTED":
                badge.setBackground(new Color(244, 67, 54));
                badge.setForeground(Color.WHITE);
                badge.setText("✗ " + status);
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

        // Approve button
        JButton approveBtn = createStyledButton("✅ Approve", new Color(46, 204, 113));
        approveBtn.setPreferredSize(new Dimension(160, 45));
        approveBtn.addActionListener(e -> approveSelectedUser());

        // Reject button
        JButton rejectBtn = createStyledButton("❌ Reject", new Color(231, 76, 60));
        rejectBtn.setPreferredSize(new Dimension(160, 45));
        rejectBtn.addActionListener(e -> rejectSelectedUser());

        // Suspend button
        JButton suspendBtn = createStyledButton("⏸ Suspend", new Color(230, 126, 34));
        suspendBtn.setPreferredSize(new Dimension(160, 45));
        suspendBtn.addActionListener(e -> suspendSelectedUser());

        // Activate button
        JButton activateBtn = createStyledButton("🔓 Activate", new Color(52, 152, 219));
        activateBtn.setPreferredSize(new Dimension(160, 45));
        activateBtn.addActionListener(e -> activateSelectedUser());

        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(suspendBtn);
        actionPanel.add(activateBtn);

        return actionPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(bgColor == Color.WHITE ? new Color(155, 89, 182) : Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        Color hoverColor = bgColor == Color.WHITE ? new Color(236, 240, 241) : bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void filterUsers() {
        String searchText = searchField.getText().trim().toLowerCase();
        String role = (String) roleFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();

        userTableModel.setRowCount(0);
        var users = userDAO.getAllUsers();

        for (User user : users) {
            // Role filter
            if (!"All Roles".equals(role) && !user.getRole().toString().equals(role)) {
                continue;
            }

            // Status filter
            if (!"All Status".equals(status) && !user.getAccountStatus().toString().equals(status)) {
                continue;
            }

            // Search filter
            String searchableText = (
                    user.getUserId() + " " +
                            user.getFullName() + " " +
                            user.getEmail() + " " +
                            user.getPhone()
            ).toLowerCase();

            if (!searchText.isEmpty() && !searchableText.contains(searchText)) {
                continue;
            }

            addUserRow(user);
        }
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        var users = userDAO.getAllUsers();

        for (User user : users) {
            addUserRow(user);
        }
    }

    private void addUserRow(User user) {
        Object[] row = {
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString().substring(0, 10) : "N/A"
        };
        userTableModel.addRow(row);
    }

    private void refreshTable() {
        loadUsers();
        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    private void approveSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a user to approve", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        String currentStatus = userTableModel.getValueAt(selectedRow, 5).toString();

        if ("ACTIVE".equals(currentStatus)) {
            showStyledMessage("User " + userName + " is already active!", "Already Active", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve user: " + userName + "?\n\nThis will activate their account.",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.updateAccountStatus(userId, User.AccountStatus.ACTIVE);
            if (success) {
                showStyledMessage("✓ User approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                showStyledMessage("✗ Failed to approve user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a user to reject", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reject user: " + userName + "?\n\nThey will need to re-register.",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.updateAccountStatus(userId, User.AccountStatus.REJECTED);
            if (success) {
                showStyledMessage("User rejected", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                showStyledMessage("✗ Failed to reject user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void suspendSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a user to suspend", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        String currentStatus = userTableModel.getValueAt(selectedRow, 5).toString();

        if ("SUSPENDED".equals(currentStatus)) {
            showStyledMessage("User is already suspended", "Already Suspended", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(
                this,
                "Enter reason for suspending " + userName + ":",
                "Suspend User",
                JOptionPane.QUESTION_MESSAGE
        );

        if (reason != null && !reason.trim().isEmpty()) {
            boolean success = userDAO.updateAccountStatus(userId, User.AccountStatus.SUSPENDED);
            if (success) {
                showStyledMessage("✓ User suspended\n\nReason: " + reason, "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                showStyledMessage("✗ Failed to suspend user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void activateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a user to activate", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        String currentStatus = userTableModel.getValueAt(selectedRow, 5).toString();

        if ("ACTIVE".equals(currentStatus)) {
            showStyledMessage("User is already active", "Already Active", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Activate user: " + userName + "?",
                "Confirm Activation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.updateAccountStatus(userId, User.AccountStatus.ACTIVE);
            if (success) {
                showStyledMessage("✓ User activated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                showStyledMessage("✗ Failed to activate user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAllUsers() {
        AllUsersDialog dialog = new AllUsersDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                userDAO
        );
        dialog.setVisible(true);
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}