package edu.univ.erp.data;

import edu.univ.erp.domain.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO for managing user notifications in the ERP database.
public class NotificationDAO {

    // Retrieves all notifications for a specific user, ordered by newest first.
    public List<Notification> getNotifications(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT notification_id, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                        rs.getInt("notification_id"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Marks a specific notification as 'Read'.
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deletes a single notification.
    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Returns the count of unread notifications for a user (for the bell icon badge).
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Creates a new notification for a specific user.
    public boolean createNotification(int userId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, message);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper to get all user IDs that belong to a specific role (e.g., all Students).
    // Connects to Auth DB to check roles.
    public List<Integer> getUserIdsByRole(String role) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT user_id FROM auth_db.users_auth WHERE role = ?";
        try (Connection conn = DBConnectionManager.getAuthConnection(); // Note: Auth DB
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    // Deletes all notifications for a user (used during user deletion cleanup).
    public boolean deleteNotificationsByUserId(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        // Note: We use the connection passed from the service layer to stay in the transaction.
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        }
    }

    // Clears all notifications for a user (User action: "Clear All").
    public boolean clearAllForUser(int userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}