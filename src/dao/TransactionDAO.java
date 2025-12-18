package dao;

import database.DatabaseConfig;
import models.Transaction;
import models.Transaction.*;
import models.Parcel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction operations
 */
public class TransactionDAO {

    /**
     * Create a new transaction
     */
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO Transactions (parcelle_id, type_transaction, ancien_proprietaire, " +
                "nouveau_proprietaire, montant, devise, date_transaction, statut_transaction, " +
                "frais_transaction, taxe_applicable, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, transaction.getParcelId());
            pstmt.setString(2, transaction.getType().name());

            if (transaction.getPreviousOwnerId() != null) {
                pstmt.setInt(3, transaction.getPreviousOwnerId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, transaction.getNewOwnerId());

            if (transaction.getAmount() != null) {
                pstmt.setBigDecimal(5, transaction.getAmount());
            } else {
                pstmt.setNull(5, Types.DECIMAL);
            }

            pstmt.setString(6, transaction.getCurrency());
            pstmt.setDate(7, transaction.getTransactionDate());
            pstmt.setString(8, transaction.getStatus().name());

            if (transaction.getTransactionFees() != null) {
                pstmt.setBigDecimal(9, transaction.getTransactionFees());
            } else {
                pstmt.setNull(9, Types.DECIMAL);
            }

            if (transaction.getApplicableTax() != null) {
                pstmt.setBigDecimal(10, transaction.getApplicableTax());
            } else {
                pstmt.setNull(10, Types.DECIMAL);
            }

            pstmt.setString(11, transaction.getNotes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    transaction.setTransactionId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get transaction by ID
     */
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM Transactions WHERE transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update transaction
     */
    public boolean updateTransaction(Transaction transaction) {
        String sql = "UPDATE Transactions SET type_transaction = ?, ancien_proprietaire = ?, " +
                "nouveau_proprietaire = ?, montant = ?, devise = ?, date_transaction = ?, " +
                "statut_transaction = ?, frais_transaction = ?, taxe_applicable = ?, notes = ? " +
                "WHERE transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getType().name());

            if (transaction.getPreviousOwnerId() != null) {
                pstmt.setInt(2, transaction.getPreviousOwnerId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setInt(3, transaction.getNewOwnerId());

            if (transaction.getAmount() != null) {
                pstmt.setBigDecimal(4, transaction.getAmount());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }

            pstmt.setString(5, transaction.getCurrency());
            pstmt.setDate(6, transaction.getTransactionDate());
            pstmt.setString(7, transaction.getStatus().name());

            if (transaction.getTransactionFees() != null) {
                pstmt.setBigDecimal(8, transaction.getTransactionFees());
            } else {
                pstmt.setNull(8, Types.DECIMAL);
            }

            if (transaction.getApplicableTax() != null) {
                pstmt.setBigDecimal(9, transaction.getApplicableTax());
            } else {
                pstmt.setNull(9, Types.DECIMAL);
            }

            pstmt.setString(10, transaction.getNotes());
            pstmt.setInt(11, transaction.getTransactionId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Approve transaction - Updates transaction status AND transfers parcel ownership
     */
    public boolean approveTransaction(int transactionId, int agentId) {
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // STEP 1: Get transaction details
            Transaction transaction = getTransactionByIdInConnection(conn, transactionId);
            if (transaction == null) {
                System.err.println("Transaction not found: " + transactionId);
                conn.rollback();
                return false;
            }

            System.out.println("\n=== APPROVING TRANSACTION ===");
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Type: " + transaction.getType());
            System.out.println("Parcel ID: " + transaction.getParcelId());
            System.out.println("Previous Owner: " + transaction.getPreviousOwnerId());
            System.out.println("New Owner: " + transaction.getNewOwnerId());

            // STEP 2: Check if this is INHERITANCE WITH DIVISION
            boolean isInheritanceDivision = transaction.getType() == TransactionType.INHERITANCE
                    && transaction.getNotes() != null
                    && transaction.getNotes().contains("INHERITANCE WITH DIVISION");

            if (isInheritanceDivision) {
                System.out.println("Type: INHERITANCE WITH DIVISION");

                // DEBUG: Print the raw notes
                System.out.println("\n=== DEBUG HEIR PARSING ===");
                System.out.println("Raw transaction notes:");
                System.out.println("'" + transaction.getNotes() + "'");
                System.out.println("Notes length: " + (transaction.getNotes() != null ? transaction.getNotes().length() : 0));
                System.out.println("========================\n");

                // Parse heir IDs from notes
                List<Integer> heirIds = parseHeirIdsFromNotes(transaction.getNotes());
                System.out.println("Heirs found: " + heirIds.size());
                System.out.println("Heir IDs: " + heirIds);

                if (heirIds.isEmpty()) {
                    System.err.println("No heirs found in transaction notes!");
                    conn.rollback();
                    return false;
                }

                // Get original parcel info
                Parcel originalParcel = getParcelByIdInConnection(conn, transaction.getParcelId());
                if (originalParcel == null) {
                    System.err.println("Original parcel not found!");
                    conn.rollback();
                    return false;
                }

                double areaPerHeir = originalParcel.getArea() / heirIds.size();
                System.out.println("Original parcel area: " + originalParcel.getArea());
                System.out.println("Area per heir: " + areaPerHeir);

                // Create new parcel for each heir
                for (int i = 0; i < heirIds.size(); i++) {
                    int heirId = heirIds.get(i);

                    // Generate new parcel number (e.g., DK-2025-0001-A, DK-2025-0001-B)
                    String newParcelNumber = originalParcel.getParcelNumber() + "-" + (char)('A' + i);

                    System.out.println("Creating parcel " + newParcelNumber + " for heir " + heirId);

                    // Create new parcel for this heir
                    if (!createHeirParcel(conn, originalParcel, newParcelNumber, heirId, areaPerHeir, transactionId)) {
                        System.err.println("Failed to create parcel for heir " + heirId);
                        conn.rollback();
                        return false;
                    }
                }

                // Mark original parcel as SUBDIVIDED
                // Use RESERVED since SUBDIVIDED is not in the CHECK constraint
                // Don't change status, just add a note that it was subdivided
                String updateNoteSql = "UPDATE Parcelles SET notes = notes || '\n[SUBDIVIDED on ' || CURRENT_DATE || ' into " + heirIds.size() + " parcels]' " +
                        "WHERE parcelle_id = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(updateNoteSql)) {
                    pstmt.setInt(1, transaction.getParcelId());
                    pstmt.executeUpdate();
                }

                System.out.println("✓ Marked original parcel as subdivided in notes");

                System.out.println("✓ Created " + heirIds.size() + " new parcels");

            } else {
                // REGULAR TRANSFER - Just update parcel owner
                System.out.println("Type: REGULAR TRANSFER");

                String updateParcelSql = "UPDATE Parcelles SET proprietaire_actuel = ?, " +
                        "date_acquisition = CURRENT_DATE, statut_parcelle = 'OCCUPIED' " +
                        "WHERE parcelle_id = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(updateParcelSql)) {
                    pstmt.setInt(1, transaction.getNewOwnerId());
                    pstmt.setInt(2, transaction.getParcelId());

                    int rows = pstmt.executeUpdate();
                    System.out.println("Updated parcel ownership: " + rows + " row(s)");

                    if (rows == 0) {
                        System.err.println("Failed to update parcel ownership!");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // STEP 3: Update transaction status to APPROVED
            String updateTransactionSql = "UPDATE Transactions SET statut_transaction = 'APPROVED', " +
                    "agent_validateur = ?, date_validation = CURRENT_TIMESTAMP " +
                    "WHERE transaction_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(updateTransactionSql)) {
                pstmt.setInt(1, agentId);
                pstmt.setInt(2, transactionId);
                pstmt.executeUpdate();
            }

            // COMMIT ALL CHANGES
            conn.commit();
            System.out.println("✓ Transaction approved successfully!\n");
            return true;

        } catch (SQLException e) {
            System.err.println("✗ Error approving transaction:");
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reject transaction
     */
    public boolean rejectTransaction(int transactionId, int agentId) {
        String sql = "UPDATE Transactions SET statut_transaction = 'REJECTED', " +
                "agent_validateur = ?, date_validation = CURRENT_TIMESTAMP " +
                "WHERE transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agentId);
            pstmt.setInt(2, transactionId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error rejecting transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Get transactions by parcel
     */
    public List<Transaction> getTransactionsByParcel(int parcelId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE parcelle_id = ? ORDER BY date_transaction DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parcelId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting transactions by parcel: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Get transactions by citizen (as buyer or seller)
     */
    public List<Transaction> getTransactionsByCitizen(int citizenId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE ancien_proprietaire = ? OR nouveau_proprietaire = ? " +
                "ORDER BY date_transaction DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, citizenId);
            pstmt.setInt(2, citizenId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting transactions by citizen: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Get transactions by status
     */
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE statut_transaction = ? ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting transactions by status: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Delete transaction
     */
    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM Transactions WHERE transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get transaction by ID within an existing connection (for transaction management)
     */
    private Transaction getTransactionByIdInConnection(Connection conn, int transactionId) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE transaction_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Get parcel by ID within an existing connection
     */
    private Parcel getParcelByIdInConnection(Connection conn, int parcelId) throws SQLException {
        String sql = "SELECT * FROM Parcelles WHERE parcelle_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parcelId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Parcel parcel = new Parcel();
                parcel.setParcelId(rs.getInt("parcelle_id"));
                parcel.setParcelNumber(rs.getString("numero_parcelle"));
                parcel.setLandTitle(rs.getString("titre_foncier"));
                parcel.setArea(rs.getDouble("superficie"));
                parcel.setAreaUnit(Parcel.AreaUnit.valueOf(rs.getString("unite_superficie")));
                parcel.setLandType(Parcel.LandType.valueOf(rs.getString("type_terrain")));
                parcel.setCurrentUsage(rs.getString("usage_actuel"));
                parcel.setAddress(rs.getString("adresse"));
                parcel.setRegion(rs.getString("region"));
                parcel.setDepartment(rs.getString("departement"));
                parcel.setCommune(rs.getString("commune"));
                parcel.setGpsCoordinates(rs.getString("coordonnees_gps"));
                parcel.setStatus(Parcel.ParcelStatus.valueOf(rs.getString("statut_parcelle")));
                parcel.setCurrentOwnerId(rs.getInt("proprietaire_actuel"));
                return parcel;
            }
        }
        return null;
    }

    /**
     * Parse heir IDs from transaction notes
     * Example notes: "INHERITANCE WITH DIVISION - 4 heirs:\nHeir 1: John Doe (ID: 5)\nHeir 2: Jane Doe (ID: 6)"
     */
    private List<Integer> parseHeirIdsFromNotes(String notes) {
        List<Integer> heirIds = new ArrayList<>();

        if (notes == null || notes.isEmpty()) {
            return heirIds;
        }

        // Parse heir IDs from format: "Heir X: Name (ID: 123)"
        String[] lines = notes.split("\n");
        for (String line : lines) {
            if (line.contains("(ID:")) {
                try {
                    int startIdx = line.indexOf("(ID:") + 4;
                    int endIdx = line.indexOf(")", startIdx);
                    if (startIdx > 3 && endIdx > startIdx) {
                        String idStr = line.substring(startIdx, endIdx).trim();
                        int id = Integer.parseInt(idStr);
                        heirIds.add(id);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing heir ID from line: " + line);
                }
            }
        }

        return heirIds;
    }

    /**
     * Create a new parcel for an heir (subdivision)
     */
    private boolean createHeirParcel(Connection conn, Parcel original, String newParcelNumber,
                                     int ownerId, double area, int transactionId) throws SQLException {

        // Remove titre_foncier completely to avoid UNIQUE constraint
        String sql = "INSERT INTO Parcelles (numero_parcelle, superficie, unite_superficie, " +
                "type_terrain, usage_actuel, adresse, region, departement, commune, coordonnees_gps, " +
                "statut_parcelle, proprietaire_actuel, date_acquisition, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newParcelNumber);
            pstmt.setDouble(2, area);
            pstmt.setString(3, original.getAreaUnit().name());
            pstmt.setString(4, original.getLandType().name());
            pstmt.setString(5, original.getCurrentUsage());
            pstmt.setString(6, original.getAddress());
            pstmt.setString(7, original.getRegion());
            pstmt.setString(8, original.getDepartment());
            pstmt.setString(9, original.getCommune());
            pstmt.setString(10, original.getGpsCoordinates());
            pstmt.setString(11, "OCCUPIED");
            pstmt.setInt(12, ownerId);
            pstmt.setString(13, "Created from subdivision of " + original.getParcelNumber() +
                    " (Transaction ID: " + transactionId + ")");

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Update parcel status within an existing connection
     */
    private boolean updateParcelStatusInConnection(Connection conn, int parcelId, String status) throws SQLException {
        String sql = "UPDATE Parcelles SET statut_parcelle = ?, derniere_modification = CURRENT_TIMESTAMP " +
                "WHERE parcelle_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, parcelId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Extract Transaction object from ResultSet
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setParcelId(rs.getInt("parcelle_id"));
        transaction.setType(TransactionType.valueOf(rs.getString("type_transaction")));

        int previousOwnerId = rs.getInt("ancien_proprietaire");
        if (!rs.wasNull()) {
            transaction.setPreviousOwnerId(previousOwnerId);
        }

        transaction.setNewOwnerId(rs.getInt("nouveau_proprietaire"));
        transaction.setAmount(rs.getBigDecimal("montant"));
        transaction.setCurrency(rs.getString("devise"));
        transaction.setTransactionDate(rs.getDate("date_transaction"));
        transaction.setStatus(TransactionStatus.valueOf(rs.getString("statut_transaction")));

        int validatingAgentId = rs.getInt("agent_validateur");
        if (!rs.wasNull()) {
            transaction.setValidatingAgentId(validatingAgentId);
        }

        transaction.setValidationDate(rs.getTimestamp("date_validation"));
        transaction.setTransactionFees(rs.getBigDecimal("frais_transaction"));
        transaction.setApplicableTax(rs.getBigDecimal("taxe_applicable"));
        transaction.setDeedNumber(rs.getString("numero_acte"));
        transaction.setNotes(rs.getString("notes"));
        transaction.setCreatedAt(rs.getTimestamp("date_creation"));

        return transaction;
    }
}