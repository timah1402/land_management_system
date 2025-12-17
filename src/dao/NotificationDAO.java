package dao;

import database.DatabaseConfig;
import models.Notification;
import models.Notification.NotificationType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Notification operations
 */
public class NotificationDAO {

    /**
     * Create a new notification
     */
    public boolean createNotification(Notification notification) {
        String sql = "INSERT INTO Notifications (user_id, type_notification, titre, message, " +
                "lien_reference) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getType().name());
            pstmt.setString(3, notification.getTitle());
            pstmt.setString(4, notification.getMessage());
            pstmt.setString(5, notification.getReferenceLink());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get notification by ID
     */
    public Notification getNotificationById(int notificationId) {
        String sql = "SELECT * FROM Notifications WHERE notification_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractNotificationFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting notification by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mark notification as read
     */
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET lue = 1, date_lecture = CURRENT_TIMESTAMP " +
                "WHERE notification_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE Notifications SET lue = 1, date_lecture = CURRENT_TIMESTAMP " +
                "WHERE user_id = ? AND lue = 0";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete notification
     */
    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete all notifications for a user
     */
    public boolean deleteAllForUser(int userId) {
        String sql = "DELETE FROM Notifications WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting all notifications: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all notifications
     */
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Get notifications by user
     */
    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE user_id = ? ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting notifications by user: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Get unread notifications by user
     */
    public List<Notification> getUnreadNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE user_id = ? AND lue = 0 " +
                "ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting unread notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Get notifications by type
     */
    public List<Notification> getNotificationsByType(int userId, NotificationType type) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE user_id = ? AND type_notification = ? " +
                "ORDER BY date_creation DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting notifications by type: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Get unread notification count for user
     */
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE user_id = ? AND lue = 0";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting unread count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total notification count
     */
    public int getNotificationCount() {
        String sql = "SELECT COUNT(*) FROM Notifications";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting notification count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract Notification object from ResultSet
     */
    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setType(NotificationType.valueOf(rs.getString("type_notification")));
        notification.setTitle(rs.getString("titre"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getInt("lue") == 1);
        notification.setCreatedAt(rs.getTimestamp("date_creation"));
        notification.setReadAt(rs.getTimestamp("date_lecture"));
        notification.setReferenceLink(rs.getString("lien_reference"));
        return notification;
    }
}