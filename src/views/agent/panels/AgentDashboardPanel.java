package views.agent.panels;

import dao.*;
import models.*;
import views.agent.components.AgentStatCard;

import javax.swing.*;
import java.awt.*;

/**
 * Agent dashboard overview panel showing agent statistics
 */
public class AgentDashboardPanel extends JPanel {

    private LandAgent currentAgent;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;
    private ParcelDAO parcelDAO;

    public AgentDashboardPanel(LandAgent currentAgent, TransactionDAO transactionDAO,
                               DisputeDAO disputeDAO, ParcelDAO parcelDAO) {
        this.currentAgent = currentAgent;
        this.transactionDAO = transactionDAO;
        this.disputeDAO = disputeDAO;
        this.parcelDAO = parcelDAO;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(1100, 300));

        // Calculate statistics
        int pendingApprovals = transactionDAO.getTransactionsByStatus(Transaction.TransactionStatus.PENDING).size();
        int myDisputes = currentAgent != null && currentAgent.getAgentId() > 0 ?
                disputeDAO.getDisputesByAgent(currentAgent.getAgentId()).size() : 0;
        int regionParcels = currentAgent != null ?
                parcelDAO.getParcelsByRegion(currentAgent.getRegion()).size() : 0;
        String myRegion = currentAgent != null ? currentAgent.getRegion() : "N/A";

        // Create stat cards
        statsPanel.add(new AgentStatCard("Pending Transactions", String.valueOf(pendingApprovals), new Color(230, 126, 34)));
        statsPanel.add(new AgentStatCard("My Assigned Disputes", String.valueOf(myDisputes), new Color(231, 76, 60)));
        statsPanel.add(new AgentStatCard("Parcels in My Region", String.valueOf(regionParcels), new Color(46, 204, 113)));
        statsPanel.add(new AgentStatCard("My Region", myRegion, new Color(52, 152, 219)));

        add(statsPanel);
    }
}