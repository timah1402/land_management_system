package dao;

import database.DatabaseConfig;
import models.Dispute;
import models.Dispute.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Dispute operations
 */
public class DisputeDAO {

    /**
     * Create a new dispute
     */
    public boolean createDispute(Dispute dispute) {
        String sql = "INSERT INTO Litiges (parcelle_id, plaignant_id, defendeur_id, type_litige, " +
                "description, statut_litige, priorite, date_ouverture, preuves_fournies) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, dispute.getParcelId());
            pstmt.setInt(2, dispute.getComplainantId());

            if (dispute.getDefendantId() != null) {
                pstmt.setInt(3, dispute.getDefendantId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, dispute.getType().name());
            pstmt.setString(5, dispute.getDescription());
            pstmt.setString(6, dispute.getStatus().name());
            pstmt.setString(7, dispute.getPriority().name());
            pstmt.setDate(8, dispute.getOpenedDate());
            pstmt.setString(9, dispute.getEvidenceProvided());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    dispute.setDisputeId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get dispute by ID
     */
    public Dispute getDisputeById(int disputeId) {
        String sql = "SELECT * FROM Litiges WHERE litige_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, disputeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDisputeFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting dispute by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update dispute
     */
    public boolean updateDispute(Dispute dispute) {
        String sql = "UPDATE Litiges SET defendeur_id = ?, type_litige = ?, description = ?, " +
                "statut_litige = ?, priorite = ?, agent_assigne = ?, resolution = ?, " +
                "preuves_fournies = ? WHERE litige_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (dispute.getDefendantId() != null) {
                pstmt.setInt(1, dispute.getDefendantId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, dispute.getType().name());
            pstmt.setString(3, dispute.getDescription());
            pstmt.setString(4, dispute.getStatus().name());
            pstmt.setString(5, dispute.getPriority().name());

            if (dispute.getAssignedAgentId() != null) {
                pstmt.setInt(6, dispute.getAssignedAgentId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            pstmt.setString(7, dispute.getResolution());
            pstmt.setString(8, dispute.getEvidenceProvided());
            pstmt.setInt(9, dispute.getDisputeId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Assign agent to dispute
     */
    public boolean assignAgent(int disputeId, int agentId) {
        String sql = "UPDATE Litiges SET agent_assigne = ?, statut_litige = 'IN_PROGRESS' WHERE litige_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agentId);
            pstmt.setInt(2, disputeId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error assigning agent to dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Resolve dispute
     */
    public boolean resolveDispute(int disputeId, String resolution) {
        String sql = "UPDATE Litiges SET statut_litige = 'RESOLVED', resolution = ?, " +
                "date_resolution = CURRENT_DATE WHERE litige_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resolution);
            pstmt.setInt(2, disputeId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error resolving dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Close dispute
     */
    public boolean closeDispute(int disputeId) {
        String sql = "UPDATE Litiges SET statut_litige = 'CLOSED' WHERE litige_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, disputeId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error closing dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete dispute
     */
    public boolean deleteDispute(int disputeId) {
        String sql = "DELETE FROM Litiges WHERE litige_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, disputeId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all disputes
     */
    public List<Dispute> getAllDisputes() {
        List<Dispute> disputes = new ArrayList<>();
        String sql = "SELECT * FROM Litiges ORDER BY date_ouverture DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                disputes.add(extractDisputeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all disputes: " + e.getMessage());
            e.printStackTrace();
        }
        return disputes;
    }

    /**
     * Get disputes by parcel
     */
    public List<Dispute> getDisputesByParcel(int parcelId) {
        List<Dispute> disputes = new ArrayList<>();
        String sql = "SELECT * FROM Litiges WHERE parcelle_id = ? ORDER BY date_ouverture DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parcelId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disputes.add(extractDisputeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting disputes by parcel: " + e.getMessage());
            e.printStackTrace();
        }
        return disputes;
    }

    /**
     * Get disputes by citizen (as complainant or defendant)
     */
    public List<Dispute> getDisputesByCitizen(int citizenId) {
        List<Dispute> disputes = new ArrayList<>();
        String sql = "SELECT * FROM Litiges WHERE plaignant_id = ? OR defendeur_id = ? " +
                "ORDER BY date_ouverture DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, citizenId);
            pstmt.setInt(2, citizenId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disputes.add(extractDisputeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting disputes by citizen: " + e.getMessage());
            e.printStackTrace();
        }
        return disputes;
    }

    /**
     * Get disputes by status
     */
    public List<Dispute> getDisputesByStatus(DisputeStatus status) {
        List<Dispute> disputes = new ArrayList<>();
        String sql = "SELECT * FROM Litiges WHERE statut_litige = ? ORDER BY date_ouverture DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disputes.add(extractDisputeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting disputes by status: " + e.getMessage());
            e.printStackTrace();
        }
        return disputes;
    }

    /**
     * Get disputes assigned to agent
     */
    public List<Dispute> getDisputesByAgent(int agentId) {
        List<Dispute> disputes = new ArrayList<>();
        String sql = "SELECT * FROM Litiges WHERE agent_assigne = ? ORDER BY date_ouverture DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disputes.add(extractDisputeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting disputes by agent: " + e.getMessage());
            e.printStackTrace();
        }
        return disputes;
    }

    /**
     * Get disputes by priority
     */
    public List<Dispute> getDisputesByPriority(Priority priority) {
        List<Dispute> disputes = new ArrayList<>();
        String sql = "SELECT * FROM Litiges WHERE priorite = ? ORDER BY date_ouverture DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, priority.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                disputes.add(extractDisputeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting disputes by priority: " + e.getMessage());
            e.printStackTrace();
        }
        return disputes;
    }

    /**
     * Get dispute count
     */
    public int getDisputeCount() {
        String sql = "SELECT COUNT(*) FROM Litiges";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting dispute count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract Dispute object from ResultSet
     */
    private Dispute extractDisputeFromResultSet(ResultSet rs) throws SQLException {
        Dispute dispute = new Dispute();
        dispute.setDisputeId(rs.getInt("litige_id"));
        dispute.setParcelId(rs.getInt("parcelle_id"));
        dispute.setComplainantId(rs.getInt("plaignant_id"));

        int defendantId = rs.getInt("defendeur_id");
        if (!rs.wasNull()) {
            dispute.setDefendantId(defendantId);
        }

        dispute.setType(DisputeType.valueOf(rs.getString("type_litige")));
        dispute.setDescription(rs.getString("description"));
        dispute.setStatus(DisputeStatus.valueOf(rs.getString("statut_litige")));
        dispute.setPriority(Priority.valueOf(rs.getString("priorite")));

        int assignedAgentId = rs.getInt("agent_assigne");
        if (!rs.wasNull()) {
            dispute.setAssignedAgentId(assignedAgentId);
        }

        dispute.setOpenedDate(rs.getDate("date_ouverture"));
        dispute.setResolutionDate(rs.getDate("date_resolution"));
        dispute.setResolution(rs.getString("resolution"));
        dispute.setEvidenceProvided(rs.getString("preuves_fournies"));

        return dispute;
    }
}