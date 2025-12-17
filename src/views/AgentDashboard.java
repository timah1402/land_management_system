package views;

import dao.*;
import models.*;
import utils.SessionManager;
import utils.Constants;
import views.agent.panels.*;
import views.agent.components.AgentHeaderPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Agent Dashboard - Main interface coordinator for land agents
 * Manages the overall layout and tab navigation
 */
public class AgentDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private LandAgent currentAgent;

    // DAOs
    private UserDAO userDAO;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;
    private CitizenDAO citizenDAO;

    // Panels
    private AgentDashboardPanel dashboardPanel;
    private TransactionsPanel transactionsPanel;
    private DisputesPanel disputesPanel;
    private RegionParcelsPanel regionParcelsPanel;

    public AgentDashboard() {
        initializeDAOs();
        loadCurrentAgent();
        initializeUI();
    }

    private void initializeDAOs() {
        userDAO = new UserDAO();
        parcelDAO = new ParcelDAO();
        transactionDAO = new TransactionDAO();
        disputeDAO = new DisputeDAO();
        citizenDAO = new CitizenDAO();
    }

    private void loadCurrentAgent() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        LandAgentDAO agentDAO = new LandAgentDAO();
        currentAgent = agentDAO.getAgentByUserId(currentUser.getUserId());
    }

    private void initializeUI() {
        setTitle("Agent Dashboard - " + Constants.APP_NAME);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(new AgentHeaderPanel(currentAgent, this::logout), BorderLayout.NORTH);

        // Tabbed pane with all panels
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Initialize panels
        dashboardPanel = new AgentDashboardPanel(currentAgent, transactionDAO, disputeDAO, parcelDAO);
        transactionsPanel = new TransactionsPanel(currentAgent, transactionDAO);
        disputesPanel = new DisputesPanel(currentAgent, disputeDAO);
        regionParcelsPanel = new RegionParcelsPanel(currentAgent, parcelDAO);

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("My Transactions", transactionsPanel);
        tabbedPane.addTab("My Disputes", disputesPanel);
        tabbedPane.addTab("Parcels in Region", regionParcelsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().endSession();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // For testing
            User agent = new User();
            agent.setUserId(1);
            agent.setFirstName("Agent");
            agent.setLastName("Test");
            agent.setRole(User.UserRole.AGENT);
            SessionManager.getInstance().startSession(agent);

            new AgentDashboard().setVisible(true);
        });
    }
}