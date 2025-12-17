package dao;

import database.DatabaseConfig;
import models.Admin;
import models.Admin.AccessLevel;
import models.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Admin operations
 */
public class AdminDAO {

    private UserDAO userDAO = new UserDAO();

    /**
     * Create a new admin
     */
    public boolean createAdmin(Admin admin) {
        // First, create the user
        admin.setRole(UserRole.ADMIN);
        boolean userCreated = userDAO.createUser(admin);

        if (!userCreated) {
            System.err.println("Failed to create user for admin");
            return false;
        }

        // Then create the admin entry with a fresh connection
        String sql = "INSERT INTO Administrateurs (user_id, niveau_acces, departement) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, admin.getUserId());
            pstmt.setString(2, admin.getAccessLevel().name());
            pstmt.setString(3, admin.getDepartment());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    admin.setAdminId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get admin by admin ID
     */
    public Admin getAdminById(int adminId) {
        String sql = "SELECT a.*, u.* FROM Administrateurs a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.admin_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting admin by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get admin by user ID
     */
    public Admin getAdminByUserId(int userId) {
        String sql = "SELECT a.*, u.* FROM Administrateurs a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting admin by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update admin
     */
    public boolean updateAdmin(Admin admin) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Update user information
            if (!userDAO.updateUser(admin)) {
                conn.rollback();
                return false;
            }

            // Update admin-specific information
            String sql = "UPDATE Administrateurs SET niveau_acces = ?, departement = ? WHERE admin_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, admin.getAccessLevel().name());
            pstmt.setString(2, admin.getDepartment());
            pstmt.setInt(3, admin.getAdminId());

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
            System.err.println("Error updating admin: " + e.getMessage());
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
     * Get all admins
     */
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM Administrateurs a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "ORDER BY u.date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all admins: " + e.getMessage());
            e.printStackTrace();
        }
        return admins;
    }

    /**
     * Get admins by access level
     */
    public List<Admin> getAdminsByAccessLevel(AccessLevel accessLevel) {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM Administrateurs a " +
                "JOIN Users u ON a.user_id = u.user_id " +
                "WHERE a.niveau_acces = ? " +
                "ORDER BY u.nom, u.prenom";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accessLevel.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting admins by access level: " + e.getMessage());
            e.printStackTrace();
        }
        return admins;
    }

    /**
     * Delete admin
     */
    public boolean deleteAdmin(int adminId) {
        Admin admin = getAdminById(adminId);
        if (admin != null) {
            return userDAO.deleteUser(admin.getUserId());
        }
        return false;
    }

    /**
     * Extract Admin object from ResultSet
     */
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin admin = new Admin();

        // User fields
        admin.setUserId(rs.getInt("user_id"));
        admin.setLastName(rs.getString("nom"));
        admin.setFirstName(rs.getString("prenom"));
        admin.setEmail(rs.getString("email"));
        admin.setPhone(rs.getString("telephone"));
        admin.setPassword(rs.getString("mot_de_passe"));
        admin.setRole(UserRole.valueOf(rs.getString("role")));
        admin.setAccountStatus(models.User.AccountStatus.valueOf(rs.getString("account_status")));
        admin.setCreatedAt(rs.getTimestamp("date_creation"));
        admin.setLastLogin(rs.getTimestamp("derniere_connexion"));

        // Admin-specific fields
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setAccessLevel(AccessLevel.valueOf(rs.getString("niveau_acces")));
        admin.setDepartment(rs.getString("departement"));

        return admin;
    }
}