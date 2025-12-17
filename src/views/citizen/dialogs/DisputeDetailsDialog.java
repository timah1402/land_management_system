package views.citizen.dialogs;

import models.Dispute;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for displaying detailed dispute information
 */
public class DisputeDetailsDialog extends JDialog {

    private Dispute dispute;

    public DisputeDetailsDialog(Frame owner, Dispute dispute) {
        super(owner, "Dispute Details", true);
        this.dispute = dispute;
        initializeUI();
    }

    private void initializeUI() {
        setSize(600, 650);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Dispute Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addField(infoPanel, "Dispute ID:", String.valueOf(dispute.getDisputeId()));
        addField(infoPanel, "Parcel ID:", String.valueOf(dispute.getParcelId()));
        addField(infoPanel, "Type:", dispute.getType().toString());
        addField(infoPanel, "Status:", dispute.getStatus().toString());
        addField(infoPanel, "Priority:", dispute.getPriority().toString());
        addField(infoPanel, "Opened Date:", dispute.getOpenedDate() != null ?
                dispute.getOpenedDate().toString() : "N/A");
        addField(infoPanel, "Complainant:", "Citizen #" + dispute.getComplainantId());
        addField(infoPanel, "Defendant:", dispute.getDefendantId() != null ?
                "Citizen #" + dispute.getDefendantId() : "N/A");
        addField(infoPanel, "Assigned Agent:", dispute.getAssignedAgentId() != null && dispute.getAssignedAgentId() > 0 ?
                "Agent #" + dispute.getAssignedAgentId() : "Not assigned");
        addField(infoPanel, "Resolution Date:", dispute.getResolutionDate() != null ?
                dispute.getResolutionDate().toString() : "Not yet resolved");

        panel.add(infoPanel);

        // Description section
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        addTextSection(panel, "Description:", dispute.getDescription());

        // Evidence section
        if (dispute.getEvidenceProvided() != null && !dispute.getEvidenceProvided().isEmpty()) {
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            addTextSection(panel, "Evidence/Notes:", dispute.getEvidenceProvided());
        }

        // Resolution section
        if (dispute.getResolution() != null && !dispute.getResolution().isEmpty()) {
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            addTextSection(panel, "Resolution:", dispute.getResolution());
        }

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

    private void addTextSection(JPanel panel, String label, String text) {
        JLabel sectionLabel = new JLabel(label);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionLabel);

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        textArea.setRows(3);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);
    }
}