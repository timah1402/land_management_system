package views.admin.dialogs;

import dao.CitizenDAO;
import dao.ParcelDAO;
import models.Citizen;
import models.Parcel;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dialog for adding a new parcel to the system
 */
public class AddParcelDialog extends JDialog {

    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private Runnable onSuccessCallback;

    private JTextField parcelNumberField;
    private JTextField landTitleField;
    private JTextField areaField;
    private JComboBox<String> landTypeCombo;
    private JTextField addressField;
    private JComboBox<String> regionCombo;
    private JTextField gpsField;
    private JComboBox<String> ownerCombo;
    private JTextField valueField;

    private List<Citizen> allCitizens;

    public AddParcelDialog(Frame owner, ParcelDAO parcelDAO, CitizenDAO citizenDAO,
                           Runnable onSuccessCallback) {
        super(owner, "Add New Parcel", true);
        this.parcelDAO = parcelDAO;
        this.citizenDAO = citizenDAO;
        this.onSuccessCallback = onSuccessCallback;

        loadData();
        initializeUI();
    }

    private void loadData() {
        allCitizens = citizenDAO.getAllCitizens();
    }

    private void initializeUI() {
        setSize(600, 750);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Parcel Number
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Parcel Number:"), gbc);
        parcelNumberField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(parcelNumberField, gbc);

        // Land Title
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Land Title:"), gbc);
        landTitleField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(landTitleField, gbc);

        // Area
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Area (hectares):"), gbc);
        areaField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(areaField, gbc);

        // Land Type
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Land Type:"), gbc);
        landTypeCombo = new JComboBox<>(new String[]{"RESIDENTIAL", "COMMERCIAL", "AGRICULTURAL", "INDUSTRIAL", "MIXED"});
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(landTypeCombo, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(addressField, gbc);

        // Region
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Region:"), gbc);
        regionCombo = new JComboBox<>(Constants.REGIONS);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(regionCombo, gbc);

        // GPS Coordinates
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("GPS Coordinates:"), gbc);
        gpsField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(gpsField, gbc);

        // Owner Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Owner (Citizen):"), gbc);

        String[] citizenOptions = createCitizenOptions();
        ownerCombo = new JComboBox<>(citizenOptions);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(ownerCombo, gbc);

        // Estimated Value
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Estimated Value (XOF):"), gbc);
        valueField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(valueField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = createSaveButton();
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(new JScrollPane(panel));
    }

    private String[] createCitizenOptions() {
        String[] options = new String[allCitizens.size() + 1];
        options[0] = "-- Select Owner (Optional) --";

        for (int i = 0; i < allCitizens.size(); i++) {
            Citizen citizen = allCitizens.get(i);
            options[i + 1] = String.format("ID: %d - %s %s (%s)",
                    citizen.getCitizenId(),
                    citizen.getFirstName(),
                    citizen.getLastName(),
                    citizen.getEmail()
            );
        }

        return options;
    }

    private JButton createSaveButton() {
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> saveParcel());
        return saveButton;
    }

    private void saveParcel() {
        // Validate required fields
        if (parcelNumberField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill required fields (Parcel Number & Address)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Parcel parcel = new Parcel();
            parcel.setParcelNumber(parcelNumberField.getText().trim());
            parcel.setLandTitle(landTitleField.getText().trim());
            parcel.setArea(Double.parseDouble(areaField.getText().trim()));
            parcel.setAreaUnit(Parcel.AreaUnit.HECTARE);
            parcel.setLandType(Parcel.LandType.valueOf((String) landTypeCombo.getSelectedItem()));
            parcel.setAddress(addressField.getText().trim());
            parcel.setRegion((String) regionCombo.getSelectedItem());
            parcel.setGpsCoordinates(gpsField.getText().trim());

            // Set owner if selected
            int selectedOwnerIndex = ownerCombo.getSelectedIndex();
            if (selectedOwnerIndex > 0) {
                Citizen selectedCitizen = allCitizens.get(selectedOwnerIndex - 1);
                parcel.setCurrentOwnerId(selectedCitizen.getCitizenId());
                parcel.setStatus(Parcel.ParcelStatus.OCCUPIED);
            } else {
                parcel.setStatus(Parcel.ParcelStatus.AVAILABLE);
            }

            // Set estimated value if provided
            if (!valueField.getText().trim().isEmpty()) {
                parcel.setEstimatedValue(new BigDecimal(valueField.getText().trim()));
            }

            if (parcelDAO.createParcel(parcel)) {
                JOptionPane.showMessageDialog(this, "Parcel created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create parcel", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}