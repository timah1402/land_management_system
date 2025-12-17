package views.admin.panels;

import dao.CitizenDAO;
import dao.ParcelDAO;
import models.Parcel;
import views.admin.dialogs.AddParcelDialog;
import views.admin.dialogs.ParcelDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for managing parcels
 */
public class ParcelManagementPanel extends JPanel {

    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;
    private JTable parcelTable;
    private DefaultTableModel parcelTableModel;

    public ParcelManagementPanel(ParcelDAO parcelDAO, CitizenDAO citizenDAO) {
        this.parcelDAO = parcelDAO;
        this.citizenDAO = citizenDAO;

        initializeUI();
        loadParcels();
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

        JLabel titleLabel = new JLabel("Parcel Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBackground(Color.WHITE);

        JButton addParcelButton = new JButton("Add New Parcel");
        addParcelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addParcelButton.setBackground(new Color(46, 204, 113));
        addParcelButton.setForeground(Color.WHITE);
        addParcelButton.setOpaque(true);
        addParcelButton.setBorderPainted(false);
        addParcelButton.addActionListener(e -> showAddParcelDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadParcels());

        buttonGroup.add(addParcelButton);
        buttonGroup.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonGroup, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel #", "Land Title", "Area (ha)", "Type", "Status", "Owner", "Region"};

        parcelTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        parcelTable = new JTable(parcelTableModel);
        parcelTable.setFont(new Font("Arial", Font.PLAIN, 12));
        parcelTable.setRowHeight(30);
        parcelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(parcelTable);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewParcelDetails());

        JButton editButton = new JButton("Edit Parcel");
        editButton.addActionListener(e -> editParcel());

        JButton deleteButton = new JButton("Delete Parcel");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteParcel());

        bottomPanel.add(viewButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);

        return bottomPanel;
    }

    public void loadParcels() {
        parcelTableModel.setRowCount(0);
        var parcels = parcelDAO.getAllParcels();

        for (var parcel : parcels) {
            Object[] row = {
                    parcel.getParcelId(),
                    parcel.getParcelNumber(),
                    parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                    parcel.getArea(),
                    parcel.getLandType(),
                    parcel.getStatus(),
                    parcel.getCurrentOwnerId() > 0 ? "Citizen #" + parcel.getCurrentOwnerId() : "Unassigned",
                    parcel.getRegion()
            };
            parcelTableModel.addRow(row);
        }
    }

    private void showAddParcelDialog() {
        AddParcelDialog dialog = new AddParcelDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                parcelDAO,
                citizenDAO,
                this::loadParcels
        );
        dialog.setVisible(true);
    }

    private void viewParcelDetails() {
        int selectedRow = parcelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a parcel", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int parcelId = (int) parcelTableModel.getValueAt(selectedRow, 0);
        Parcel parcel = parcelDAO.getParcelById(parcelId);

        if (parcel != null) {
            ParcelDetailsDialog dialog = new ParcelDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    parcel
            );
            dialog.setVisible(true);
        }
    }

    private void editParcel() {
        int selectedRow = parcelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a parcel to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Edit functionality coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteParcel() {
        int selectedRow = parcelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a parcel to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int parcelId = (int) parcelTableModel.getValueAt(selectedRow, 0);
        String parcelNumber = (String) parcelTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete parcel: " + parcelNumber + "?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (parcelDAO.deleteParcel(parcelId)) {
                JOptionPane.showMessageDialog(this, "Parcel deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadParcels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete parcel", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}