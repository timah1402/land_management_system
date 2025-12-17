package views;

import dao.CitizenDAO;
import dao.UserDAO;
import models.Citizen;
import models.User;
import utils.PasswordHasher;
import utils.ValidationUtils;
import utils.DateUtils;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

/**
 * Registration Frame for new users
 */
public class RegisterFrame extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField cniField;
    private JTextField birthPlaceField;
    private JTextField addressField;
    private JTextField occupationField;
    private JSpinner birthDateSpinner;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    private JLabel passwordStrengthLabel;

    private CitizenDAO citizenDAO;

    public RegisterFrame() {
        citizenDAO = new CitizenDAO();
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle(Constants.WINDOW_REGISTER);
        setSize(600, 900); // Increased even more
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Changed to true

        // Main panel with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel (scrollable)
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Citizen Registration");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Personal Information Section
        addSectionTitle(panel, "Personal Information", gbc, row++);

        // First Name
        addFormField(panel, "First Name:", gbc, row++);
        firstNameField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(firstNameField, gbc);

        // Last Name
        addFormField(panel, "Last Name:", gbc, row++);
        lastNameField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(lastNameField, gbc);

        // CNI Number
        addFormField(panel, "CNI Number:", gbc, row++);
        cniField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(cniField, gbc);

        // Birth Date
        addFormField(panel, "Birth Date:", gbc, row++);
        SpinnerDateModel dateModel = new SpinnerDateModel();
        birthDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(birthDateSpinner, "dd/MM/yyyy");
        birthDateSpinner.setEditor(dateEditor);
        birthDateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(birthDateSpinner, gbc);

        // Birth Place
        addFormField(panel, "Birth Place:", gbc, row++);
        birthPlaceField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(birthPlaceField, gbc);

        // Contact Information Section
        addSectionTitle(panel, "Contact Information", gbc, row++);

        // Phone
        addFormField(panel, "Phone:", gbc, row++);
        phoneField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(phoneField, gbc);

        // Email
        addFormField(panel, "Email:", gbc, row++);
        emailField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(emailField, gbc);

        // Address
        addFormField(panel, "Address:", gbc, row++);
        addressField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(addressField, gbc);

        // Occupation
        addFormField(panel, "Occupation:", gbc, row++);
        occupationField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(occupationField, gbc);

        // Account Security Section
        addSectionTitle(panel, "Account Security", gbc, row++);

        // Password
        addFormField(panel, "Password:", gbc, row++);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        styleField(passwordField);
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updatePasswordStrength();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(passwordField, gbc);

        // Password strength
        passwordStrengthLabel = new JLabel("");
        passwordStrengthLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(passwordStrengthLabel, gbc);

        // Confirm Password
        addFormField(panel, "Confirm Password:", gbc, row++);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        styleField(confirmPasswordField);
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(confirmPasswordField, gbc);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(39, 174, 96)); // Green
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setOpaque(true); // Important for macOS
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(149, 165, 166)); // Gray
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true); // Important for macOS
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> backToLogin());

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addSectionTitle(JPanel panel, String title, GridBagConstraints gbc, int row) {
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sectionLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        panel.add(sectionLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
    }

    private void addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);
        gbc.weightx = 0.7;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        styleField(field);
        return field;
    }

    private void styleField(JComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        String strength = PasswordHasher.getPasswordStrength(password);

        passwordStrengthLabel.setText("Strength: " + strength);

        switch (strength) {
            case "Very Weak":
            case "Weak":
                passwordStrengthLabel.setForeground(Color.RED);
                break;
            case "Medium":
                passwordStrengthLabel.setForeground(new Color(243, 156, 18));
                break;
            case "Strong":
            case "Very Strong":
                passwordStrengthLabel.setForeground(new Color(39, 174, 96));
                break;
        }
    }

    private void handleRegister() {
        // Validate all fields
        if (!validateAllFields()) {
            return;
        }

        // Disable button during processing
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    // Create citizen object
                    Citizen citizen = createCitizenFromForm();

                    // Save to database
                    return citizenDAO.createCitizen(citizen);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();

                    if (success) {
                        showSuccess("Registration successful! Please wait for approval.");

                        // Redirect to login after delay
                        Timer timer = new Timer(2000, e -> backToLogin());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showError("Registration failed. Please try again.");
                        registerButton.setEnabled(true);
                        registerButton.setText("Register");
                    }
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                }
            }
        };

        worker.execute();
    }

    private boolean validateAllFields() {
        // First Name
        if (!ValidationUtils.isValidName(firstNameField.getText())) {
            showError("Please enter a valid first name");
            firstNameField.requestFocus();
            return false;
        }

        // Last Name
        if (!ValidationUtils.isValidName(lastNameField.getText())) {
            showError("Please enter a valid last name");
            lastNameField.requestFocus();
            return false;
        }

        // CNI
        if (!ValidationUtils.isValidCNI(cniField.getText())) {
            showError("Please enter a valid CNI (13 digits)");
            cniField.requestFocus();
            return false;
        }

        // Check if CNI exists
        if (citizenDAO.idCardExists(cniField.getText().replaceAll("[\\s-]", ""))) {
            showError("This CNI is already registered");
            cniField.requestFocus();
            return false;
        }

        // Phone
        if (!ValidationUtils.isValidPhone(phoneField.getText())) {
            showError("Please enter a valid phone number (9 digits)");
            phoneField.requestFocus();
            return false;
        }

        // Email
        if (!ValidationUtils.isValidEmail(emailField.getText())) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        }

        // Check if email exists
        UserDAO userDAO = new UserDAO();
        if (userDAO.emailExists(emailField.getText())) {
            showError("This email is already registered");
            emailField.requestFocus();
            return false;
        }

        // Address
        if (!ValidationUtils.isNotEmpty(addressField.getText())) {
            showError("Please enter your address");
            addressField.requestFocus();
            return false;
        }

        // Password
        String password = new String(passwordField.getPassword());
        if (!PasswordHasher.isPasswordStrong(password)) {
            showError("Password must be at least 8 characters with mixed case, numbers, and symbols");
            passwordField.requestFocus();
            return false;
        }

        // Confirm Password
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }

    private Citizen createCitizenFromForm() {
        Citizen citizen = new Citizen();

        // User fields
        citizen.setFirstName(firstNameField.getText().trim());
        citizen.setLastName(lastNameField.getText().trim());
        citizen.setEmail(emailField.getText().trim());
        citizen.setPhone(phoneField.getText().trim());
        citizen.setPassword(PasswordHasher.hashPassword(new String(passwordField.getPassword())));
        citizen.setRole(User.UserRole.CITIZEN);
        citizen.setAccountStatus(User.AccountStatus.PENDING);

        // Citizen fields
        citizen.setIdCardNumber(cniField.getText().replaceAll("[\\s-]", ""));
        citizen.setDateOfBirth(new Date(((java.util.Date) birthDateSpinner.getValue()).getTime()));
        citizen.setPlaceOfBirth(birthPlaceField.getText().trim());
        citizen.setFullAddress(addressField.getText().trim());
        citizen.setOccupation(occupationField.getText().trim());

        return citizen;
    }

    private void backToLogin() {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
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
}