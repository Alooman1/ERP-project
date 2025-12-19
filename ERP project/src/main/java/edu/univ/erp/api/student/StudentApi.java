package edu.univ.erp.api.student;

import edu.univ.erp.domain.DetailedGradeRow;
import edu.univ.erp.domain.FinalGradeRow;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.EnrollmentService;

import edu.univ.erp.domain.RegisteredCourse;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.NotificationService;
import edu.univ.erp.domain.Notification;
import java.util.List;

public class StudentApi {

    private final StudentService studentService;
    private final GradeService gradeService;
    private final EnrollmentService enrollmentService;

    public StudentApi() {
        // Set up services for student tasks
        this.gradeService = new GradeService();
        this.studentService = new StudentService();
        this.enrollmentService = new EnrollmentService();
    }

    // Get student's profile information
    public Student getStudentProfile(int userId) {
        return studentService.getStudentProfile(userId);
    }
    private final NotificationService notificationService = new NotificationService();

    // Get student's messages and notifications
    public java.util.List<edu.univ.erp.domain.Notification> getNotifications(int userId) {
        return notificationService.getUserNotifications(userId);
    }

    // Check if student has unread messages
    public boolean hasUnreadNotifications(int userId) {
        return notificationService.hasUnread(userId);
    }

    // Mark a notification as read
    public void markNotificationRead(int notificationId) {
        notificationService.markRead(notificationId);
    }

    // Sign up for a class section
    public String registerForSection(int studentId, int sectionId) {
        return enrollmentService.registerForSection(studentId, sectionId);
    }

    // Drop a class section
    public String dropSection(int studentId, int sectionId) {
        return enrollmentService.dropSection(studentId, sectionId);
    }

    // Get final grades for all completed courses
    public List<FinalGradeRow> getFinalGrades(int userId) {
        return gradeService.getFinalGrades(userId);
    }

    // Change student's display name
    public boolean updateStudentName(int userId, String newName) {
        return studentService.updateStudentName(userId, newName);
    }

    // Get detailed breakdown of grades for each assignment type
    public List<DetailedGradeRow> getComponentGrades(int userId) {
        return gradeService.getComponentGrades(userId);
    }

    // Get list of classes student is currently taking
    public List<RegisteredCourse> getRegisteredCourses(int userId) {
        return studentService.getRegisteredCourses(userId);
    }
}