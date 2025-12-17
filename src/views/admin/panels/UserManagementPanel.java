package views.admin.panels;

import dao.UserDAO;
import models.User;
import views.admin.dialogs.AllUsersDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for managing user accounts and approvals
 */
public class UserManagementPanel extends JPanel {

    private UserDAO userDAO;
    private Runnable onChangeCallback;
    private JTable userTable;
    private DefaultTableModel userTableModel;

    public UserManagementPanel(UserDAO userDAO, Runnable onChangeCallback) {
        this.userDAO = userDAO;
        this.onChangeCallback = onChangeCallback;

        initializeUI();
        loadPendingUsers();
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

        JLabel titleLabel = new JLabel("User Management - Pending Approvals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> refreshTable());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Role", "Status", "Date"};

        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(userTable);
    }

    private JPanel createBottomPanel() {
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

        return buttonPanel;
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

    private void refreshTable() {
        loadPendingUsers();
        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
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
                refreshTable();
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
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reject user", "Error", JOptionPane.ERROR_MESSAGE);
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
}