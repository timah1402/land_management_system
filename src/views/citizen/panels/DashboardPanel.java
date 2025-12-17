package views.citizen.panels;

import dao.*;
import models.*;
import views.citizen.components.StatCard;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard overview panel showing statistics
 */
public class DashboardPanel extends JPanel {

    private Citizen currentCitizen;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;

    public DashboardPanel(Citizen currentCitizen, ParcelDAO parcelDAO,
                          TransactionDAO transactionDAO, DisputeDAO disputeDAO) {
        this.currentCitizen = currentCitizen;
        this.parcelDAO = parcelDAO;
        this.transactionDAO = transactionDAO;
        this.disputeDAO = disputeDAO;

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
        int myParcels = currentCitizen != null ?
                parcelDAO.getParcelsByOwner(currentCitizen.getCitizenId()).size() : 0;
        int myTransactions = currentCitizen != null ?
                transactionDAO.getTransactionsByCitizen(currentCitizen.getCitizenId()).size() : 0;
        int myDisputes = currentCitizen != null ?
                disputeDAO.getDisputesByCitizen(currentCitizen.getCitizenId()).size() : 0;
        int pendingTransactions = currentCitizen != null ?
                (int) transactionDAO.getTransactionsByCitizen(currentCitizen.getCitizenId()).stream()
                        .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING).count() : 0;

        // Add stat cards
        statsPanel.add(new StatCard("My Parcels", String.valueOf(myParcels), new Color(46, 204, 113)));
        statsPanel.add(new StatCard("My Transactions", String.valueOf(myTransactions), new Color(52, 152, 219)));
        statsPanel.add(new StatCard("My Disputes", String.valueOf(myDisputes), new Color(231, 76, 60)));
        statsPanel.add(new StatCard("Pending Approvals", String.valueOf(pendingTransactions), new Color(230, 126, 34)));

        add(statsPanel);
    }
}