package views.admin.panels;

import dao.*;
import models.User;
import views.admin.components.AdminStatCard;

import javax.swing.*;
import java.awt.*;

/**
 * Admin dashboard overview panel showing system statistics
 */
public class AdminDashboardPanel extends JPanel {

    private UserDAO userDAO;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;

    private JPanel statsPanel;

    public AdminDashboardPanel(UserDAO userDAO, ParcelDAO parcelDAO,
                               TransactionDAO transactionDAO, DisputeDAO disputeDAO) {
        this.userDAO = userDAO;
        this.parcelDAO = parcelDAO;
        this.transactionDAO = transactionDAO;
        this.disputeDAO = disputeDAO;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("System Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics panel
        statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(1100, 300));

        loadStatistics();

        add(statsPanel);
    }

    private void loadStatistics() {
        statsPanel.removeAll();

        // Get statistics
        int totalUsers = userDAO.getAllUsers().size();
        int pendingUsers = userDAO.getUsersByStatus(User.AccountStatus.PENDING).size();
        int totalParcels = parcelDAO.getParcelCount();
        int totalTransactions = transactionDAO.getAllTransactions().size();
        int totalDisputes = disputeDAO.getDisputeCount();
        int activeCitizens = userDAO.getUsersByRole(User.UserRole.CITIZEN).size();

        // Create stat cards
        statsPanel.add(new AdminStatCard("Total Users", String.valueOf(totalUsers), new Color(52, 152, 219)));
        statsPanel.add(new AdminStatCard("Pending Approvals", String.valueOf(pendingUsers), new Color(230, 126, 34)));
        statsPanel.add(new AdminStatCard("Total Parcels", String.valueOf(totalParcels), new Color(46, 204, 113)));
        statsPanel.add(new AdminStatCard("Transactions", String.valueOf(totalTransactions), new Color(155, 89, 182)));
        statsPanel.add(new AdminStatCard("Disputes", String.valueOf(totalDisputes), new Color(231, 76, 60)));
        statsPanel.add(new AdminStatCard("Active Citizens", String.valueOf(activeCitizens), new Color(26, 188, 156)));

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    public void refreshStats() {
        loadStatistics();
    }
}