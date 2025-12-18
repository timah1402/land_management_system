package views.agent.dialogs;

import dao.*;
import models.*;
import models.Transaction.*;
import utils.ParcelNumberGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPROVED Multi-step wizard for land transfers with INHERITANCE support
 * Supports: SALE, TRANSFER, and INHERITANCE (with land division like Papa Samba)
 */
public class ProcessTransferDialog extends JDialog {

    private LandAgent currentAgent;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private TransactionDAO transactionDAO;
    private Runnable onSuccess;

    // Wizard steps
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private int currentStep = 0;

    // Step 1: Select Parcel
    private JTextField parcelSearchField;
    private JButton searchButton;
    private JPanel parcelInfoPanel;
    private Parcel selectedParcel;

    // Step 2: Transfer Details
    private JLabel currentOwnerLabel;
    private JComboBox<String> newOwnerCombo;
    private JComboBox<String> transferTypeCombo;
    private JTextField salePriceField;
    private JTextArea descriptionArea;
    private JPanel salePricePanel;
    private JPanel heirsListPanel;
    private JButton generateHeirsButton;
    private List<JComboBox<String>> heirComboBoxes;

    // INHERITANCE fields
    private JPanel inheritancePanel;
    private JCheckBox divideParcelCheckbox;
    private JPanel heirsPanel;
    private JTextField numberOfHeirsField;

    // Step 3: Review
    private JTextArea reviewArea;

    // Navigation buttons
    private JButton backButton;
    private JButton nextButton;
    private JButton submitButton;

    private List<Citizen> allCitizens;

    public ProcessTransferDialog(Frame parent, LandAgent agent, Runnable onSuccess) {
        super(parent, "Process Land Transfer", true);
        this.currentAgent = agent;
        this.parcelDAO = new ParcelDAO();
        this.citizenDAO = new CitizenDAO();
        this.transactionDAO = new TransactionDAO();
        this.onSuccess = onSuccess;
        this.heirComboBoxes = new ArrayList<>(); // ✅ Initialize here!

        initializeUI();
        loadCitizens();
        setLocationRelativeTo(parent);
    }
    private void initializeUI() {
        setSize(800, 700);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(new Color(236, 240, 241));

        cardsPanel.add(createStep1Panel(), "STEP1");
        cardsPanel.add(createStep2Panel(), "STEP2");
        cardsPanel.add(createStep3Panel(), "STEP3");

        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        mainPanel.add(createNavigationPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        updateNavigationButtons();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("🔄 Process Land Transfer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Transfer land ownership between citizens");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);

        JLabel stepLabel = new JLabel("Step 1 of 3");
        stepLabel.setFont(new Font("Arial", Font.BOLD, 14));
        stepLabel.setForeground(Color.WHITE);
        stepLabel.setName("stepIndicator");
        panel.add(stepLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStep1Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Section with colored background
        JPanel searchSection = createColoredSection("🔍 Search Parcel", new Color(224, 242, 254));

        JLabel instruction = new JLabel("Enter the parcel number to transfer");
        instruction.setFont(new Font("Arial", Font.PLAIN, 13));
        instruction.setForeground(new Color(127, 140, 141));
        instruction.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchSection.add(instruction);
        searchSection.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLabel = new JLabel("Parcel Number:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchPanel.add(searchLabel);

        parcelSearchField = new JTextField(20);
        parcelSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        parcelSearchField.setPreferredSize(new Dimension(250, 32));
        parcelSearchField.addActionListener(e -> searchParcel());
        searchPanel.add(parcelSearchField);

        searchButton = createStyledButton("🔍 Search", new Color(52, 152, 219));
        searchButton.setPreferredSize(new Dimension(100, 32));
        searchButton.addActionListener(e -> searchParcel());
        searchPanel.add(searchButton);

        searchSection.add(searchPanel);

        panel.add(searchSection);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Parcel info panel (initially hidden)
        parcelInfoPanel = new JPanel();
        parcelInfoPanel.setLayout(new BoxLayout(parcelInfoPanel, BoxLayout.Y_AXIS));
        parcelInfoPanel.setBackground(new Color(232, 245, 233));
        parcelInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        parcelInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parcelInfoPanel.setVisible(false);

        JScrollPane scrollPane = new JScrollPane(parcelInfoPanel);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        panel.add(scrollPane);

        return panel;
    }

    private JPanel createStep2Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Current Owner Section
        JPanel ownerSection = createColoredSection("👤 Current Owner", new Color(243, 233, 250));
        currentOwnerLabel = new JLabel("N/A");
        currentOwnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentOwnerLabel.setForeground(new Color(155, 89, 182));
        ownerSection.add(currentOwnerLabel);
        panel.add(ownerSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Transfer Type Section
        JPanel typeSection = createColoredSection("🔄 Transfer Type", new Color(254, 241, 221));
        transferTypeCombo = new JComboBox<>(new String[]{
                "SALE", "TRANSFER", "INHERITANCE"
        });
        transferTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        transferTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        transferTypeCombo.setBackground(Color.WHITE);
        transferTypeCombo.addActionListener(e -> handleTransferTypeChange());
        typeSection.add(transferTypeCombo);
        panel.add(typeSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // INHERITANCE PANEL (Papa Samba scenario!)
        inheritancePanel = createInheritancePanel();
        panel.add(inheritancePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // New Owner Section
        JPanel newOwnerSection = createColoredSection("👥 New Owner/Heir", new Color(224, 242, 254));
        newOwnerCombo = new JComboBox<>();
        newOwnerCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        newOwnerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        newOwnerCombo.setBackground(Color.WHITE);
        newOwnerSection.add(newOwnerCombo);
        panel.add(newOwnerSection);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Sale Price Panel (conditional)
        salePricePanel = createColoredSection("💰 Sale Price", new Color(255, 235, 238));
        salePriceField = new JTextField();
        salePriceField.setFont(new Font("Arial", Font.PLAIN, 14));
        salePriceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JPanel priceInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        priceInputPanel.setOpaque(false);
        priceInputPanel.add(salePriceField);
        JLabel fcfaLabel = new JLabel("FCFA");
        fcfaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        priceInputPanel.add(fcfaLabel);
        salePricePanel.add(priceInputPanel);
        panel.add(salePricePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Description Section
        JPanel descSection = createColoredSection("📝 Description", new Color(236, 240, 241));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        descSection.add(descScroll);
        panel.add(descSection);

        return panel;
    }

    private JPanel createInheritancePanel() {
        inheritancePanel = new JPanel();
        inheritancePanel.setLayout(new BoxLayout(inheritancePanel, BoxLayout.Y_AXIS));
        inheritancePanel.setBackground(new Color(255, 243, 224));
        inheritancePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        inheritancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inheritancePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500)); // ✅ Increased height
        inheritancePanel.setVisible(false);

        JLabel title = new JLabel("⚠️ Inheritance / Succession Options");
        title.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(new Color(230, 126, 34));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        inheritancePanel.add(title);
        inheritancePanel.add(Box.createRigidArea(new Dimension(0, 12)));

        divideParcelCheckbox = new JCheckBox("Divide parcel among multiple heirs (Papa Samba scenario)");
        divideParcelCheckbox.setFont(new Font("Arial", Font.PLAIN, 13));
        divideParcelCheckbox.setOpaque(false);
        divideParcelCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        divideParcelCheckbox.addActionListener(e -> toggleHeirsPanel());
        inheritancePanel.add(divideParcelCheckbox);
        inheritancePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Heirs panel
        heirsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        heirsPanel.setOpaque(false);
        heirsPanel.setVisible(false);

        JLabel heirsLabel = new JLabel("Number of heirs:");
        heirsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        heirsPanel.add(heirsLabel);

        numberOfHeirsField = new JTextField(5);
        numberOfHeirsField.setFont(new Font("Arial", Font.PLAIN, 13));
        heirsPanel.add(numberOfHeirsField);

        // ✅ ADD BUTTON TO GENERATE HEIR FIELDS
        generateHeirsButton = createStyledButton("Generate Heir Fields", new Color(230, 126, 34));
        generateHeirsButton.setPreferredSize(new Dimension(160, 30));
        generateHeirsButton.addActionListener(e -> generateHeirFields());
        heirsPanel.add(generateHeirsButton);

        JLabel note = new JLabel("(e.g., Papa Samba: 4 children)");
        note.setFont(new Font("Arial", Font.ITALIC, 11));
        note.setForeground(new Color(127, 140, 141));
        heirsPanel.add(note);

        inheritancePanel.add(heirsPanel);
        inheritancePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // ✅ HEIRS LIST PANEL (where dropdowns will appear)
        heirsListPanel = new JPanel();
        heirsListPanel.setLayout(new BoxLayout(heirsListPanel, BoxLayout.Y_AXIS));
        heirsListPanel.setOpaque(false);
        heirsListPanel.setVisible(false);
        heirsListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inheritancePanel.add(heirsListPanel);

        JLabel warning = new JLabel("⚠️ Each heir will get equal share of the land");
        warning.setFont(new Font("Arial", Font.ITALIC, 12));
        warning.setForeground(new Color(230, 126, 34));
        warning.setAlignmentX(Component.LEFT_ALIGNMENT);
        inheritancePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        inheritancePanel.add(warning);

        return inheritancePanel;
    }
    private void generateHeirFields() {
        String heirsText = numberOfHeirsField.getText().trim();

        if (heirsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the number of heirs first.",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numHeirs;
        try {
            numHeirs = Integer.parseInt(heirsText);
            if (numHeirs < 2 || numHeirs > 10) {
                JOptionPane.showMessageDialog(this, "Number of heirs must be between 2 and 10.",
                        "Invalid Number", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        heirsListPanel.removeAll();
        heirComboBoxes.clear();
        // Title
        JLabel selectLabel = new JLabel("Select each heir from the citizen database:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 13));
        selectLabel.setForeground(new Color(230, 126, 34));
        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        heirsListPanel.add(selectLabel);
        heirsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create dropdown for EACH heir
        for (int i = 1; i <= numHeirs; i++) {
            JPanel heirRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            heirRow.setOpaque(false);
            heirRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel label = new JLabel("Heir " + i + ":");
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setPreferredSize(new Dimension(60, 25));
            heirRow.add(label);

            JComboBox<String> heirCombo = new JComboBox<>();
            heirCombo.setFont(new Font("Arial", Font.PLAIN, 12));
            heirCombo.setPreferredSize(new Dimension(400, 30));
            heirCombo.setBackground(Color.WHITE);

            // Populate with citizens (excluding current owner)
            heirCombo.addItem("-- Select Heir " + i + " --");
            for (Citizen citizen : allCitizens) {
                if (selectedParcel != null && citizen.getCitizenId() != selectedParcel.getCurrentOwnerId()) {
                    String displayName = citizen.getFirstName() + " " + citizen.getLastName() +
                            " (CNI: " + citizen.getIdCardNumber() + ")";
                    heirCombo.addItem(displayName);
                }
            }

            heirComboBoxes.add(heirCombo);
            heirRow.add(heirCombo);
            heirsListPanel.add(heirRow);
        }

        // Show area per heir
        double areaPerHeir = selectedParcel.getArea() / numHeirs;
        JLabel areaLabel = new JLabel(String.format("✓ Each heir receives: %.2f %s",
                areaPerHeir, selectedParcel.getAreaUnit()));
        areaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        areaLabel.setForeground(new Color(46, 204, 113));
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        heirsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        heirsListPanel.add(areaLabel);

        // Show the list
        heirsListPanel.setVisible(true);
        heirsListPanel.revalidate();
        heirsListPanel.repaint();
        inheritancePanel.revalidate();
        inheritancePanel.repaint();
    }


    private JPanel createStep3Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel reviewSection = createColoredSection("✅ Review Transfer", new Color(232, 245, 233));

        JLabel instruction = new JLabel("Please review all details before submitting");
        instruction.setFont(new Font("Arial", Font.PLAIN, 13));
        instruction.setForeground(new Color(127, 140, 141));
        instruction.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewSection.add(instruction);
        reviewSection.add(Box.createRigidArea(new Dimension(0, 15)));

        reviewArea = new JTextArea();
        reviewArea.setEditable(false);
        reviewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBackground(Color.WHITE);
        reviewArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(reviewArea);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        reviewSection.add(scrollPane);

        panel.add(reviewSection);

        return panel;
    }

    private JPanel createColoredSection(String title, Color bgColor) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 10)));

        return section;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        backButton = createStyledButton("← Back", new Color(127, 140, 141));
        backButton.addActionListener(e -> previousStep());
        panel.add(backButton);

        nextButton = createStyledButton("Next →", new Color(52, 152, 219));
        nextButton.addActionListener(e -> nextStep());
        panel.add(nextButton);

        submitButton = createStyledButton("✓ Submit Transfer", new Color(46, 204, 113));
        submitButton.setPreferredSize(new Dimension(160, 38));
        submitButton.setVisible(false);
        submitButton.addActionListener(e -> submitTransfer());
        panel.add(submitButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(100, 38));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadCitizens() {
        allCitizens = citizenDAO.getAllCitizens();
    }

    private void searchParcel() {
        String parcelNumber = parcelSearchField.getText().trim();

        if (parcelNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a parcel number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedParcel = parcelDAO.getParcelByNumber(parcelNumber);

        if (selectedParcel == null) {
            JOptionPane.showMessageDialog(this, "Parcel not found. Please check the parcel number.",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            parcelInfoPanel.setVisible(false);
            return;
        }

        if (!selectedParcel.getRegion().equals(currentAgent.getRegion())) {
            JOptionPane.showMessageDialog(this,
                    "This parcel is not in your region (" + currentAgent.getRegion() + ").",
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
            selectedParcel = null;
            parcelInfoPanel.setVisible(false);
            return;
        }

        if (selectedParcel.getCurrentOwnerId() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "This parcel has no current owner. Cannot process transfer.",
                    "No Owner", JOptionPane.WARNING_MESSAGE);
            selectedParcel = null;
            parcelInfoPanel.setVisible(false);
            return;
        }

        if (selectedParcel.getStatus() == Parcel.ParcelStatus.IN_TRANSACTION) {
            JOptionPane.showMessageDialog(this,
                    "This parcel is already in a transaction.",
                    "Already In Transaction", JOptionPane.WARNING_MESSAGE);
            selectedParcel = null;
            parcelInfoPanel.setVisible(false);
            return;
        }

        displayParcelInfo();
    }

    private void displayParcelInfo() {
        parcelInfoPanel.removeAll();
        parcelInfoPanel.setVisible(true);

        Citizen owner = citizenDAO.getCitizenById(selectedParcel.getCurrentOwnerId());
        String ownerName = owner != null ? owner.getFirstName() + " " + owner.getLastName() : "Unknown";

        JLabel successLabel = new JLabel("✓ Parcel Found!");
        successLabel.setFont(new Font("Arial", Font.BOLD, 16));
        successLabel.setForeground(new Color(46, 204, 113));
        successLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parcelInfoPanel.add(successLabel);
        parcelInfoPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addInfoRow(parcelInfoPanel, "Parcel Number:", selectedParcel.getParcelNumber());
        addInfoRow(parcelInfoPanel, "Type:", selectedParcel.getLandType().toString());
        addInfoRow(parcelInfoPanel, "Area:", selectedParcel.getArea() + " " + selectedParcel.getAreaUnit());
        addInfoRow(parcelInfoPanel, "Address:", selectedParcel.getAddress());
        addInfoRow(parcelInfoPanel, "Current Owner:", ownerName);
        addInfoRow(parcelInfoPanel, "Status:", selectedParcel.getStatus().toString());

        parcelInfoPanel.revalidate();
        parcelInfoPanel.repaint();
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 13));
        labelComp.setForeground(new Color(52, 73, 94));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(labelComp);
        row.add(valueComp);

        panel.add(row);
    }

    private void handleTransferTypeChange() {
        String transferType = (String) transferTypeCombo.getSelectedItem();
        salePricePanel.setVisible("SALE".equals(transferType));
        inheritancePanel.setVisible("INHERITANCE".equals(transferType));
    }

    private void toggleHeirsPanel() {
        boolean divided = divideParcelCheckbox.isSelected();
        heirsPanel.setVisible(divided);

        if (!divided) {
            heirsListPanel.setVisible(false);
            heirsListPanel.removeAll();
            heirComboBoxes.clear();
            numberOfHeirsField.setText("");
        }
    }

    private void nextStep() {
        if (currentStep == 0) {
            if (selectedParcel == null) {
                JOptionPane.showMessageDialog(this, "Please search and select a parcel first.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            populateStep2();
        } else if (currentStep == 1) {
            if (!validateStep2()) {
                return;
            }
            populateStep3();
        }

        currentStep++;
        updateWizard();
    }

    private void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            updateWizard();
        }
    }

    private void updateWizard() {
        switch (currentStep) {
            case 0: cardLayout.show(cardsPanel, "STEP1"); break;
            case 1: cardLayout.show(cardsPanel, "STEP2"); break;
            case 2: cardLayout.show(cardsPanel, "STEP3"); break;
        }

        updateNavigationButtons();
        updateStepIndicator();
    }

    private void updateNavigationButtons() {
        backButton.setVisible(currentStep > 0);
        nextButton.setVisible(currentStep < 2);
        submitButton.setVisible(currentStep == 2);
    }

    private void updateStepIndicator() {
        Container header = (Container) ((JPanel) getContentPane().getComponent(0)).getComponent(0);
        for (Component comp : header.getComponents()) {
            if (comp instanceof JLabel && "stepIndicator".equals(comp.getName())) {
                ((JLabel) comp).setText("Step " + (currentStep + 1) + " of 3");
                break;
            }
        }
    }

    private void populateStep2() {
        Citizen owner = citizenDAO.getCitizenById(selectedParcel.getCurrentOwnerId());
        if (owner != null) {
            currentOwnerLabel.setText(owner.getFirstName() + " " + owner.getLastName() +
                    " (CNI: " + owner.getIdCardNumber() + ")");
        }

        newOwnerCombo.removeAllItems();
        newOwnerCombo.addItem("-- Select New Owner --");

        for (Citizen citizen : allCitizens) {
            if (citizen.getCitizenId() != selectedParcel.getCurrentOwnerId()) {
                String displayName = citizen.getFirstName() + " " + citizen.getLastName() +
                        " (CNI: " + citizen.getIdCardNumber() + ")";
                newOwnerCombo.addItem(displayName);
            }
        }

        transferTypeCombo.setSelectedIndex(0);
        salePriceField.setText("");
        descriptionArea.setText("");
        divideParcelCheckbox.setSelected(false);
        numberOfHeirsField.setText("");
        handleTransferTypeChange();
    }

    private boolean validateStep2() {
        String transferType = (String) transferTypeCombo.getSelectedItem();

        // ✅ Check transfer type selected
        if (transferType == null || transferType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a transfer type.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // ✅ INHERITANCE with division - validate heirs list
        if ("INHERITANCE".equals(transferType) && divideParcelCheckbox.isSelected()) {
            if (heirComboBoxes == null || heirComboBoxes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please click 'Generate Heir Fields' button first.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Check all heir dropdowns are selected
            for (int i = 0; i < heirComboBoxes.size(); i++) {
                JComboBox<String> combo = heirComboBoxes.get(i);
                if (combo.getSelectedIndex() <= 0) {
                    JOptionPane.showMessageDialog(this, "Please select Heir " + (i + 1),
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // Check for duplicates
            java.util.Set<Integer> selectedIndices = new java.util.HashSet<>();
            for (JComboBox<String> combo : heirComboBoxes) {
                if (!selectedIndices.add(combo.getSelectedIndex())) {
                    JOptionPane.showMessageDialog(this, "Cannot select the same heir multiple times.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            return true; // ✅ Valid - DON'T check newOwnerCombo!
        }

        // ✅ For non-division transfers
        if (newOwnerCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select the new owner.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // ✅ Validate sale price
        if ("SALE".equals(transferType)) {
            String priceText = salePriceField.getText().trim();
            if (priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the sale price.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                salePriceField.requestFocus();
                return false;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Sale price must be greater than 0.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    salePriceField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for sale price.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                salePriceField.requestFocus();
                return false;
            }
        }

        return true;
    }


    private void populateStep3() {
        Citizen currentOwner = citizenDAO.getCitizenById(selectedParcel.getCurrentOwnerId());
        String transferType = (String) transferTypeCombo.getSelectedItem();

        StringBuilder review = new StringBuilder();
        review.append("═══════════════════════════════════════════\n");
        review.append("           TRANSFER REVIEW\n");
        review.append("═══════════════════════════════════════════\n\n");
        review.append("PARCEL INFORMATION:\n");
        review.append("  • Parcel Number: ").append(selectedParcel.getParcelNumber()).append("\n");
        review.append("  • Type: ").append(selectedParcel.getLandType()).append("\n");
        review.append("  • Area: ").append(String.format("%.2f %s", selectedParcel.getArea(), selectedParcel.getAreaUnit())).append("\n");
        review.append("  • Address: ").append(selectedParcel.getAddress()).append("\n\n");

        review.append("TRANSFER DETAILS:\n");
        review.append("  • From: ").append(currentOwner.getFirstName()).append(" ").append(currentOwner.getLastName());
        review.append(" (CNI: ").append(currentOwner.getIdCardNumber()).append(")\n");
        review.append("  • Transfer Type: ").append(transferType).append("\n");

        // ✅ SHOW HEIRS LIST FOR INHERITANCE DIVISION
        if ("INHERITANCE".equals(transferType) && divideParcelCheckbox.isSelected()) {
            int numHeirs = heirComboBoxes.size();
            review.append("  • ⚠️ LAND DIVISION: YES (Papa Samba Scenario!)\n");
            review.append("  • Number of Heirs: ").append(numHeirs).append("\n");
            review.append("  • Each heir receives: ").append(String.format("%.2f %s",
                    selectedParcel.getArea() / numHeirs, selectedParcel.getAreaUnit())).append("\n\n");
            review.append("HEIRS:\n");

            for (int i = 0; i < numHeirs; i++) {
                JComboBox<String> combo = heirComboBoxes.get(i);
                int heirIndex = combo.getSelectedIndex() - 1; // -1 because first item is "-- Select --"
                if (heirIndex >= 0) {
                    Citizen heir = allCitizens.get(heirIndex);
                    review.append("  ").append(i + 1).append(". ").append(heir.getFirstName()).append(" ").append(heir.getLastName());
                    review.append(" (CNI: ").append(heir.getIdCardNumber()).append(")\n");
                }
            }
            review.append("\n  Note: ").append(numHeirs).append(" NEW parcels will be created!\n");
        } else {
            int newOwnerIndex = newOwnerCombo.getSelectedIndex() - 1;
            Citizen newOwner = allCitizens.get(newOwnerIndex);
            review.append("  • To: ").append(newOwner.getFirstName()).append(" ").append(newOwner.getLastName());
            review.append(" (CNI: ").append(newOwner.getIdCardNumber()).append(")\n");
        }

        if ("SALE".equals(transferType)) {
            review.append("  • Sale Price: ").append(salePriceField.getText()).append(" FCFA\n");
        }

        review.append("  • Date: ").append(LocalDate.now()).append("\n\n");

        review.append("DESCRIPTION:\n");
        review.append(descriptionArea.getText().isEmpty() ? "No description provided" : descriptionArea.getText());
        review.append("\n\n");

        review.append("═══════════════════════════════════════════\n");
        review.append("Agent: ").append(currentAgent.getFirstName()).append(" ").append(currentAgent.getLastName()).append("\n");
        review.append("Region: ").append(currentAgent.getRegion()).append("\n");
        review.append("═══════════════════════════════════════════\n");

        reviewArea.setText(review.toString());
    }



    private void submitTransfer() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to submit this transfer?",
                "Confirm Transfer", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String transferType = (String) transferTypeCombo.getSelectedItem();

        // Handle INHERITANCE with division (Papa Samba scenario!)
        if ("INHERITANCE".equals(transferType) && divideParcelCheckbox.isSelected()) {
            handleInheritanceWithDivision();
            return;
        }

        // Handle regular transfer
        handleRegularTransfer();
    }


    // ========== REPLACE handleInheritanceWithDivision() METHOD ==========

    private void handleInheritanceWithDivision() {
        int numHeirs = heirComboBoxes.size();
        double areaPerHeir = selectedParcel.getArea() / numHeirs;

        // Build heir information for saving
        StringBuilder heirNames = new StringBuilder();
        StringBuilder heirData = new StringBuilder("INHERITANCE WITH DIVISION - " + numHeirs + " heirs:\n");

        // Collect all heir citizen IDs
        List<Integer> heirIds = new ArrayList<>();

        for (int i = 0; i < numHeirs; i++) {
            JComboBox<String> combo = heirComboBoxes.get(i);
            int heirIndex = combo.getSelectedIndex() - 1;
            if (heirIndex >= 0) {
                Citizen heir = allCitizens.get(heirIndex);
                heirIds.add(heir.getCitizenId());

                heirNames.append("\n  ").append(i + 1).append(". ").append(heir.getFirstName())
                        .append(" ").append(heir.getLastName());

                heirData.append("Heir ").append(i + 1).append(": ")
                        .append(heir.getFirstName()).append(" ").append(heir.getLastName())
                        .append(" (ID: ").append(heir.getCitizenId()).append(")\n");
            }
        }

        heirData.append("\nArea per heir: ").append(String.format("%.2f %s", areaPerHeir, selectedParcel.getAreaUnit()));
        heirData.append("\nDescription: ").append(descriptionArea.getText());

        // ✅ CREATE TRANSACTION WITH ALL HEIR IDs
        Transaction transaction = new Transaction();
        transaction.setParcelId(selectedParcel.getParcelId());
        transaction.setPreviousOwnerId(selectedParcel.getCurrentOwnerId());

        // ✅ SAVE FIRST HEIR AS NEW OWNER (primary heir)
        if (!heirIds.isEmpty()) {
            transaction.setNewOwnerId(heirIds.get(0));
        } else {
            transaction.setNewOwnerId(selectedParcel.getCurrentOwnerId());
        }

        transaction.setTransactionDate(Date.valueOf(LocalDate.now()));
        transaction.setType(TransactionType.INHERITANCE);
        transaction.setNotes(heirData.toString());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setValidatingAgentId(currentAgent.getAgentId());

        // ✅ SAVE TO DATABASE
        System.out.println("\n=== SAVING INHERITANCE TRANSACTION ===");
        System.out.println("Parcel ID: " + selectedParcel.getParcelId());
        System.out.println("Number of heirs: " + numHeirs);
        System.out.println("Heir IDs: " + heirIds);

        boolean success = false;
        try {
            success = transactionDAO.createTransaction(transaction);
            System.out.println("Transaction save result: " + success);
        } catch (Exception e) {
            System.err.println("EXCEPTION during transaction creation:");
            e.printStackTrace();
        }

        if (success) {
            System.out.println("✓ Transaction saved successfully!\n");

            // ✅ UPDATE PARCEL STATUS
            parcelDAO.updateParcelStatus(selectedParcel.getParcelId(), Parcel.ParcelStatus.IN_TRANSACTION);

            JOptionPane.showMessageDialog(this,
                    "✓ INHERITANCE WITH DIVISION INITIATED!\n\n" +
                            "Original Parcel: " + selectedParcel.getParcelNumber() + "\n" +
                            "Total Area: " + selectedParcel.getArea() + " " + selectedParcel.getAreaUnit() + "\n\n" +
                            "Will create " + numHeirs + " new parcels:\n" +
                            "Each heir receives: " + String.format("%.2f %s", areaPerHeir, selectedParcel.getAreaUnit()) + "\n\n" +
                            "Heirs:" + heirNames.toString() + "\n\n" +
                            "Status: PENDING approval\n\n" +
                            "Transaction ID: " + transaction.getTransactionId(),
                    "Papa Samba Inheritance",
                    JOptionPane.INFORMATION_MESSAGE);

            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } else {
            System.err.println("✗ Failed to save transaction!\n");
            JOptionPane.showMessageDialog(this,
                    "Failed to create inheritance transaction.\nCheck console for errors.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


// ========== ALSO ADD THIS DEBUG TO handleRegularTransfer() ==========

    private void handleRegularTransfer() {
        Transaction transaction = new Transaction();
        int newOwnerIndex = newOwnerCombo.getSelectedIndex() - 1;
        Citizen newOwner = allCitizens.get(newOwnerIndex);

        transaction.setParcelId(selectedParcel.getParcelId());
        transaction.setPreviousOwnerId(selectedParcel.getCurrentOwnerId());
        transaction.setNewOwnerId(newOwner.getCitizenId());
        transaction.setTransactionDate(Date.valueOf(LocalDate.now()));

        String transferType = (String) transferTypeCombo.getSelectedItem();
        transaction.setType(TransactionType.valueOf(transferType));

        if ("SALE".equals(transferType)) {
            transaction.setAmount(new BigDecimal(salePriceField.getText().trim()));
        }

        transaction.setNotes(descriptionArea.getText().trim());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setValidatingAgentId(currentAgent.getAgentId());

        // ✅ ADD DEBUG OUTPUT
        System.out.println("\n=== SAVING REGULAR TRANSACTION ===");
        System.out.println("Transfer Type: " + transferType);
        System.out.println("Parcel ID: " + selectedParcel.getParcelId());
        System.out.println("Previous Owner: " + selectedParcel.getCurrentOwnerId());
        System.out.println("New Owner: " + newOwner.getCitizenId());

        boolean success = false;
        try {
            success = transactionDAO.createTransaction(transaction);
            System.out.println("Transaction save result: " + success);
        } catch (Exception e) {
            System.err.println("EXCEPTION during transaction creation:");
            e.printStackTrace();
        }

        if (success) {
            System.out.println("✓ Transaction saved successfully!\n");

            parcelDAO.updateParcelStatus(selectedParcel.getParcelId(), Parcel.ParcelStatus.IN_TRANSACTION);

            JOptionPane.showMessageDialog(this,
                    "✓ Transfer initiated successfully!\n\n" +
                            "Parcel: " + selectedParcel.getParcelNumber() + "\n" +
                            "Status: IN_TRANSACTION\n\n" +
                            "The transaction is now pending approval.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } else {
            System.err.println("✗ Failed to save transaction!\n");
            JOptionPane.showMessageDialog(this,
                    "Failed to create transfer.\nCheck console for errors.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }}
