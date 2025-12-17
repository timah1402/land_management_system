package views.citizen.panels;

import dao.ParcelDAO;
import models.Citizen;
import models.Parcel;
import views.citizen.dialogs.ParcelDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for displaying and managing citizen's parcels
 */
public class ParcelsPanel extends JPanel {

    private Citizen currentCitizen;
    private ParcelDAO parcelDAO;
    private JTable parcelTable;
    private DefaultTableModel parcelTableModel;

    public ParcelsPanel(Citizen currentCitizen, ParcelDAO parcelDAO) {
        this.currentCitizen = currentCitizen;
        this.parcelDAO = parcelDAO;

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

        JLabel titleLabel = new JLabel("My Parcels");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadParcels());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel #", "Land Title", "Area", "Type", "Status", "Region", "Value (XOF)"};

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

        bottomPanel.add(viewButton);

        return bottomPanel;
    }

    private void loadParcels() {
        parcelTableModel.setRowCount(0);

        if (currentCitizen != null) {
            var parcels = parcelDAO.getParcelsByOwner(currentCitizen.getCitizenId());

            for (var parcel : parcels) {
                Object[] row = {
                        parcel.getParcelId(),
                        parcel.getParcelNumber(),
                        parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                        parcel.getArea() + " " + parcel.getAreaUnit(),
                        parcel.getLandType(),
                        parcel.getStatus(),
                        parcel.getRegion(),
                        parcel.getEstimatedValue() != null ? String.format("%.2f", parcel.getEstimatedValue()) : "N/A"
                };
                parcelTableModel.addRow(row);
            }
        }
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
            ParcelDetailsDialog dialog = new ParcelDetailsDialog((Frame) SwingUtilities.getWindowAncestor(this), parcel);
            dialog.setVisible(true);
        }
    }
}