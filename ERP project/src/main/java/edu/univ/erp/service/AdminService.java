package edu.univ.erp.service;

import edu.univ.erp.data.*;
import org.mindrot.jbcrypt.BCrypt;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Admin;
import java.util.List;

import edu.univ.erp.domain.AuthCredentials;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminService {

    private final UserAuthDAO userAuthDAO;
    private final StudentDAO studentDAO;
    private final InstructorDAO instructorDAO;
    private final AdminDAO adminDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final GradeDAO gradeDAO;
    private final NotificationDAO notificationDAO;

    public AdminService() {
        // Setting up all the data access objects we need
        this.userAuthDAO = new UserAuthDAO();
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
        this.adminDAO = new AdminDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.gradeDAO = new GradeDAO();
        this.notificationDAO = new NotificationDAO();
    }
    public AdminService(UserAuthDAO userAuthDAO, StudentDAO studentDAO, InstructorDAO instructorDAO,
                        AdminDAO adminDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO,
                        NotificationDAO notificationDAO, GradeDAO gradeDAO) {
        this.userAuthDAO = userAuthDAO;
        this.studentDAO = studentDAO;
        this.instructorDAO = instructorDAO;
        this.adminDAO = adminDAO;
        this.courseDAO = courseDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.notificationDAO = notificationDAO;
        this.gradeDAO = gradeDAO;
    }


    // Get admin's own profile information
    public edu.univ.erp.domain.Admin getAdminProfile(int userId) {
        return adminDAO.getAdminProfile(userId);
    }

    // Update admin's name in the system
    public boolean updateAdminName(int userId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false; // Don't allow empty names
        }
        return adminDAO.updateAdminName(userId, newName);
    }

    // Create a new user account (student, instructor, or admin)
    public String createNewUser(String role, String username, String password, String fullName, 
                                String rollNo, String program, int year, String department) {

        // Check if required fields are filled
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty()) {
            return "Username, Password, and Full Name are required.";
        }

        // Encrypt the password for security
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create the user account in authentication system
        int newUserId = userAuthDAO.createUser(username, hashedPassword, role);

        if (newUserId == -1) {
            return "Error: Username '" + username + "' may already be taken.";
        }

        // Create the user profile based on their role
        boolean profileCreated = false;
        switch (role) {
            case "Student":
                if (rollNo == null || rollNo.trim().isEmpty()) {
                    return "Error: Roll Number is required for students.";
                }
                profileCreated = studentDAO.createStudentProfile(newUserId, fullName, rollNo, program, year);
                break;
            case "Instructor":
                profileCreated = instructorDAO.createInstructorProfile(newUserId, fullName, department);
                break;
            case "Admin":
                profileCreated = adminDAO.createAdminProfile(newUserId, fullName);
                break;
        }

        // Return success or error message
        if (profileCreated) {
            return "Success: Created new " + role + " '" + username + "'.";
        } else {
            return "Critical Error: Could not create user profile. Roll No may be a duplicate.";
        }
    }

    // Check if system is in maintenance mode
    public boolean getMaintenanceStatus() {
        return adminDAO.isMaintenanceModeOn();
    }

    // Turn maintenance mode on or off
    public boolean setMaintenanceStatus(boolean isNowOn) {
        return adminDAO.setMaintenanceMode(isNowOn);
    }

    // Send notification to a specific user by their ID
    public boolean sendNotification(int recipientId, String message) {
        if (recipientId <= 0 || message == null || message.trim().isEmpty()) {
            System.err.println("ERROR: Failed to send notification. Invalid recipient ID or empty message.");
            return false;
        }

        boolean inserted = notificationDAO.createNotification(recipientId, message);

        if (!inserted) {
            System.err.println("ERROR: Failed to insert notification into database.");
            return false;
        }

        System.out.println("LOG: Notification inserted for userId = " + recipientId);
        return true;
    }

    // Send notification to a user by their username
    public boolean sendNotificationToUsername(String username, String message) {
        if (username == null || username.trim().isEmpty()) return false;
        if (message == null || message.trim().isEmpty()) return false;

        // Find the user by username first
        AuthCredentials credentials = userAuthDAO.findUserByUsername(username);

        if (credentials == null) {
            System.err.println("ERROR: Username '" + username + "' not found.");
            return false;
        }

        int userId = credentials.getUserId();
        boolean inserted = notificationDAO.createNotification(userId, message);

        if (!inserted) {
            System.err.println("ERROR: Failed to insert notification into DB.");
            return false;
        }

        return true;
    }

    // Get user authentication details by username
    public edu.univ.erp.domain.AuthCredentials getAuthCredentialsByUsername(String username) {
        return userAuthDAO.findUserByUsername(username);
    }

    // Get list of all students in the system
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    // Get list of all instructors in the system
    public List<Instructor> getAllInstructors() {
        return instructorDAO.getAllInstructors();
    }

    // Get list of all admins in the system
    public List<Admin> getAllAdmins() {
        return adminDAO.getAllAdmins(); 
    }

    // Get a student's class schedule
    public List<CatalogItem> getStudentSchedule(int studentId) {
        return courseDAO.getRegisteredCatalogItems(studentId);
    }

    // Get an instructor's teaching schedule
    public List<AssignedSection> getInstructorSchedule(int instructorId) {
        return instructorDAO.getAssignedSections(instructorId);
    }

    // Delete a user account completely from the system
    public String deleteUser(String username) {
        // First find the user by username
        AuthCredentials credentials = userAuthDAO.findUserByUsername(username);

        if (credentials == null) {
            return "Error: User '" + username + "' not found.";
        }

        int userId = credentials.getUserId();
        String role = credentials.getRole();

        try (Connection erpConn = DBConnectionManager.getErpConnection();
             Connection authConn = DBConnectionManager.getAuthConnection()) {

            erpConn.setAutoCommit(false);
            authConn.setAutoCommit(false);

            // Delete user data based on their role
            boolean profileDeleted = switch (role) {
                case "Student" -> {
                    // Delete student's grades, enrollments and notifications
                    gradeDAO.deleteGradesByStudentId(userId);
                    enrollmentDAO.deleteEnrollmentsByStudentId(userId);
                    notificationDAO.deleteNotificationsByUserId(erpConn, userId);
                    yield studentDAO.deleteStudentProfile(erpConn, userId);
                }
                case "Instructor" -> {
                    // Delete instructor's notifications
                    notificationDAO.deleteNotificationsByUserId(erpConn, userId);
                    yield instructorDAO.deleteInstructorProfile(erpConn, userId);
                }
                case "Admin" -> {
                    // Delete admin's notifications
                    notificationDAO.deleteNotificationsByUserId(erpConn, userId);
                    yield adminDAO.deleteAdminProfile(erpConn, userId);
                }
                default -> true;
            };

            if (!profileDeleted) {
                erpConn.rollback();
                return "Error: Failed to delete user profile in ERP DB.";
            }
            erpConn.commit();

            // Finally delete the authentication record
            if (userAuthDAO.deleteUser(authConn, userId)) {
                authConn.commit();
                return "Success: User '" + username + "' (" + role + ") deleted.";
            } else {
                authConn.rollback();
                return "Critical Error: User profile deleted, but Auth record failed to delete.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Critical Error: Deletion failed due to database issue: " + e.getMessage();
        }
    }
}