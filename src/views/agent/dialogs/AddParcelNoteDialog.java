package views.agent.dialogs;

import dao.ParcelDAO;
import models.Parcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for agents to add notes to parcels
 * Implements the Friday scenario: Adding investigation notes to disputed parcels
 */
public class AddParcelNoteDialog extends JDialog {

    private Parcel parcel;
    private ParcelDAO parcelDAO;
    private Runnable onSuccess;

    // Form components
    private JTextArea noteTextArea;
    private JCheckBox importantCheckBox;

    public AddParcelNoteDialog(Frame parent, Parcel parcel, Runnable onSuccess) {
        super(parent, "Add Note to Parcel", true);
        this.parcel = parcel;
        this.parcelDAO = new ParcelDAO();
        this.onSuccess = onSuccess;

        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setSize(600, 500);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Add Note to Parcel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Subtitle with parcel info
        JLabel subtitleLabel = new JLabel("Parcel: " + parcel.getParcelNumber() + " | " + parcel.getAddress());
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
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Parcel Information Section
        panel.add(createParcelInfoSection());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Note Section
        panel.add(createSectionLabel("📝 Add Your Note"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Note text area
        noteTextArea = new JTextArea(10, 40);
        noteTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        noteTextArea.setLineWrap(true);
        noteTextArea.setWrapStyleWord(true);
        noteTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(noteTextArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.add(scrollPane);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Help text
        JLabel helpLabel = new JLabel("Enter investigation notes, dispute information, or any relevant details");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(127, 140, 141));
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(helpLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Important checkbox
        importantCheckBox = new JCheckBox("Mark as Important");
        importantCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));
        importantCheckBox.setBackground(Color.WHITE);
        importantCheckBox.setForeground(new Color(231, 76, 60));
        importantCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(importantCheckBox);

        // Examples section
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createExamplesSection());

        return panel;
    }

    private JPanel createParcelInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("📍 Parcel Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Info rows
        panel.add(createInfoRow("Parcel Number:", parcel.getParcelNumber()));
        panel.add(createInfoRow("Type:", parcel.getLandType().toString()));
        panel.add(createInfoRow("Area:", parcel.getArea() + " " + parcel.getAreaUnit()));
        panel.add(createInfoRow("Address:", parcel.getAddress()));
        panel.add(createInfoRow("Region:", parcel.getRegion()));
        panel.add(createInfoRow("Status:", parcel.getStatus().toString()));

        return panel;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setForeground(new Color(52, 73, 94));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComp.setForeground(new Color(44, 62, 80));

        row.add(labelComp);
        row.add(valueComp);

        return row;
    }

    private JPanel createExamplesSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(254, 249, 231));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 196, 15), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("💡 Example Notes:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(243, 156, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        String[] examples = {
                "• Family dispute - in mediation",
                "• Fake document suspected - under investigation",
                "• Boundary conflict with neighboring parcel SL-2019-0235",
                "• Documents verified - all authentic",
                "• Court case pending - hearing scheduled for next month"
        };

        for (String example : examples) {
            JLabel exampleLabel = new JLabel(example);
            exampleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            exampleLabel.setForeground(new Color(127, 140, 141));
            exampleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(exampleLabel);
        }

        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(44, 62, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        // Add Note button
        JButton addButton = new JButton("Add Note");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(140, 40));
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> handleAddNote());

        panel.add(cancelButton);
        panel.add(addButton);

        return panel;
    }

    private void handleAddNote() {
        String noteText = noteTextArea.getText().trim();

        // Validate
        if (noteText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a note before submitting.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            noteTextArea.requestFocus();
            return;
        }

        if (noteText.length() < 10) {
            JOptionPane.showMessageDialog(
                    this,
                    "Note must be at least 10 characters long.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            noteTextArea.requestFocus();
            return;
        }

        // Prepare note with timestamp and importance marker
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        String fullNote = "";

        // Add importance marker if checked
        if (importantCheckBox.isSelected()) {
            fullNote = "[IMPORTANT] ";
        }

        // Add timestamp
        fullNote += "[" + timestamp + "] ";

        // Add the note text
        fullNote += noteText;

        // Append to existing notes
        String existingNotes = parcel.getNotes();
        if (existingNotes != null && !existingNotes.trim().isEmpty()) {
            parcel.setNotes(existingNotes + "\n\n" + fullNote);
        } else {
            parcel.setNotes(fullNote);
        }

        // Update in database
        boolean success = parcelDAO.updateParcel(parcel);

        if (success) {
            String message = "Note added successfully to parcel " + parcel.getParcelNumber();
            if (importantCheckBox.isSelected()) {
                message += "\n\n⚠️ Note marked as IMPORTANT";
            }

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to add note. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}