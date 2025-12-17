package dao;

import database.DatabaseConfig;
import models.LandAgent;
import models.LandAgent.AgentStatus;
import models.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LandAgent operations
 */
public class LandAgentDAO {

    private UserDAO userDAO = new UserDAO();

    /**
     * Create a new land agent
     */
    public boolean createLandAgent(LandAgent agent) {
        // First, create the user
        agent.setRole(UserRole.AGENT);
        boolean userCreated = userDAO.createUser(agent);

        if (!userCreated) {
            System.err.println("Failed to create user for land agent");
            return false;
        }

        // Then create the agent entry with a fresh connection
        String sql = "INSERT INTO AgentsFonciers (user_id, matricule, region, specialisation, " +
                "date_nomination, statut) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, agent.getUserId());
            pstmt.setString(2, agent.getRegistrationNumber());
            pstmt.setString(3, agent.getRegion());
            pstmt.setString(4, agent.getSpecialization());
            pstmt.setDate(5, agent.getAppointmentDate());
            pstmt.setString(6, agent.getStatus().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    agent.setAgentId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating land agent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get land agent by agent ID
     */
    public LandAgent getAgentById(int agentId) {
        String sql = "SELECT a.*, u.* FROM AgentsFonciers a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.agent_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAgentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting agent by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get land agent by user ID
     */
    public LandAgent getAgentByUserId(int userId) {
        String sql = "SELECT a.*, u.* FROM AgentsFonciers a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAgentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting agent by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get land agent by registration number
     */
    public LandAgent getAgentByRegistrationNumber(String registrationNumber) {
        String sql = "SELECT a.*, u.* FROM AgentsFonciers a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.matricule = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, registrationNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAgentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting agent by registration number: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update land agent
     */
    public boolean updateAgent(LandAgent agent) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Update user information
            if (!userDAO.updateUser(agent)) {
                conn.rollback();
                return false;
            }

            // Update agent-specific information
            String sql = "UPDATE AgentsFonciers SET matricule = ?, region = ?, " +
                    "specialisation = ?, date_nomination = ?, statut = ? " +
                    "WHERE agent_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, agent.getRegistrationNumber());
            pstmt.setString(2, agent.getRegion());
            pstmt.setString(3, agent.getSpecialization());
            pstmt.setDate(4, agent.getAppointmentDate());
            pstmt.setString(5, agent.getStatus().name());
            pstmt.setInt(6, agent.getAgentId());

            boolean result = pstmt.executeUpdate() > 0;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            pstmt.close();
            return result;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error updating agent: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all land agents
     */
    public List<LandAgent> getAllAgents() {
        List<LandAgent> agents = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM AgentsFonciers a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "ORDER BY u.date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agents.add(extractAgentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all agents: " + e.getMessage());
            e.printStackTrace();
        }
        return agents;
    }

    /**
     * Get agents by region
     */
    public List<LandAgent> getAgentsByRegion(String region) {
        List<LandAgent> agents = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM AgentsFonciers a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.region = ? " +
                "ORDER BY u.nom, u.prenom";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, region);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                agents.add(extractAgentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting agents by region: " + e.getMessage());
            e.printStackTrace();
        }
        return agents;
    }

    /**
     * Get agents by status
     */
    public List<LandAgent> getAgentsByStatus(AgentStatus status) {
        List<LandAgent> agents = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM AgentsFonciers a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.statut = ? " +
                "ORDER BY u.nom, u.prenom";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                agents.add(extractAgentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting agents by status: " + e.getMessage());
            e.printStackTrace();
        }
        return agents;
    }

    /**
     * Update agent status
     */
    public boolean updateAgentStatus(int agentId, AgentStatus status) {
        String sql = "UPDATE AgentsFonciers SET statut = ? WHERE agent_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, agentId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating agent status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete land agent
     */
    public boolean deleteAgent(int agentId) {
        LandAgent agent = getAgentById(agentId);
        if (agent != null) {
            return userDAO.deleteUser(agent.getUserId());
        }
        return false;
    }

    /**
     * Check if registration number exists
     */
    public boolean registrationNumberExists(String registrationNumber) {
        String sql = "SELECT COUNT(*) FROM AgentsFonciers WHERE matricule = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, registrationNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking registration number: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extract LandAgent object from ResultSet
     */
    private LandAgent extractAgentFromResultSet(ResultSet rs) throws SQLException {
        LandAgent agent = new LandAgent();

        // User fields
        agent.setUserId(rs.getInt("user_id"));
        agent.setLastName(rs.getString("nom"));
        agent.setFirstName(rs.getString("prenom"));
        agent.setEmail(rs.getString("email"));
        agent.setPhone(rs.getString("telephone"));
        agent.setPassword(rs.getString("mot_de_passe"));
        agent.setRole(UserRole.valueOf(rs.getString("role")));
        agent.setAccountStatus(models.User.AccountStatus.valueOf(rs.getString("account_status")));
        agent.setCreatedAt(rs.getTimestamp("date_creation"));
        agent.setLastLogin(rs.getTimestamp("derniere_connexion"));

        // Agent-specific fields
        agent.setAgentId(rs.getInt("agent_id"));
        agent.setRegistrationNumber(rs.getString("matricule"));
        agent.setRegion(rs.getString("region"));
        agent.setSpecialization(rs.getString("specialisation"));
        agent.setAppointmentDate(rs.getDate("date_nomination"));
        agent.setStatus(AgentStatus.valueOf(rs.getString("statut")));

        return agent;
    }
}