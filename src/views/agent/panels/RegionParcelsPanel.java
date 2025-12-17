package views.agent.panels;

import dao.ParcelDAO;
import models.LandAgent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for viewing parcels in the agent's region
 */
public class RegionParcelsPanel extends JPanel {

    private LandAgent currentAgent;
    private ParcelDAO parcelDAO;
    private JTable parcelTable;
    private DefaultTableModel parcelTableModel;

    public RegionParcelsPanel(LandAgent currentAgent, ParcelDAO parcelDAO) {
        this.currentAgent = currentAgent;
        this.parcelDAO = parcelDAO;

        initializeUI();
        loadRegionParcels();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        add(createTopPanel(), BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Parcels in My Region: " +
                (currentAgent != null ? currentAgent.getRegion() : "N/A"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadRegionParcels());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Parcel #", "Land Title", "Area", "Type", "Status", "Owner"};

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

    public void loadRegionParcels() {
        parcelTableModel.setRowCount(0);

        if (currentAgent != null) {
            var parcels = parcelDAO.getParcelsByRegion(currentAgent.getRegion());

            for (var parcel : parcels) {
                Object[] row = {
                        parcel.getParcelId(),
                        parcel.getParcelNumber(),
                        parcel.getLandTitle() != null ? parcel.getLandTitle() : "N/A",
                        parcel.getArea() + " " + parcel.getAreaUnit(),
                        parcel.getLandType(),
                        parcel.getStatus(),
                        parcel.getCurrentOwnerId() > 0 ? "Citizen #" + parcel.getCurrentOwnerId() : "Unassigned"
                };
                parcelTableModel.addRow(row);
            }
        }
    }
}