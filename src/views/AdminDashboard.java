
        package views;

import dao.*;
import models.User;
import utils.SessionManager;
import utils.Constants;
import views.admin.panels.*;
import views.admin.components.AdminHeaderPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard - Main interface coordinator for administrators
 * Manages the overall layout and tab navigation
 */
public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    // DAOs
    private UserDAO userDAO;
    private CitizenDAO citizenDAO;
    private ParcelDAO parcelDAO;
    private TransactionDAO transactionDAO;
    private DisputeDAO disputeDAO;

    // Panels
    private AdminDashboardPanel dashboardPanel;
    private UserManagementPanel userManagementPanel;
    private ParcelManagementPanel parcelManagementPanel;
    private TransactionManagementPanel transactionManagementPanel;
    private DisputeManagementPanel disputeManagementPanel;

    public AdminDashboard() {
        initializeDAOs();
        initializeUI();
    }

    private void initializeDAOs() {
        userDAO = new UserDAO();
        citizenDAO = new CitizenDAO();
        parcelDAO = new ParcelDAO();
        transactionDAO = new TransactionDAO();
        disputeDAO = new DisputeDAO();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - " + Constants.APP_NAME);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(new AdminHeaderPanel(this::logout), BorderLayout.NORTH);

        // Tabbed pane with all panels
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Initialize panels
        dashboardPanel = new AdminDashboardPanel(userDAO, parcelDAO, transactionDAO, disputeDAO);
        userManagementPanel = new UserManagementPanel(userDAO, this::refreshDashboard);
        parcelManagementPanel = new ParcelManagementPanel(parcelDAO, citizenDAO);
        transactionManagementPanel = new TransactionManagementPanel(transactionDAO);
        disputeManagementPanel = new DisputeManagementPanel(disputeDAO);

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("User Management", userManagementPanel);
        tabbedPane.addTab("Parcels", parcelManagementPanel);
        tabbedPane.addTab("Transactions", transactionManagementPanel);
        tabbedPane.addTab("Disputes", disputeManagementPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void refreshDashboard() {
        dashboardPanel.refreshStats();
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

            // For testing - set a dummy admin session
            User admin = new User();
            admin.setUserId(1);
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.UserRole.ADMIN);
            SessionManager.getInstance().startSession(admin);

            new AdminDashboard().setVisible(true);
        });
    }
}

