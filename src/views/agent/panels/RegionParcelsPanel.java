package views.agent.panels;

import dao.*;
import models.*;
import models.Parcel.*;
import views.agent.dialogs.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

/**
 * Panel showing all parcels in the agent's region with management options
 */
public class RegionParcelsPanel extends JPanel {

    private LandAgent currentAgent;
    private ParcelDAO parcelDAO;
    private CitizenDAO citizenDAO;

    private JTable parcelsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> typeFilterCombo;
    private JLabel countLabel;

    private List<Parcel> currentParcels;

    public RegionParcelsPanel(LandAgent currentAgent, ParcelDAO parcelDAO) {
        this.currentAgent = currentAgent;
        this.parcelDAO = parcelDAO;
        this.citizenDAO = new CitizenDAO();

        initializeUI();
        loadParcels();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and filters
        add(createTopPanel(), BorderLayout.NORTH);

        // Center panel with table
        add(createTablePanel(), BorderLayout.CENTER);

        // Bottom panel with actions
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Parcels in My Region: " + currentAgent.getRegion());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        countLabel = new JLabel("Total: 0 parcels");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setForeground(new Color(127, 140, 141));
        titlePanel.add(countLabel, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        // Filters panel
        panel.add(createFiltersPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Search field
        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.addActionListener(e -> filterParcels());
        panel.add(searchField);

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(statusLabel);

        statusFilterCombo = new JComboBox<>(new String[]{
                "All", "AVAILABLE", "OCCUPIED", "IN_TRANSACTION", "IN_DISPUTE", "RESERVED"
        });
        statusFilterCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        statusFilterCombo.addActionListener(e -> filterParcels());
        panel.add(statusFilterCombo);

        // Type filter
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(typeLabel);

        typeFilterCombo = new JComboBox<>(new String[]{
                "All", "RESIDENTIAL", "COMMERCIAL", "AGRICULTURAL", "INDUSTRIAL", "MIXED"
        });
        typeFilterCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        typeFilterCombo.addActionListener(e -> filterParcels());
        panel.add(typeFilterCombo);

        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setOpaque(true);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> filterParcels());
        panel.add(searchButton);

        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setOpaque(true);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> clearFilters());
        panel.add(clearButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Create table
        String[] columns = {
                "Parcel #", "Type", "Area", "Address", "Owner", "Status", "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is editable
            }
        };

        parcelsTable = new JTable(tableModel);
        parcelsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        parcelsTable.setRowHeight(50);
        parcelsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        parcelsTable.getTableHeader().setBackground(new Color(52, 152, 219));
        parcelsTable.getTableHeader().setForeground(Color.WHITE);
        parcelsTable.setSelectionBackground(new Color(174, 214, 241));
        parcelsTable.setGridColor(new Color(189, 195, 199));

        // Set column widths
        TableColumnModel columnModel = parcelsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120); // Parcel #
        columnModel.getColumn(1).setPreferredWidth(100); // Type
        columnModel.getColumn(2).setPreferredWidth(80);  // Area
        columnModel.getColumn(3).setPreferredWidth(200); // Address
        columnModel.getColumn(4).setPreferredWidth(150); // Owner
        columnModel.getColumn(5).setPreferredWidth(100); // Status
        columnModel.getColumn(6).setPreferredWidth(250); // Actions

        // Add button renderer and editor for Actions column
        parcelsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        parcelsTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(parcelsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> {
            loadParcels();
            JOptionPane.showMessageDialog(this, "Parcels refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(refreshButton);

        return panel;
    }

    private void loadParcels() {
        currentParcels = parcelDAO.getParcelsByRegion(currentAgent.getRegion());
        refreshTable(currentParcels);
    }

    private void filterParcels() {
        List<Parcel> filtered = parcelDAO.getParcelsByRegion(currentAgent.getRegion());

        // Filter by search term
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            filtered = filtered.stream()
                    .filter(p ->
                            p.getParcelNumber().toLowerCase().contains(searchTerm) ||
                                    p.getAddress().toLowerCase().contains(searchTerm) ||
                                    (p.getCurrentUsage() != null && p.getCurrentUsage().toLowerCase().contains(searchTerm))
                    )
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filter by status
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        if (!"All".equals(statusFilter)) {
            ParcelStatus status = ParcelStatus.valueOf(statusFilter);
            filtered = filtered.stream()
                    .filter(p -> p.getStatus() == status)
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filter by type
        String typeFilter = (String) typeFilterCombo.getSelectedItem();
        if (!"All".equals(typeFilter)) {
            LandType type = LandType.valueOf(typeFilter);
            filtered = filtered.stream()
                    .filter(p -> p.getLandType() == type)
                    .collect(java.util.stream.Collectors.toList());
        }

        currentParcels = filtered;
        refreshTable(filtered);
    }

    private void clearFilters() {
        searchField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        typeFilterCombo.setSelectedIndex(0);
        loadParcels();
    }

    private void refreshTable(List<Parcel> parcels) {
        tableModel.setRowCount(0);

        for (Parcel parcel : parcels) {
            String ownerName = getOwnerName(parcel.getCurrentOwnerId());
            String areaDisplay = String.format("%.2f %s", parcel.getArea(), parcel.getAreaUnit());

            tableModel.addRow(new Object[]{
                    parcel.getParcelNumber(),
                    parcel.getLandType(),
                    areaDisplay,
                    parcel.getAddress(),
                    ownerName,
                    parcel.getStatus(),
                    parcel // Store the parcel object for button actions
            });
        }

        countLabel.setText("Total: " + parcels.size() + " parcel(s)");
    }

    private String getOwnerName(int ownerId) {
        if (ownerId <= 0) {
            return "No Owner";
        }

        Citizen owner = citizenDAO.getCitizenById(ownerId);
        if (owner != null) {
            return owner.getFirstName() + " " + owner.getLastName();
        }
        return "Unknown";
    }

    public void refresh() {
        loadParcels();
    }

    // Button Renderer for Actions column
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            removeAll();

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            // View button
            JButton viewBtn = new JButton("👁️ View");
            viewBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            viewBtn.setBackground(new Color(52, 152, 219));
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setBorderPainted(false);
            viewBtn.setOpaque(true);
            viewBtn.setPreferredSize(new Dimension(70, 30));
            add(viewBtn);

            // Note button
            JButton noteBtn = new JButton("📝 Note");
            noteBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            noteBtn.setBackground(new Color(241, 196, 15));
            noteBtn.setForeground(Color.WHITE);
            noteBtn.setFocusPainted(false);
            noteBtn.setBorderPainted(false);
            noteBtn.setOpaque(true);
            noteBtn.setPreferredSize(new Dimension(70, 30));
            add(noteBtn);

            // Edit button
            JButton editBtn = new JButton("✏️ Edit");
            editBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            editBtn.setBackground(new Color(46, 204, 113));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setOpaque(true);
            editBtn.setPreferredSize(new Dimension(70, 30));
            add(editBtn);

            return this;
        }
    }

    // Button Editor for Actions column
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private Parcel currentParcel;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);

            // View button
            JButton viewBtn = new JButton("👁️ View");
            viewBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            viewBtn.setBackground(new Color(52, 152, 219));
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setBorderPainted(false);
            viewBtn.setOpaque(true);
            viewBtn.setPreferredSize(new Dimension(70, 30));
            viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            viewBtn.addActionListener(e -> viewParcelDetails());
            panel.add(viewBtn);

            // Note button
            JButton noteBtn = new JButton("📝 Note");
            noteBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            noteBtn.setBackground(new Color(241, 196, 15));
            noteBtn.setForeground(Color.WHITE);
            noteBtn.setFocusPainted(false);
            noteBtn.setBorderPainted(false);
            noteBtn.setOpaque(true);
            noteBtn.setPreferredSize(new Dimension(70, 30));
            noteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            noteBtn.addActionListener(e -> addParcelNote());
            panel.add(noteBtn);

            // Edit button
            JButton editBtn = new JButton("✏️ Edit");
            editBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            editBtn.setBackground(new Color(46, 204, 113));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setOpaque(true);
            editBtn.setPreferredSize(new Dimension(70, 30));
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.addActionListener(e -> editParcel());
            panel.add(editBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {

            currentParcel = (Parcel) value;

            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentParcel;
        }

        private void viewParcelDetails() {
            if (currentParcel == null) return;

            String ownerName = getOwnerName(currentParcel.getCurrentOwnerId());

            String details = String.format(
                    "📋 PARCEL DETAILS\n\n" +
                            "Parcel Number: %s\n" +
                            "Land Title: %s\n" +
                            "Type: %s\n" +
                            "Area: %.2f %s\n" +
                            "Current Usage: %s\n\n" +
                            "📍 LOCATION\n\n" +
                            "Address: %s\n" +
                            "Region: %s\n" +
                            "Department: %s\n" +
                            "Commune: %s\n" +
                            "GPS: %s\n\n" +
                            "👤 OWNERSHIP\n\n" +
                            "Owner: %s\n" +
                            "Acquisition Date: %s\n" +
                            "Status: %s\n" +
                            "Estimated Value: %s FCFA\n\n" +
                            "📝 NOTES\n\n%s",
                    currentParcel.getParcelNumber(),
                    currentParcel.getLandTitle() != null ? currentParcel.getLandTitle() : "N/A",
                    currentParcel.getLandType(),
                    currentParcel.getArea(),
                    currentParcel.getAreaUnit(),
                    currentParcel.getCurrentUsage() != null ? currentParcel.getCurrentUsage() : "N/A",
                    currentParcel.getAddress(),
                    currentParcel.getRegion(),
                    currentParcel.getDepartment() != null ? currentParcel.getDepartment() : "N/A",
                    currentParcel.getCommune() != null ? currentParcel.getCommune() : "N/A",
                    currentParcel.getGpsCoordinates() != null ? currentParcel.getGpsCoordinates() : "N/A",
                    ownerName,
                    currentParcel.getAcquisitionDate() != null ? currentParcel.getAcquisitionDate() : "N/A",
                    currentParcel.getStatus(),
                    currentParcel.getEstimatedValue() != null ? currentParcel.getEstimatedValue() : "N/A",
                    currentParcel.getNotes() != null && !currentParcel.getNotes().isEmpty() ?
                            currentParcel.getNotes() : "No notes available"
            );

            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 500));

            JOptionPane.showMessageDialog(
                    RegionParcelsPanel.this,
                    scrollPane,
                    "Parcel Details: " + currentParcel.getParcelNumber(),
                    JOptionPane.INFORMATION_MESSAGE
            );

            fireEditingStopped();
        }

        private void addParcelNote() {
            if (currentParcel == null) return;

            Window window = SwingUtilities.getWindowAncestor(RegionParcelsPanel.this);
            Frame frame = null;
            if (window instanceof Frame) {
                frame = (Frame) window;
            }

            AddParcelNoteDialog dialog = new AddParcelNoteDialog(
                    frame,
                    currentParcel,
                    () -> {
                        loadParcels();
                    }
            );
            dialog.setVisible(true);

            fireEditingStopped();
        }

        private void editParcel() {
            if (currentParcel == null) return;

            JOptionPane.showMessageDialog(
                    RegionParcelsPanel.this,
                    "Edit Parcel feature will be added in the next phase!",
                    "Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE
            );

            fireEditingStopped();
        }
    }
}