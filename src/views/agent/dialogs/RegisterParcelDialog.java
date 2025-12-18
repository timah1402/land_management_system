package views.agent.dialogs;

import dao.*;
import models.*;
import models.Parcel.*;
import utils.ParcelNumberGenerator;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for agents to register new parcels for citizens
 * Implements the Monday scenario: Ibrahima's 5-hectare field registration
 */
public class RegisterParcelDialog extends JDialog {

    private LandAgent currentAgent;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private Runnable onSuccess;

    // Form components
    private JLabel parcelNumberLabel;
    private JTextField landTitleField;
    private JComboBox<String> ownerComboBox;
    private JComboBox<String> landTypeComboBox;
    private JTextField areaField;
    private JComboBox<String> areaUnitComboBox;
    private JTextArea usageArea;
    private JTextField addressField;
    private JLabel regionLabel;
    private JTextField departmentField;
    private JTextField communeField;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JTextField estimatedValueField;
    private JTextArea notesArea;

    private List<Citizen> allCitizens;
    private String generatedParcelNumber;

    public RegisterParcelDialog(Frame parent, LandAgent agent, Runnable onSuccess) {
        super(parent, "Register New Parcel", true);
        this.currentAgent = agent;
        this.parcelDAO = new ParcelDAO();
        this.citizenDAO = new CitizenDAO();
        this.onSuccess = onSuccess;

        // Generate parcel number
        this.generatedParcelNumber = ParcelNumberGenerator.generateParcelNumber(agent.getRegion());

        initializeUI();
        loadCitizens();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setSize(700, 800);
        setLayout(new BorderLayout());

        // Main panel with scroll
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Register New Parcel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Register a new land parcel for a citizen");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Section 1: Parcel Identification (BLUE BACKGROUND)
        JPanel identSection = createSectionPanel("📋 Parcel Identification", new Color(224, 242, 254));
        identSection.add(Box.createRigidArea(new Dimension(0, 10)));

        parcelNumberLabel = new JLabel(generatedParcelNumber);
        parcelNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        parcelNumberLabel.setForeground(new Color(52, 152, 219));
        identSection.add(createFormRow("Parcel Number:", parcelNumberLabel,
                "Auto-generated: " + generatedParcelNumber));
        identSection.add(Box.createRigidArea(new Dimension(0, 10)));

        landTitleField = new JTextField(20);
        identSection.add(createFormRow("Land Title (Titre Foncier):", landTitleField,
                "Optional - Official land title number if available"));

        panel.add(identSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section 2: Owner Information (PURPLE BACKGROUND)
        JPanel ownerSection = createSectionPanel("👤 Owner Information", new Color(243, 233, 250));
        ownerSection.add(Box.createRigidArea(new Dimension(0, 10)));

        ownerComboBox = new JComboBox<>();
        ownerComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        ownerComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        ownerSection.add(createFormRow("Owner (Citizen):", ownerComboBox,
                "Select the citizen who owns this parcel"));

        panel.add(ownerSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section 3: Parcel Details (ORANGE BACKGROUND)
        JPanel detailsSection = createSectionPanel("📐 Parcel Details", new Color(254, 241, 221));
        detailsSection.add(Box.createRigidArea(new Dimension(0, 10)));

        landTypeComboBox = new JComboBox<>(new String[]{
                "RESIDENTIAL", "COMMERCIAL", "AGRICULTURAL", "INDUSTRIAL", "MIXED"
        });
        landTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        landTypeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        detailsSection.add(createFormRow("Land Type:", landTypeComboBox,
                "Type of land use"));
        detailsSection.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel areaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        areaPanel.setOpaque(false);
        areaField = new JTextField(10);
        areaField.setFont(new Font("Arial", Font.PLAIN, 14));
        areaUnitComboBox = new JComboBox<>(new String[]{"HECTARE", "M2"});
        areaUnitComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        areaPanel.add(areaField);
        areaPanel.add(new JLabel("Unit:"));
        areaPanel.add(areaUnitComboBox);
        detailsSection.add(createFormRow("Area:", areaPanel,
                "Total area of the parcel"));
        detailsSection.add(Box.createRigidArea(new Dimension(0, 10)));

        usageArea = new JTextArea(2, 20);
        usageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        usageArea.setLineWrap(true);
        usageArea.setWrapStyleWord(true);
        usageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane usageScroll = new JScrollPane(usageArea);
        usageScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        detailsSection.add(createFormRow("Current Usage:", usageScroll,
                "Describe how the land is currently being used"));
        detailsSection.add(Box.createRigidArea(new Dimension(0, 10)));

        estimatedValueField = new JTextField(20);
        detailsSection.add(createFormRow("Estimated Value (FCFA):", estimatedValueField,
                "Optional - Estimated market value in FCFA"));

        panel.add(detailsSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section 4: Location (RED BACKGROUND)
        JPanel locationSection = createSectionPanel("📍 Location", new Color(254, 226, 226));
        locationSection.add(Box.createRigidArea(new Dimension(0, 10)));

        addressField = new JTextField(20);
        locationSection.add(createFormRow("Address:", addressField,
                "Full address or location description"));
        locationSection.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel regionLabel = new JLabel(currentAgent.getRegion());
        regionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        regionLabel.setForeground(new Color(231, 76, 60));
        locationSection.add(createFormRow("Region:", regionLabel,
                "Auto-filled from your assigned region"));
        locationSection.add(Box.createRigidArea(new Dimension(0, 10)));

        departmentField = new JTextField(20);
        locationSection.add(createFormRow("Department:", departmentField,
                "Optional - Department name"));
        locationSection.add(Box.createRigidArea(new Dimension(0, 10)));

        communeField = new JTextField(20);
        locationSection.add(createFormRow("Commune:", communeField,
                "Optional - Commune name"));
        locationSection.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel gpsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        gpsPanel.setOpaque(false);
        latitudeField = new JTextField(10);
        latitudeField.setFont(new Font("Arial", Font.PLAIN, 14));
        longitudeField = new JTextField(10);
        longitudeField.setFont(new Font("Arial", Font.PLAIN, 14));
        gpsPanel.add(new JLabel("Lat:"));
        gpsPanel.add(latitudeField);
        gpsPanel.add(new JLabel("Long:"));
        gpsPanel.add(longitudeField);
        locationSection.add(createFormRow("GPS Coordinates:", gpsPanel,
                "Optional - Latitude and Longitude"));

        panel.add(locationSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section 5: Additional Notes (GRAY BACKGROUND)
        JPanel notesSection = createSectionPanel("📝 Additional Notes", new Color(236, 240, 241));
        notesSection.add(Box.createRigidArea(new Dimension(0, 10)));

        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        notesSection.add(createFormRow("Notes:", notesScroll,
                "Optional - Any additional information"));

        panel.add(notesSection);

        return panel;
    }

    private JPanel createSectionPanel(String title, Color bgColor) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);

        return section;
    }

    private JPanel createFormRow(String labelText, Component inputComponent, String helpText) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.add(label);
        rowPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Input component
        if (inputComponent instanceof JTextField) {
            JTextField field = (JTextField) inputComponent;
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
        if (inputComponent instanceof JComponent) {
            ((JComponent) inputComponent).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        rowPanel.add(inputComponent);

        // Help text
        if (helpText != null && !helpText.isEmpty()) {
            rowPanel.add(Box.createRigidArea(new Dimension(0, 3)));
            JLabel helpLabel = new JLabel(helpText);
            helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            helpLabel.setForeground(new Color(127, 140, 141));
            helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rowPanel.add(helpLabel);
        }

        return rowPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        // Register button
        JButton registerButton = new JButton("Register Parcel");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(160, 40));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setOpaque(true);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());

        panel.add(cancelButton);
        panel.add(registerButton);

        return panel;
    }

    private void loadCitizens() {
        allCitizens = citizenDAO.getAllCitizens();
        ownerComboBox.removeAllItems();
        ownerComboBox.addItem("-- Select Owner --");

        for (Citizen citizen : allCitizens) {
            String displayName = citizen.getFirstName() + " " + citizen.getLastName() +
                    " (CNI: " + citizen.getIdCardNumber() + ")";
            ownerComboBox.addItem(displayName);
        }
    }

    private void handleRegister() {
        // Validate form
        if (!validateForm()) {
            return;
        }

        // Create parcel object
        Parcel parcel = createParcelFromForm();

        // Debug output
        System.out.println("\n=== REGISTERING PARCEL ===");
        System.out.println("Parcel Number: " + parcel.getParcelNumber());
        System.out.println("Owner ID: " + parcel.getCurrentOwnerId());
        System.out.println("Region: " + parcel.getRegion());
        System.out.println("Area: " + parcel.getArea() + " " + parcel.getAreaUnit());
        System.out.println("Address: " + parcel.getAddress());
        System.out.println("Land Type: " + parcel.getLandType());

        // Save to database
        boolean success = false;
        try {
            success = parcelDAO.createParcel(parcel);
            System.out.println("Database result: " + success);
        } catch (Exception e) {
            System.err.println("EXCEPTION during createParcel:");
            e.printStackTrace();
        }

        if (success) {
            System.out.println("✓ Parcel saved successfully!\n");
            JOptionPane.showMessageDialog(
                    this,
                    "Parcel " + generatedParcelNumber + " registered successfully!\n" +
                            "Owner: " + getSelectedCitizenName(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } else {
            System.err.println("✗ Failed to save parcel!\n");
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to register parcel. Check console for errors.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean validateForm() {
        // Owner selection
        if (ownerComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an owner for this parcel.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // Area
        String areaText = areaField.getText().trim();
        if (areaText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the parcel area.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            areaField.requestFocus();
            return false;
        }

        try {
            double area = Double.parseDouble(areaText);
            if (area <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Area must be greater than 0.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                areaField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number for area.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            areaField.requestFocus();
            return false;
        }

        // Address
        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the parcel address.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            addressField.requestFocus();
            return false;
        }

        // GPS coordinates validation (if provided)
        String lat = latitudeField.getText().trim();
        String lon = longitudeField.getText().trim();
        if (!lat.isEmpty() || !lon.isEmpty()) {
            if (lat.isEmpty() || lon.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please provide both latitude and longitude, or leave both empty.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
        }

        // Estimated value validation (if provided)
        String valueText = estimatedValueField.getText().trim();
        if (!valueText.isEmpty()) {
            try {
                double value = Double.parseDouble(valueText);
                if (value < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Estimated value cannot be negative.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE
                    );
                    estimatedValueField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid number for estimated value.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                estimatedValueField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private Parcel createParcelFromForm() {
        Parcel parcel = new Parcel();

        // Basic info
        parcel.setParcelNumber(generatedParcelNumber);
        parcel.setLandTitle(landTitleField.getText().trim());

        // Owner
        int selectedIndex = ownerComboBox.getSelectedIndex() - 1;
        if (selectedIndex >= 0) {
            Citizen owner = allCitizens.get(selectedIndex);
            parcel.setCurrentOwnerId(owner.getCitizenId());
            parcel.setAcquisitionDate(Date.valueOf(LocalDate.now()));
        }

        // Details
        parcel.setLandType(LandType.valueOf((String) landTypeComboBox.getSelectedItem()));
        parcel.setArea(Double.parseDouble(areaField.getText().trim()));
        parcel.setAreaUnit(AreaUnit.valueOf((String) areaUnitComboBox.getSelectedItem()));
        parcel.setCurrentUsage(usageArea.getText().trim());

        // Location
        parcel.setAddress(addressField.getText().trim());
        parcel.setRegion(currentAgent.getRegion());
        parcel.setDepartment(departmentField.getText().trim());
        parcel.setCommune(communeField.getText().trim());

        // GPS
        String lat = latitudeField.getText().trim();
        String lon = longitudeField.getText().trim();
        if (!lat.isEmpty() && !lon.isEmpty()) {
            parcel.setGpsCoordinates(lat + ", " + lon);
        }

        // Value
        String valueText = estimatedValueField.getText().trim();
        if (!valueText.isEmpty()) {
            parcel.setEstimatedValue(new BigDecimal(valueText));
        }

        // Status and notes
        parcel.setStatus(ParcelStatus.OCCUPIED); // Occupied because it has an owner
        parcel.setNotes(notesArea.getText().trim());

        return parcel;
    }

    private String getSelectedCitizenName() {
        int selectedIndex = ownerComboBox.getSelectedIndex() - 1;
        if (selectedIndex >= 0) {
            Citizen owner = allCitizens.get(selectedIndex);
            return owner.getFirstName() + " " + owner.getLastName();
        }
        return "";
    }
}