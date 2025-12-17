package dao;

import database.DatabaseConfig;
import models.Citizen;
import models.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Citizen operations
 */
public class CitizenDAO {

    private UserDAO userDAO = new UserDAO();

    /**
     * Create a new citizen (creates User first, then Citizen entry)
     */
    public boolean createCitizen(Citizen citizen) {
        // First, create the user
        citizen.setRole(UserRole.CITIZEN);
        boolean userCreated = userDAO.createUser(citizen);

        if (!userCreated) {
            System.err.println("Failed to create user for citizen");
            return false;
        }

        // Then create the citizen entry with a fresh connection
        String sql = "INSERT INTO Citoyens (user_id, numero_cni, date_naissance, lieu_naissance, " +
                "adresse_complete, profession) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, citizen.getUserId());
            pstmt.setString(2, citizen.getIdCardNumber());
            pstmt.setDate(3, citizen.getDateOfBirth());
            pstmt.setString(4, citizen.getPlaceOfBirth());
            pstmt.setString(5, citizen.getFullAddress());
            pstmt.setString(6, citizen.getOccupation());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    citizen.setCitizenId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating citizen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get citizen by citizen ID
     */
    public Citizen getCitizenById(int citizenId) {
        String sql = "SELECT c.*, u.* FROM Citoyens c " +
                "JOIN Users u ON c.user_id = u.user_id " +
                "WHERE c.citoyen_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, citizenId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCitizenFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting citizen by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get citizen by user ID
     */
    public Citizen getCitizenByUserId(int userId) {
        String sql = "SELECT c.*, u.* FROM Citoyens c " +
                "JOIN Users u ON c.user_id = u.user_id " +
                "WHERE c.user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCitizenFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting citizen by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get citizen by ID card number
     */
    public Citizen getCitizenByIdCard(String idCardNumber) {
        String sql = "SELECT c.*, u.* FROM Citoyens c " +
                "JOIN Users u ON c.user_id = u.user_id " +
                "WHERE c.numero_cni = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idCardNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCitizenFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting citizen by ID card: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update citizen information
     */
    public boolean updateCitizen(Citizen citizen) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Update user information
            if (!userDAO.updateUser(citizen)) {
                conn.rollback();
                return false;
            }

            // Update citizen-specific information
            String sql = "UPDATE Citoyens SET numero_cni = ?, date_naissance = ?, " +
                    "lieu_naissance = ?, adresse_complete = ?, profession = ? " +
                    "WHERE citoyen_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, citizen.getIdCardNumber());
            pstmt.setDate(2, citizen.getDateOfBirth());
            pstmt.setString(3, citizen.getPlaceOfBirth());
            pstmt.setString(4, citizen.getFullAddress());
            pstmt.setString(5, citizen.getOccupation());
            pstmt.setInt(6, citizen.getCitizenId());

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
            System.err.println("Error updating citizen: " + e.getMessage());
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
     * Get all citizens
     */
    public List<Citizen> getAllCitizens() {
        List<Citizen> citizens = new ArrayList<>();
        String sql = "SELECT c.*, u.* FROM Citoyens c " +
                "JOIN Users u ON c.user_id = u.user_id " +
                "ORDER BY u.date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                citizens.add(extractCitizenFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all citizens: " + e.getMessage());
            e.printStackTrace();
        }
        return citizens;
    }

    /**
     * Search citizens by name
     */
    public List<Citizen> searchCitizensByName(String searchTerm) {
        List<Citizen> citizens = new ArrayList<>();
        String sql = "SELECT c.*, u.* FROM Citoyens c " +
                "JOIN Users u ON c.user_id = u.user_id " +
                "WHERE u.nom LIKE ? OR u.prenom LIKE ? " +
                "ORDER BY u.nom, u.prenom";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                citizens.add(extractCitizenFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching citizens: " + e.getMessage());
            e.printStackTrace();
        }
        return citizens;
    }

    /**
     * Delete citizen
     */
    public boolean deleteCitizen(int citizenId) {
        // Due to CASCADE, deleting the user will also delete the citizen entry
        Citizen citizen = getCitizenById(citizenId);
        if (citizen != null) {
            return userDAO.deleteUser(citizen.getUserId());
        }
        return false;
    }

    /**
     * Check if ID card number exists
     */
    public boolean idCardExists(String idCardNumber) {
        String sql = "SELECT COUNT(*) FROM Citoyens WHERE numero_cni = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idCardNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking ID card: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extract Citizen object from ResultSet
     */
    private Citizen extractCitizenFromResultSet(ResultSet rs) throws SQLException {
        Citizen citizen = new Citizen();

        // User fields
        citizen.setUserId(rs.getInt("user_id"));
        citizen.setLastName(rs.getString("nom"));
        citizen.setFirstName(rs.getString("prenom"));
        citizen.setEmail(rs.getString("email"));
        citizen.setPhone(rs.getString("telephone"));
        citizen.setPassword(rs.getString("mot_de_passe"));
        citizen.setRole(UserRole.valueOf(rs.getString("role")));
        citizen.setAccountStatus(models.User.AccountStatus.valueOf(rs.getString("account_status")));
        citizen.setCreatedAt(rs.getTimestamp("date_creation"));
        citizen.setLastLogin(rs.getTimestamp("derniere_connexion"));

        // Citizen-specific fields
        citizen.setCitizenId(rs.getInt("citoyen_id"));
        citizen.setIdCardNumber(rs.getString("numero_cni"));
        citizen.setDateOfBirth(rs.getDate("date_naissance"));
        citizen.setPlaceOfBirth(rs.getString("lieu_naissance"));
        citizen.setFullAddress(rs.getString("adresse_complete"));
        citizen.setOccupation(rs.getString("profession"));

        return citizen;
    }
}