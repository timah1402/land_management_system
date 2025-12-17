package views.agent.dialogs;

import models.Transaction;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for displaying detailed transaction information (Agent version)
 */
public class TransactionDetailsDialog extends JDialog {

    private Transaction transaction;

    public TransactionDetailsDialog(Frame owner, Transaction transaction) {
        super(owner, "Transaction Details", true);
        this.transaction = transaction;
        initializeUI();
    }

    private void initializeUI() {
        setSize(500, 500);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Transaction Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addField(infoPanel, "Transaction ID:", String.valueOf(transaction.getTransactionId()));
        addField(infoPanel, "Parcel ID:", String.valueOf(transaction.getParcelId()));
        addField(infoPanel, "Type:", transaction.getType().toString());
        addField(infoPanel, "Amount:", transaction.getAmount() != null ?
                String.format("%.2f XOF", transaction.getAmount()) : "0.00 XOF");
        addField(infoPanel, "Date:", transaction.getTransactionDate() != null ?
                transaction.getTransactionDate().toString() : "N/A");
        addField(infoPanel, "Status:", transaction.getStatus().toString());
        addField(infoPanel, "Previous Owner:", transaction.getPreviousOwnerId() != null ?
                "Citizen #" + transaction.getPreviousOwnerId() : "N/A");
        addField(infoPanel, "New Owner:", "Citizen #" + transaction.getNewOwnerId());

        panel.add(infoPanel);

        // Notes section
        if (transaction.getNotes() != null && !transaction.getNotes().isEmpty()) {
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            JLabel notesLabel = new JLabel("Notes:");
            notesLabel.setFont(new Font("Arial", Font.BOLD, 12));
            notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(notesLabel);

            JTextArea notesArea = new JTextArea(transaction.getNotes());
            notesArea.setEditable(false);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            notesArea.setRows(3);
            panel.add(new JScrollPane(notesArea));
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
}