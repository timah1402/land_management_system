package dao;

import database.DatabaseConfig;
import models.Transaction;
import models.Transaction.*;

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
     * Approve transaction
     */
    public boolean approveTransaction(int transactionId, int agentId) {
        String sql = "UPDATE Transactions SET statut_transaction = 'APPROVED', " +
                "agent_validateur = ?, date_validation = CURRENT_TIMESTAMP " +
                "WHERE transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agentId);
            pstmt.setInt(2, transactionId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error approving transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
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