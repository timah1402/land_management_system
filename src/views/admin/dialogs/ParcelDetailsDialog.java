package views.admin.dialogs;

import models.Parcel;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for displaying detailed parcel information (Admin version)
 */
public class ParcelDetailsDialog extends JDialog {

    private Parcel parcel;

    public ParcelDetailsDialog(Frame owner, Parcel parcel) {
        super(owner, "Parcel Details", true);
        this.parcel = parcel;
        initializeUI();
    }

    private void initializeUI() {
        setSize(500, 600);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Parcel Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addField(infoPanel, "Parcel ID:", String.valueOf(parcel.getParcelId()));
        addField(infoPanel, "Parcel Number:", parcel.getParcelNumber());
        addField(infoPanel, "Land Title:", parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A");
        addField(infoPanel, "Area:", String.format("%.2f %s", parcel.getArea(), parcel.getAreaUnit()));
        addField(infoPanel, "Land Type:", parcel.getLandType().toString());
        addField(infoPanel, "Status:", parcel.getStatus().toString());
        addField(infoPanel, "Address:", parcel.getAddress());
        addField(infoPanel, "Region:", parcel.getRegion());
        addField(infoPanel, "GPS Coordinates:", parcel.getGpsCoordinates() != null ? parcel.getGpsCoordinates() : "N/A");
        addField(infoPanel, "Estimated Value:", parcel.getEstimatedValue() != null ?
                String.format("%.2f XOF", parcel.getEstimatedValue()) : "N/A");
        addField(infoPanel, "Current Owner ID:", parcel.getCurrentOwnerId() > 0 ?
                String.valueOf(parcel.getCurrentOwnerId()) : "Unassigned");
        addField(infoPanel, "Acquisition Date:", parcel.getAcquisitionDate() != null ?
                parcel.getAcquisitionDate().toString() : "N/A");

        panel.add(infoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);

        add(new JScrollPane(panel));
    }

    private void addField(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}