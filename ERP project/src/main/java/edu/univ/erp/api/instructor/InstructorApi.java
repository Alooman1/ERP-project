package edu.univ.erp.api.instructor;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.InstructorService;
import java.util.List;
import edu.univ.erp.domain.GradebookRow;
import edu.univ.erp.service.NotificationService;

public class InstructorApi {

    private final InstructorService instructorService;
    private final GradeService gradeService;

    public InstructorApi(UserSession session) {
        // Set up services for instructor tasks
        this.instructorService = new InstructorService();
        this.gradeService = new GradeService();
    }
    private final NotificationService notificationService = new NotificationService();

    // Get instructor's messages and notifications
    public java.util.List<edu.univ.erp.domain.Notification> getNotifications(int userId) {
        return notificationService.getUserNotifications(userId);
    }

    // Send a message to all students in a class section
    public boolean sendSectionNotification(int sectionId, String message) {
        return notificationService.broadcastToSection(sectionId, message);
    }

    // Check if instructor has unread messages
    public boolean hasUnreadNotifications(int userId) {
        return notificationService.hasUnread(userId);
    }

    // Mark a notification as read
    public void markNotificationRead(int notificationId) {
        notificationService.markRead(notificationId);
    }

    // Get instructor's profile information
    public Instructor getInstructorProfile(int userId) {
        return instructorService.getInstructorProfile(userId);
    }

    // Get list of classes the instructor is teaching
    public List<AssignedSection> getAssignedSections(int instructorId) {
        return instructorService.getAssignedSections(instructorId);
    }

    // Get gradebook with all students and grades for a section
    public List<GradebookRow> getGradebook(int sectionId) {
        return gradeService.getGradebookForSection(sectionId);
    }

    // Enter or update a grade for a student
    public boolean saveGrade(int enrollmentId, String component, Double score) {
        return gradeService.saveGrade(enrollmentId, component, score);
    }

    // Get statistics about class performance
    public List<ClassStats> getSectionStats(int sectionId) {
        return gradeService.getSectionStats(sectionId);
    }

    // Change instructor's display name
    public boolean updateInstructorName(int userId, String newName) {
        return instructorService.updateInstructorName(userId, newName);
    }
}