package views;

import dao.UserDAO;
import dao.AdminDAO;
import dao.LandAgentDAO;
import dao.CitizenDAO;
import models.User;
import models.Admin;
import models.LandAgent;
import models.Citizen;
import utils.SessionManager;
import utils.PasswordHasher;
import utils.ValidationUtils;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login Frame for user authentication
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private LandAgentDAO agentDAO;
    private CitizenDAO citizenDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        adminDAO = new AdminDAO();
        agentDAO = new LandAgentDAO();
        citizenDAO = new CitizenDAO();

        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle(Constants.WINDOW_LOGIN);
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel(Constants.APP_NAME);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Land Management System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(450, 400));

        int yPos = 20;

        // Login title
        JLabel loginTitle = new JLabel("Login to Your Account");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 18));
        loginTitle.setForeground(new Color(44, 62, 80));
        loginTitle.setBounds(120, yPos, 250, 30);
        panel.add(loginTitle);
        yPos += 50;

        // Email label
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setBounds(50, yPos, 100, 25);
        panel.add(emailLabel);

        // Email field
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBounds(150, yPos, 250, 30);
        panel.add(emailField);
        yPos += 50;

        // Password label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(50, yPos, 100, 25);
        panel.add(passwordLabel);

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(150, yPos, 250, 30);
        passwordField.addActionListener(e -> handleLogin());
        panel.add(passwordField);
        yPos += 40;

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(50, yPos, 350, 25);
        panel.add(messageLabel);
        yPos += 40;

        // Login button - MATCH REGISTER BUTTON STYLE
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);  // CHANGED TO FALSE like register
        loginButton.setOpaque(true);
        loginButton.setBounds(150, yPos, 150, 45);
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        registerButton = new JButton("Register Here");
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.setForeground(new Color(52, 152, 219));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegisterFrame());

        panel.add(noAccountLabel);
        panel.add(registerButton);

        return panel;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!validateInput(email, password)) {
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return userDAO.getUserByEmail(email);
            }

            @Override
            protected void done() {
                try {
                    User user = get();

                    if (user == null) {
                        showError("Invalid email or password");
                        return;
                    }

                    if (!PasswordHasher.verifyPassword(password, user.getPassword())) {
                        showError("Invalid email or password");
                        return;
                    }

                    if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
                        handleInactiveAccount(user.getAccountStatus());
                        return;
                    }

                    SessionManager.getInstance().startSession(user);
                    openDashboard(user);

                } catch (Exception e) {
                    showError("Login failed: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };

        worker.execute();
    }

    private boolean validateInput(String email, String password) {
        if (!ValidationUtils.isNotEmpty(email)) {
            showError("Email is required");
            emailField.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError("Invalid email format");
            emailField.requestFocus();
            return false;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            showError("Password is required");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    private void handleInactiveAccount(User.AccountStatus status) {
        switch (status) {
            case PENDING:
                showError(Constants.MSG_ACCOUNT_PENDING);
                break;
            case SUSPENDED:
                showError(Constants.MSG_ACCOUNT_SUSPENDED);
                break;
            case REJECTED:
                showError(Constants.MSG_ACCOUNT_REJECTED);
                break;
        }
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case ADMIN:
                SwingUtilities.invokeLater(() -> {
                    AdminDashboard dashboard = new AdminDashboard();
                    dashboard.setVisible(true);
                });
                dispose();
                break;
            case AGENT:
                SwingUtilities.invokeLater(() -> {
                    AgentDashboard dashboard = new AgentDashboard();
                    dashboard.setVisible(true);
                });
                dispose();
                break;
            case CITIZEN:
                SwingUtilities.invokeLater(() -> {
                    CitizenDashboard dashboard = new CitizenDashboard();
                    dashboard.setVisible(true);
                });
                dispose();
                break;
        }
    }

    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setVisible(true);
        dispose();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(Color.RED);
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(new Color(39, 174, 96));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}