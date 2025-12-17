package views;

import dao.*;
import models.*;
import utils.SessionManager;
import utils.Constants;
import views.citizen.panels.*;
import views.citizen.components.HeaderPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Citizen Dashboard - Main interface coordinator for citizens
 * Manages the overall layout and tab navigation
 */
public class CitizenDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private Citizen currentCitizen;

    // DAOs
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;
    private CitizenDAO citizenDAO;

    // Panels
    private DashboardPanel dashboardPanel;
    private ParcelsPanel parcelsPanel;
    private TransactionsPanel transactionsPanel;
    private DisputesPanel disputesPanel;
    private ProfilePanel profilePanel;

    public CitizenDashboard() {
        initializeDAOs();
        loadCurrentCitizen();
        initializeUI();
    }

    private void initializeDAOs() {
        parcelDAO = new ParcelDAO();
        transactionDAO = new TransactionDAO();
        disputeDAO = new DisputeDAO();
        citizenDAO = new CitizenDAO();
    }

    private void loadCurrentCitizen() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        currentCitizen = citizenDAO.getCitizenByUserId(currentUser.getUserId());
    }

    private void initializeUI() {
        setTitle("Citizen Dashboard - " + Constants.APP_NAME);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(new HeaderPanel(this::logout), BorderLayout.NORTH);

        // Tabbed pane with all panels
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Initialize panels
        dashboardPanel = new DashboardPanel(currentCitizen, parcelDAO, transactionDAO, disputeDAO);
        parcelsPanel = new ParcelsPanel(currentCitizen, parcelDAO);
        transactionsPanel = new TransactionsPanel(currentCitizen, transactionDAO, parcelDAO, citizenDAO);
        disputesPanel = new DisputesPanel(currentCitizen, disputeDAO, parcelDAO, citizenDAO);
        profilePanel = new ProfilePanel(currentCitizen);

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("My Parcels", parcelsPanel);
        tabbedPane.addTab("My Transactions", transactionsPanel);
        tabbedPane.addTab("My Disputes", disputesPanel);
        tabbedPane.addTab("My Profile", profilePanel);

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
            User citizen = new User();
            citizen.setUserId(1);
            citizen.setFirstName("Test");
            citizen.setLastName("Citizen");
            citizen.setRole(User.UserRole.CITIZEN);
            SessionManager.getInstance().startSession(citizen);

            new CitizenDashboard().setVisible(true);
        });
    }
}