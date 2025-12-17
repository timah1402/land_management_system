package views.citizen.dialogs;

import dao.*;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for filing a new dispute
 */
public class NewDisputeDialog extends JDialog {

    private Citizen currentCitizen;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private DisputeDAO disputeDAO;
    private Runnable onSuccessCallback;

    private JComboBox<String> parcelCombo;
    private JComboBox<String> typeCombo;
    private JComboBox<String> defendantCombo;
    private JComboBox<String> priorityCombo;
    private JTextArea descriptionArea;
    private JTextArea evidenceArea;

    private List<Parcel> allParcels;
    private List<Citizen> allCitizens;

    public NewDisputeDialog(Frame owner, Citizen currentCitizen, ParcelDAO parcelDAO,
                            CitizenDAO citizenDAO, DisputeDAO disputeDAO,
                            Runnable onSuccessCallback) {
        super(owner, "File New Dispute", true);
        this.currentCitizen = currentCitizen;
        this.parcelDAO = parcelDAO;
        this.citizenDAO = citizenDAO;
        this.disputeDAO = disputeDAO;
        this.onSuccessCallback = onSuccessCallback;

        loadData();
        initializeUI();
    }

    private void loadData() {
        allParcels = parcelDAO.getAllParcels();
        allCitizens = citizenDAO.getAllCitizens();
    }

    private void initializeUI() {
        setSize(500, 600);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Parcel Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Select Parcel:"), gbc);

        String[] parcelOptions = createParcelOptions();
        parcelCombo = new JComboBox<>(parcelOptions);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(parcelCombo, gbc);

        // Dispute Type
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Type:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"OWNERSHIP", "BOUNDARY", "USAGE", "INHERITANCE", "OTHER"});
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(typeCombo, gbc);

        // Defendant Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Defendant (optional):"), gbc);

        String[] citizenOptions = createCitizenOptions();
        defendantCombo = new JComboBox<>(citizenOptions);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(defendantCombo, gbc);

        // Priority
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Priority:"), gbc);
        priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH", "URGENT"});
        priorityCombo.setSelectedIndex(1); // Default to MEDIUM
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(priorityCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Description:"), gbc);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(descScroll, gbc);

        // Evidence
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Evidence/Notes:"), gbc);
        evidenceArea = new JTextArea(3, 20);
        evidenceArea.setLineWrap(true);
        JScrollPane evidenceScroll = new JScrollPane(evidenceArea);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(evidenceScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = createSubmitButton();
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(new JScrollPane(panel));
    }

    private String[] createParcelOptions() {
        String[] options = new String[allParcels.size() + 1];
        options[0] = "-- Select Parcel --";

        for (int i = 0; i < allParcels.size(); i++) {
            Parcel parcel = allParcels.get(i);
            options[i + 1] = String.format("ID: %d - %s (%s)",
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getRegion()
            );
        }

        return options;
    }

    private String[] createCitizenOptions() {
        String[] options = new String[allCitizens.size() + 1];
        options[0] = "-- Select Defendant (Optional) --";

        for (int i = 0; i < allCitizens.size(); i++) {
            Citizen citizen = allCitizens.get(i);
            options[i + 1] = String.format("ID: %d - %s %s",
                    citizen.getCitizenId(),
                    citizen.getFirstName(),
                    citizen.getLastName()
            );
        }

        return options;
    }

    private JButton createSubmitButton() {
        JButton submitButton = new JButton("File Dispute");
        submitButton.setBackground(new Color(231, 76, 60));
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(e -> submitDispute());
        return submitButton;
    }

    private void submitDispute() {
        try {
            if (parcelCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a parcel", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (descriptionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide a description", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Dispute dispute = new Dispute();

            // Get selected parcel
            int selectedParcelIndex = parcelCombo.getSelectedIndex();
            Parcel selectedParcel = allParcels.get(selectedParcelIndex - 1);
            dispute.setParcelId(selectedParcel.getParcelId());

            dispute.setComplainantId(currentCitizen.getCitizenId());

            // Get selected defendant
            int selectedDefendantIndex = defendantCombo.getSelectedIndex();
            if (selectedDefendantIndex > 0) {
                Citizen selectedCitizen = allCitizens.get(selectedDefendantIndex - 1);
                dispute.setDefendantId(selectedCitizen.getCitizenId());
            }

            dispute.setType(Dispute.DisputeType.valueOf((String) typeCombo.getSelectedItem()));
            dispute.setDescription(descriptionArea.getText().trim());
            dispute.setStatus(Dispute.DisputeStatus.OPEN);
            dispute.setPriority(Dispute.Priority.valueOf((String) priorityCombo.getSelectedItem()));
            dispute.setOpenedDate(Date.valueOf(LocalDate.now()));
            dispute.setEvidenceProvided(evidenceArea.getText().trim());

            if (disputeDAO.createDispute(dispute)) {
                JOptionPane.showMessageDialog(this,
                        "Dispute filed successfully!\nAn agent will be assigned soon.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to file dispute", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}