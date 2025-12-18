package views.citizen.dialogs;

import dao.*;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for creating a new transaction
 */
public class NewTransactionDialog extends JDialog {

    private Citizen currentCitizen;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private TransactionDAO transactionDAO;
    private Runnable onSuccessCallback;

    private JComboBox<String> parcelCombo;
    private JComboBox<String> typeCombo;
    private JComboBox<String> previousOwnerCombo;
    private JTextField amountField;
    private JTextArea notesArea;

    private List<Parcel> allParcels;
    private List<Citizen> allCitizens;

    public NewTransactionDialog(Frame owner, Citizen currentCitizen, ParcelDAO parcelDAO,
                                CitizenDAO citizenDAO, TransactionDAO transactionDAO,
                                Runnable onSuccessCallback) {
        super(owner, "New Transaction Request", true);
        this.currentCitizen = currentCitizen;
        this.parcelDAO = parcelDAO;
        this.citizenDAO = citizenDAO;
        this.transactionDAO = transactionDAO;
        this.onSuccessCallback = onSuccessCallback;

        loadData();
        initializeUI();
    }

    private void loadData() {
        // Citizens should only see their own parcels
        if (currentCitizen != null) {
            allParcels = parcelDAO.getParcelsByOwner(currentCitizen.getCitizenId());
        } else {
            allParcels = new ArrayList<>();
        }
        allCitizens = citizenDAO.getAllCitizens();
    }

    private void initializeUI() {
        setSize(500, 550);
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
        panel.add(new JLabel("Select Your Parcel:"), gbc);

        String[] parcelOptions = createParcelOptions();
        parcelCombo = new JComboBox<>(parcelOptions);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(parcelCombo, gbc);

        // Transaction Type
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Type:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"PURCHASE", "SALE", "TRANSFER", "INHERITANCE", "DONATION", "EXCHANGE"});
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(typeCombo, gbc);

        // Previous Owner Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("From Citizen (optional):"), gbc);

        String[] citizenOptions = createCitizenOptions();
        previousOwnerCombo = new JComboBox<>(citizenOptions);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(previousOwnerCombo, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Amount (XOF):"), gbc);
        amountField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(amountField, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Notes:"), gbc);
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(notesScroll, gbc);

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
        if (allParcels.isEmpty()) {
            return new String[]{"-- You don't own any parcels yet --"};
        }

        String[] options = new String[allParcels.size() + 1];
        options[0] = "-- Select Your Parcel --";

        for (int i = 0; i < allParcels.size(); i++) {
            Parcel parcel = allParcels.get(i);
            options[i + 1] = String.format("ID: %d - %s (%s, %s)",
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getLandType(),
                    parcel.getRegion()
            );
        }

        return options;
    }

    private String[] createCitizenOptions() {
        String[] options = new String[allCitizens.size() + 1];
        options[0] = "-- Select Previous Owner (Optional) --";

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
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(e -> submitTransaction());
        return submitButton;
    }

    private void submitTransaction() {
        try {
            // Check if user has any parcels
            if (allParcels.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You don't own any parcels yet. You cannot create a transaction.",
                        "No Parcels",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (parcelCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a parcel", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Transaction transaction = new Transaction();

            // Get selected parcel
            int selectedParcelIndex = parcelCombo.getSelectedIndex();
            Parcel selectedParcel = allParcels.get(selectedParcelIndex - 1);
            transaction.setParcelId(selectedParcel.getParcelId());

            transaction.setType(Transaction.TransactionType.valueOf((String) typeCombo.getSelectedItem()));

            // Get selected previous owner
            int selectedOwnerIndex = previousOwnerCombo.getSelectedIndex();
            if (selectedOwnerIndex > 0) {
                Citizen selectedCitizen = allCitizens.get(selectedOwnerIndex - 1);
                transaction.setPreviousOwnerId(selectedCitizen.getCitizenId());
            }

            transaction.setNewOwnerId(currentCitizen.getCitizenId());
            transaction.setAmount(new BigDecimal(amountField.getText().trim()));
            transaction.setCurrency("XOF");
            transaction.setTransactionDate(Date.valueOf(LocalDate.now()));
            transaction.setStatus(Transaction.TransactionStatus.PENDING);
            transaction.setNotes(notesArea.getText().trim());

            if (transactionDAO.createTransaction(transaction)) {
                JOptionPane.showMessageDialog(this,
                        "Transaction submitted successfully!\nPending agent approval.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit transaction", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}