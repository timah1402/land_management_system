package views.citizen.panels;

import dao.*;
import models.*;
import views.citizen.dialogs.DisputeDetailsDialog;
import views.citizen.dialogs.NewDisputeDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for displaying and managing citizen's disputes
 */
public class DisputesPanel extends JPanel {

    private Citizen currentCitizen;
    private DisputeDAO disputeDAO;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private JTable disputeTable;
    private DefaultTableModel disputeTableModel;

    public DisputesPanel(Citizen currentCitizen, DisputeDAO disputeDAO,
                         ParcelDAO parcelDAO, CitizenDAO citizenDAO) {
        this.currentCitizen = currentCitizen;
        this.disputeDAO = disputeDAO;
        this.parcelDAO = parcelDAO;
        this.citizenDAO = citizenDAO;

        initializeUI();
        loadDisputes();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        add(createTopPanel(), BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Bottom panel
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Disputes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBackground(Color.WHITE);

        JButton newDisputeButton = new JButton("File New Dispute");
        newDisputeButton.setBackground(new Color(231, 76, 60));
        newDisputeButton.setForeground(Color.WHITE);
        newDisputeButton.setOpaque(true);
        newDisputeButton.setBorderPainted(false);
        newDisputeButton.addActionListener(e -> showNewDisputeDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDisputes());

        buttonGroup.add(newDisputeButton);
        buttonGroup.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonGroup, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel", "Type", "Status", "Priority", "Opened", "Resolution"};

        disputeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        disputeTable = new JTable(disputeTableModel);
        disputeTable.setFont(new Font("Arial", Font.PLAIN, 12));
        disputeTable.setRowHeight(30);
        disputeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(disputeTable);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewDisputeDetails());

        bottomPanel.add(viewButton);

        return bottomPanel;
    }

    public void loadDisputes() {
        disputeTableModel.setRowCount(0);

        if (currentCitizen != null) {
            var disputes = disputeDAO.getDisputesByCitizen(currentCitizen.getCitizenId());

            for (var dispute : disputes) {
                Object[] row = {
                        dispute.getDisputeId(),
                        "Parcel #" + dispute.getParcelId(),
                        dispute.getType(),
                        dispute.getStatus(),
                        dispute.getPriority(),
                        dispute.getOpenedDate() != null ? dispute.getOpenedDate().toString() : "N/A",
                        dispute.getResolution() != null ? "Resolved" : "Pending"
                };
                disputeTableModel.addRow(row);
            }
        }
    }

    private void viewDisputeDetails() {
        int selectedRow = disputeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dispute", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);
        Dispute dispute = disputeDAO.getDisputeById(disputeId);

        if (dispute != null) {
            DisputeDetailsDialog dialog = new DisputeDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), dispute);
            dialog.setVisible(true);
        }
    }

    private void showNewDisputeDialog() {
        NewDisputeDialog dialog = new NewDisputeDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                currentCitizen,
                parcelDAO,
                citizenDAO,
                disputeDAO,
                this::loadDisputes
        );
        dialog.setVisible(true);
    }
}