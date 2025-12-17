package dao;

import database.DatabaseConfig;
import models.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AuditLog operations
 */
public class AuditLogDAO {

    /**
     * Create a new audit log entry
     */
    public boolean createLog(AuditLog log) {
        String sql = "INSERT INTO AuditLog (user_id, action, table_affectee, enregistrement_id, " +
                "anciennes_valeurs, nouvelles_valeurs, adresse_ip) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (log.getUserId() != null) {
                pstmt.setInt(1, log.getUserId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, log.getAction());
            pstmt.setString(3, log.getAffectedTable());

            if (log.getRecordId() != null) {
                pstmt.setInt(4, log.getRecordId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, log.getOldValues());
            pstmt.setString(6, log.getNewValues());
            pstmt.setString(7, log.getIpAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    log.setLogId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating audit log: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Quick log method (simplified)
     */
    public boolean log(Integer userId, String action, String tableName, Integer recordId, String ipAddress) {
        AuditLog log = new AuditLog(userId, action, tableName, recordId, ipAddress);
        return createLog(log);
    }

    /**
     * Log with old and new values
     */
    public boolean logWithValues(Integer userId, String action, String tableName, Integer recordId,
                                 String oldValues, String newValues, String ipAddress) {
        AuditLog log = new AuditLog(userId, action, tableName, recordId, ipAddress);
        log.setOldValues(oldValues);
        log.setNewValues(newValues);
        return createLog(log);
    }

    /**
     * Get audit log by ID
     */
    public AuditLog getLogById(int logId) {
        String sql = "SELECT * FROM AuditLog WHERE log_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, logId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractLogFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting audit log by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all audit logs
     */
    public List<AuditLog> getAllLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all audit logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get logs by user
     */
    public List<AuditLog> getLogsByUser(int userId) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting logs by user: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get logs by table
     */
    public List<AuditLog> getLogsByTable(String tableName) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE table_affectee = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting logs by table: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get logs by action
     */
    public List<AuditLog> getLogsByAction(String action) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE action = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, action);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting logs by action: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get logs for a specific record
     */
    public List<AuditLog> getLogsByRecord(String tableName, int recordId) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE table_affectee = ? AND enregistrement_id = ? " +
                "ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tableName);
            pstmt.setInt(2, recordId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting logs by record: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get logs within date range
     */
    public List<AuditLog> getLogsByDateRange(Timestamp startDate, Timestamp endDate) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting logs by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get recent logs (last N entries)
     */
    public List<AuditLog> getRecentLogs(int limit) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(extractLogFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting recent logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Delete old logs (older than specified days)
     */
    public boolean deleteOldLogs(int daysOld) {
        String sql = "DELETE FROM AuditLog WHERE timestamp < datetime('now', '-' || ? || ' days')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, daysOld);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting old logs: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get log count
     */
    public int getLogCount() {
        String sql = "SELECT COUNT(*) FROM AuditLog";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting log count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract AuditLog object from ResultSet
     */
    private AuditLog extractLogFromResultSet(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            log.setUserId(userId);
        }

        log.setAction(rs.getString("action"));
        log.setAffectedTable(rs.getString("table_affectee"));

        int recordId = rs.getInt("enregistrement_id");
        if (!rs.wasNull()) {
            log.setRecordId(recordId);
        }

        log.setOldValues(rs.getString("anciennes_valeurs"));
        log.setNewValues(rs.getString("nouvelles_valeurs"));
        log.setIpAddress(rs.getString("adresse_ip"));
        log.setTimestamp(rs.getTimestamp("timestamp"));

        return log;
    }
}