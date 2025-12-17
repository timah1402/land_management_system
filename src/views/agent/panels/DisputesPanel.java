package views.agent.panels;

import dao.DisputeDAO;
import models.Dispute;
import models.LandAgent;
import views.agent.dialogs.DisputeDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for managing assigned disputes
 */
public class DisputesPanel extends JPanel {

    private LandAgent currentAgent;
    private DisputeDAO disputeDAO;
    private JTable disputeTable;
    private DefaultTableModel disputeTableModel;

    public DisputesPanel(LandAgent currentAgent, DisputeDAO disputeDAO) {
        this.currentAgent = currentAgent;
        this.disputeDAO = disputeDAO;

        initializeUI();
        loadMyDisputes();
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

        JLabel titleLabel = new JLabel("My Assigned Disputes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyDisputes());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel", "Type", "Status", "Priority", "Complainant", "Opened"};

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

        JButton resolveButton = new JButton("Resolve Dispute");
        resolveButton.setBackground(new Color(39, 174, 96));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setOpaque(true);
        resolveButton.setBorderPainted(false);
        resolveButton.addActionListener(e -> resolveDispute());

        bottomPanel.add(viewButton);
        bottomPanel.add(resolveButton);

        return bottomPanel;
    }

    public void loadMyDisputes() {
        disputeTableModel.setRowCount(0);

        if (currentAgent != null && currentAgent.getAgentId() > 0) {
            var disputes = disputeDAO.getDisputesByAgent(currentAgent.getAgentId());

            for (var dispute : disputes) {
                Object[] row = {
                        dispute.getDisputeId(),
                        "Parcel #" + dispute.getParcelId(),
                        dispute.getType(),
                        dispute.getStatus(),
                        dispute.getPriority(),
                        "Citizen #" + dispute.getComplainantId(),
                        dispute.getOpenedDate() != null ? dispute.getOpenedDate().toString() : "N/A"
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
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    dispute
            );
            dialog.setVisible(true);
        }
    }

    private void resolveDispute() {
        int selectedRow = disputeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dispute", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resolution = JOptionPane.showInputDialog(this, "Enter resolution details:");
        if (resolution != null && !resolution.trim().isEmpty()) {
            int disputeId = (int) disputeTableModel.getValueAt(selectedRow, 0);

            if (disputeDAO.resolveDispute(disputeId, resolution.trim())) {
                JOptionPane.showMessageDialog(this, "Dispute resolved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMyDisputes();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to resolve dispute", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}