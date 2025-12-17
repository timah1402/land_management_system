package views.admin.dialogs;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dialog for displaying all users in the system
 */
public class AllUsersDialog extends JDialog {

    private UserDAO userDAO;

    public AllUsersDialog(Frame owner, UserDAO userDAO) {
        super(owner, "All Users", true);
        this.userDAO = userDAO;
        initializeUI();
    }

    private void initializeUI() {
        setSize(900, 600);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"ID", "Name", "Email", "Role", "Status", "Created"};
        DefaultTableModel allUsersModel = new DefaultTableModel(columnNames, 0) {
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
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }
}