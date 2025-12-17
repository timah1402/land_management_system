package views.citizen.panels;

import models.Citizen;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying citizen profile information
 */
public class ProfilePanel extends JPanel {

    private Citizen currentCitizen;

    public ProfilePanel(Citizen currentCitizen) {
        this.currentCitizen = currentCitizen;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        if (currentCitizen != null) {
            add(createProfileInfoPanel());
        } else {
            JLabel errorLabel = new JLabel("Unable to load profile information");
            errorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(errorLabel);
        }
    }

    private JPanel createProfileInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(800, 400));

        addProfileField(infoPanel, "Full Name:", currentCitizen.getFullName());
        addProfileField(infoPanel, "Email:", currentCitizen.getEmail());
        addProfileField(infoPanel, "Phone:", currentCitizen.getPhone());
        addProfileField(infoPanel, "ID Card Number:",
                currentCitizen.getIdCardNumber() != null ? currentCitizen.getIdCardNumber() : "N/A");
        addProfileField(infoPanel, "Date of Birth:",
                currentCitizen.getDateOfBirth() != null ? currentCitizen.getDateOfBirth().toString() : "N/A");
        addProfileField(infoPanel, "Place of Birth:",
                currentCitizen.getPlaceOfBirth() != null ? currentCitizen.getPlaceOfBirth() : "N/A");
        addProfileField(infoPanel, "Address:",
                currentCitizen.getFullAddress() != null ? currentCitizen.getFullAddress() : "N/A");
        addProfileField(infoPanel, "Occupation:",
                currentCitizen.getOccupation() != null ? currentCitizen.getOccupation() : "N/A");
        addProfileField(infoPanel, "Account Status:", currentCitizen.getAccountStatus().toString());
        addProfileField(infoPanel, "Member Since:",
                currentCitizen.getCreatedAt() != null ? currentCitizen.getCreatedAt().toString().substring(0, 10) : "N/A");

        return infoPanel;
    }

    private void addProfileField(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}