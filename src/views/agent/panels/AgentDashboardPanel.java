package views.agent.panels;

import dao.*;
import models.*;
import views.agent.components.AgentStatCard;
import views.agent.dialogs.RegisterParcelDialog;
import views.agent.dialogs.ProcessTransferDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Agent dashboard overview panel showing agent statistics and quick actions
 */
public class AgentDashboardPanel extends JPanel {

    private LandAgent currentAgent;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;
    private ParcelDAO parcelDAO;
    private Frame parentFrame;

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

        // Title
        JLabel titleLabel = new JLabel("My Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Welcome back, " + currentAgent.getFirstName() + "!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(subtitleLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // Quick Actions Section
        add(createQuickActionsPanel());
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

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section title
        JLabel titleLabel = new JLabel("⚡ Quick Actions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Actions grid
        JPanel actionsGrid = new JPanel(new GridLayout(1, 3, 15, 15));
        actionsGrid.setBackground(Color.WHITE);
        actionsGrid.setMaximumSize(new Dimension(1100, 120));
        actionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action Card 1: Register New Parcel
        JPanel registerCard = createActionCard(
                "📋 Register New Parcel",
                "Register a new land parcel for a citizen",
                new Color(46, 204, 113),
                e -> openRegisterParcelDialog()
        );
        actionsGrid.add(registerCard);

        // Action Card 2: Process Transfer
        JPanel transferCard = createActionCard(
                "🔄 Process Transfer",
                "Initiate a land transfer between owners",
                new Color(52, 152, 219),
                e -> openProcessTransferDialog()
        );
        actionsGrid.add(transferCard);

        // Action Card 3: View Reports
        JPanel reportsCard = createActionCard(
                "📊 Generate Report",
                "Create reports and statistics",
                new Color(155, 89, 182),
                e -> JOptionPane.showMessageDialog(this, "Reports feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE)
        );
        actionsGrid.add(reportsCard);

        panel.add(actionsGrid);

        return panel;
    }

    private JPanel createActionCard(String title, String description, Color color, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Description
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(236, 240, 241));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (action != null) {
                    action.actionPerformed(new java.awt.event.ActionEvent(card, 0, "clicked"));
                }
            }
        });

        return card;
    }

    private void openRegisterParcelDialog() {
        // Find parent frame
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame frame = null;
        if (window instanceof Frame) {
            frame = (Frame) window;
        }

        // Open dialog
        RegisterParcelDialog dialog = new RegisterParcelDialog(
                frame,
                currentAgent,
                this::refreshStats
        );
        dialog.setVisible(true);
    }

    private void openProcessTransferDialog() {
        // Find parent frame
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame frame = null;
        if (window instanceof Frame) {
            frame = (Frame) window;
        }

        // Open dialog
        ProcessTransferDialog dialog = new ProcessTransferDialog(
                frame,
                currentAgent,
                this::refreshStats
        );
        dialog.setVisible(true);
    }

    private void refreshStats() {
        // Refresh the statistics after a parcel is registered
        // You can call this method to update the stat cards
        removeAll();
        initializeUI();
        revalidate();
        repaint();
    }

    /**
     * Public method to refresh the panel (can be called from parent)
     */
    public void refresh() {
        refreshStats();
    }
}