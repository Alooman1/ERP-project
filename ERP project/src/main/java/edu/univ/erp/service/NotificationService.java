package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.NotificationDAO;
import edu.univ.erp.domain.Notification;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;
    private final EnrollmentDAO enrollmentDAO;

    public NotificationService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.notificationDAO = new NotificationDAO();
    }

    // Get all notifications for a user
    public List<Notification> getUserNotifications(int userId) {
        return notificationDAO.getNotifications(userId);
    }

    // Mark a notification as read
    public void markRead(int notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    // Check if user has unread notifications
    public boolean hasUnread(int userId) {
        return notificationDAO.getUnreadCount(userId) > 0;
    }

    // Delete a specific notification
    public boolean deleteNotification(int notificationId) {
        return notificationDAO.deleteNotification(notificationId);
    }

    // Clear all notifications for a user
    public boolean clearAllForUser(int userId) {
        return notificationDAO.clearAllForUser(userId);
    }

    // Send notification to all students in a section
    public boolean broadcastToSection(int sectionId, String message) {
        // Get all student IDs in the section
        List<Integer> userIds = enrollmentDAO.getStudentUserIdsBySection(sectionId);
        if (userIds.isEmpty()) return false;

        // Send notification to each student
        boolean success = true;
        for (int id : userIds) {
            if (!notificationDAO.createNotification(id, message)) {
                success = false;
            }
        }
        return success;
    }

    // Send notification to a specific user
    public boolean sendIndividualNotification(int userId, String message) {
        return notificationDAO.createNotification(userId, message);
    }

    // Send notification to all users with a specific role
    public boolean broadcastToRole(String role, String message) {
        // Get all user IDs with the specified role
        List<Integer> userIds = notificationDAO.getUserIdsByRole(role);
        if (userIds.isEmpty()) return false;

        // Send notification to each user
        boolean success = true;
        for (int id : userIds) {
            if (!notificationDAO.createNotification(id, message)) {
                success = false;
            }
        }
        return success;
    }
}