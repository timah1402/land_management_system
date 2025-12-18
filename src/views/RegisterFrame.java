package views;

import dao.CitizenDAO;
import dao.LandAgentDAO;
import dao.UserDAO;
import models.Citizen;
import models.LandAgent;
import models.User;
import utils.PasswordHasher;
import utils.ValidationUtils;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Registration Frame for new users (Citizens and Agents)
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

    // Role selection
    private JComboBox<String> roleComboBox;
    private JComboBox<String> regionComboBox;
    private JPanel agentFieldsPanel;

    private CitizenDAO citizenDAO;
    private LandAgentDAO agentDAO;

    public RegisterFrame() {
        citizenDAO = new CitizenDAO();
        agentDAO = new LandAgentDAO();
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle(Constants.WINDOW_REGISTER);
        setSize(600, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

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

        JLabel subtitleLabel = new JLabel("Register as Citizen or Land Agent");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Account Type
        addSectionTitle(panel, "Account Type", gbc, row++);
        addFormField(panel, "Register as:", gbc, row++);
        roleComboBox = new JComboBox<>(new String[]{"Citizen", "Land Agent"});
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        styleField(roleComboBox);
        roleComboBox.addActionListener(e -> toggleAgentFields());
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(roleComboBox, gbc);

        // Agent region field (hidden initially)
        agentFieldsPanel = createAgentFieldsPanel();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(agentFieldsPanel, gbc);
        gbc.gridwidth = 1;

        // Personal Information
        addSectionTitle(panel, "Personal Information", gbc, row++);
        row = addPersonalFields(panel, gbc, row);

        // Contact Information
        addSectionTitle(panel, "Contact Information", gbc, row++);
        row = addContactFields(panel, gbc, row);

        // Account Security
        addSectionTitle(panel, "Account Security", gbc, row++);
        row = addSecurityFields(panel, gbc, row);

        // Message and Buttons
        row = addMessageAndButtons(panel, gbc, row);

        return panel;
    }

    private JPanel createAgentFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        JLabel regionLabel = new JLabel("Assigned Region:");
        regionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(regionLabel, gbc);

        regionComboBox = new JComboBox<>(Constants.REGIONS);
        regionComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        styleField(regionComboBox);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(regionComboBox, gbc);

        return panel;
    }

    private int addPersonalFields(JPanel panel, GridBagConstraints gbc, int row) {
        addFormField(panel, "First Name:", gbc, row++);
        firstNameField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(firstNameField, gbc);

        addFormField(panel, "Last Name:", gbc, row++);
        lastNameField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(lastNameField, gbc);

        addFormField(panel, "CNI Number:", gbc, row++);
        cniField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(cniField, gbc);

        addFormField(panel, "Birth Date:", gbc, row++);
        birthDateSpinner = new JSpinner(new SpinnerDateModel());
        birthDateSpinner.setEditor(new JSpinner.DateEditor(birthDateSpinner, "dd/MM/yyyy"));
        birthDateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(birthDateSpinner, gbc);

        addFormField(panel, "Birth Place:", gbc, row++);
        birthPlaceField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(birthPlaceField, gbc);

        return row;
    }

    private int addContactFields(JPanel panel, GridBagConstraints gbc, int row) {
        addFormField(panel, "Phone:", gbc, row++);
        phoneField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(phoneField, gbc);

        addFormField(panel, "Email:", gbc, row++);
        emailField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(emailField, gbc);

        addFormField(panel, "Address:", gbc, row++);
        addressField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(addressField, gbc);

        addFormField(panel, "Occupation:", gbc, row++);
        occupationField = createTextField();
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(occupationField, gbc);

        return row;
    }

    private int addSecurityFields(JPanel panel, GridBagConstraints gbc, int row) {
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

        passwordStrengthLabel = new JLabel("");
        passwordStrengthLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(passwordStrengthLabel, gbc);

        addFormField(panel, "Confirm Password:", gbc, row++);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        styleField(confirmPasswordField);
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        panel.add(confirmPasswordField, gbc);

        return row;
    }

    private int addMessageAndButtons(JPanel panel, GridBagConstraints gbc, int row) {
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(39, 174, 96));
        registerButton.addActionListener(e -> handleRegister());

        backButton = new JButton("Back to Login");
        styleButton(backButton, new Color(149, 165, 166));
        backButton.addActionListener(e -> backToLogin());

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridy = row++;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return row;
    }

    private void toggleAgentFields() {
        agentFieldsPanel.setVisible(roleComboBox.getSelectedIndex() == 1);
        revalidate();
        repaint();
    }

    private void addSectionTitle(JPanel panel, String title, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        panel.add(label, gbc);
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        if (!validateAllFields()) return;

        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    boolean isAgent = roleComboBox.getSelectedIndex() == 1;
                    return isAgent ? agentDAO.createLandAgent(createAgentFromForm())
                            : citizenDAO.createCitizen(createCitizenFromForm());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        String role = roleComboBox.getSelectedIndex() == 1 ? "Land Agent" : "Citizen";
                        showSuccess(role + " registration successful! Awaiting admin approval.");
                        new Timer(2000, e -> backToLogin()).start();
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
        }.execute();
    }

    private boolean validateAllFields() {
        if (!ValidationUtils.isValidName(firstNameField.getText())) {
            return showErrorAndFocus("Please enter a valid first name", firstNameField);
        }
        if (!ValidationUtils.isValidName(lastNameField.getText())) {
            return showErrorAndFocus("Please enter a valid last name", lastNameField);
        }
        if (!ValidationUtils.isNotEmpty(cniField.getText())) {
            return showErrorAndFocus("Please enter your CNI number", cniField);
        }
        if (!ValidationUtils.isValidPhone(phoneField.getText())) {
            return showErrorAndFocus("Please enter a valid phone number", phoneField);
        }
        if (!ValidationUtils.isValidEmail(emailField.getText())) {
            return showErrorAndFocus("Please enter a valid email", emailField);
        }
        if (new UserDAO().emailExists(emailField.getText())) {
            return showErrorAndFocus("This email is already registered", emailField);
        }
        if (!ValidationUtils.isNotEmpty(addressField.getText())) {
            return showErrorAndFocus("Please enter your address", addressField);
        }
        if (roleComboBox.getSelectedIndex() == 1 && regionComboBox.getSelectedIndex() == -1) {
            return showErrorAndFocus("Please select a region", regionComboBox);
        }
        String password = new String(passwordField.getPassword());
        if (!PasswordHasher.isPasswordStrong(password)) {
            return showErrorAndFocus("Password must be at least 8 characters with mixed case, numbers, and symbols", passwordField);
        }
        if (!password.equals(new String(confirmPasswordField.getPassword()))) {
            return showErrorAndFocus("Passwords do not match", confirmPasswordField);
        }
        return true;
    }

    private boolean showErrorAndFocus(String message, JComponent component) {
        showError(message);
        component.requestFocus();
        return false;
    }

    private Citizen createCitizenFromForm() {
        Citizen citizen = new Citizen();
        citizen.setFirstName(firstNameField.getText().trim());
        citizen.setLastName(lastNameField.getText().trim());
        citizen.setEmail(emailField.getText().trim());
        citizen.setPhone(phoneField.getText().trim());
        citizen.setPassword(PasswordHasher.hashPassword(new String(passwordField.getPassword())));
        citizen.setRole(User.UserRole.CITIZEN);
        citizen.setAccountStatus(User.AccountStatus.PENDING);
        citizen.setIdCardNumber(cniField.getText().replaceAll("[\\s-]", ""));
        citizen.setDateOfBirth(new Date(((java.util.Date) birthDateSpinner.getValue()).getTime()));
        citizen.setPlaceOfBirth(birthPlaceField.getText().trim());
        citizen.setFullAddress(addressField.getText().trim());
        citizen.setOccupation(occupationField.getText().trim());
        return citizen;
    }

    private LandAgent createAgentFromForm() {
        LandAgent agent = new LandAgent();
        agent.setFirstName(firstNameField.getText().trim());
        agent.setLastName(lastNameField.getText().trim());
        agent.setEmail(emailField.getText().trim());
        agent.setPhone(phoneField.getText().trim());
        agent.setPassword(PasswordHasher.hashPassword(new String(passwordField.getPassword())));
        agent.setRole(User.UserRole.AGENT);
        agent.setAccountStatus(User.AccountStatus.PENDING);
        agent.setRegion((String) regionComboBox.getSelectedItem());
        agent.setRegistrationNumber("AGT-" + System.currentTimeMillis());
        agent.setSpecialization("Land Management");
        agent.setAppointmentDate(Date.valueOf(LocalDate.now()));
        agent.setStatus(LandAgent.AgentStatus.ACTIVE);
        return agent;
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
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