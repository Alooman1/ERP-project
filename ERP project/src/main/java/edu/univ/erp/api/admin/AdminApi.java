package edu.univ.erp.api.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.NotificationService;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.Admin;
import java.util.List;

public class AdminApi {

    private final CourseService courseService;
    private final InstructorService instructorService;
    private final NotificationService notificationService;
    private final AdminService adminService;

    public AdminApi() {
        // Set up all the services we need for admin tasks
        this.courseService = new CourseService();
        this.instructorService = new InstructorService();
        this.notificationService = new NotificationService();
        this.adminService = new AdminService();
    }

    // Remove a user from the system by their username
    public String deleteUser(String username) {
        return adminService.deleteUser(username);
    }

    // Send a message to one specific user
    public boolean sendIndividualNotification(String username, String message) {
        return adminService.sendNotificationToUsername(username, message);
    }

    // Send a message to all users of a certain type (like all students)
    public boolean sendBroadcastNotification(String role, String message) {
        return notificationService.broadcastToRole(role, message);
    }

    // Get admin's own profile information
    public Admin getAdminProfile(int userId) {
        return adminService.getAdminProfile(userId);
    }

    // Change admin's display name
    public boolean updateAdminName(int userId, String newName) {
        return adminService.updateAdminName(userId, newName);
    }

    // Check if system is in maintenance mode
    public boolean isMaintenanceModeOn() {
        return adminService.getMaintenanceStatus();
    }

    // Turn maintenance mode on or off
    public boolean setMaintenanceMode(boolean isNowOn) {
        return adminService.setMaintenanceStatus(isNowOn);
    }

    // Check if admin has any unread messages
    public boolean hasUnreadNotifications(int userId) {
        return notificationService.hasUnread(userId);
    }

    // Create a new student account
    public String createStudent(String username, String password, String fullName,
                                String rollNo, String program, int year) {
        return adminService.createNewUser("Student", username, password, fullName,
                rollNo, program, year, null);
    }

    // Create a new teacher account
    public String createInstructor(String username, String password, String fullName,
                                   String department) {
        return adminService.createNewUser("Instructor", username, password, fullName,
                null, null, 0, department);
    }

    // Create a new admin account
    public String createAdmin(String username, String password, String fullName) {
        return adminService.createNewUser("Admin", username, password, fullName,
                null, null, 0, null);
    }

    // Get list of all courses in the system
    public List<CatalogItem> getAllCourses() {
        return courseService.getAllCourses();
    }

    // Add a new course to the system
    public String createCourse(String code, String title, int credits) {
        return courseService.createCourse(code, title, credits);
    }

    // Update information for an existing course
    public String updateCourse(String code, String title, int credits) {
        return courseService.updateCourse(code, title, credits);
    }

    // Create a class section for a course with registration deadlines
    public String createSection(String courseCode, String capacity, String dayTime,
                                String room, String semester, String year,
                                String regDeadline, String dropDeadline) {
        return courseService.createSection(courseCode, capacity, dayTime, room, semester, year, regDeadline, dropDeadline);
    }

    // Change when and where a section meets
    public String updateSectionSchedule(int sectionId, String dayTime, String room) {
        return courseService.updateSectionSchedule(sectionId, dayTime, room);
    }

    // Get list of all teachers
    public List<Instructor> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    // Get detailed info about all sections
    public List<CatalogItem> getAllSectionsDetailed() {
        return courseService.getAllSectionsDetailed();
    }

    // Assign a teacher to teach a section
    public String assignInstructorToSection(int sectionId, int instructorId) {
        return courseService.assignInstructorToSection(sectionId, instructorId);
    }

    // Remove a course and all its sections
    public String deleteCourse(String code) {
        return courseService.deleteCourseAndSections(code);
    }

    // Remove just one section (but keep the course)
    public String deleteSection(int sectionId) {
        return courseService.deleteSection(sectionId);
    }

    // Get list of all students
    public List<Student> getAllStudents() {
        return adminService.getAllStudents();
    }

    // See what classes a student is taking
    public List<CatalogItem> getStudentSchedule(int studentId) {
        return adminService.getStudentSchedule(studentId);
    }

    // See what classes a teacher is teaching
    public List<AssignedSection> getInstructorSchedule(int instructorId) {
        return adminService.getInstructorSchedule(instructorId);
    }

    // Get list of all admin users
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }
}