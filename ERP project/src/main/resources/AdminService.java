package edu.univ.erp.service;

import edu.univ.erp.data.AdminDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.data.UserAuthDAO;
import org.mindrot.jbcrypt.BCrypt; // For hashing new passwords

/**
 * The "brain" for all Admin-level tasks.
 * This service coordinates all DAOs.
 */
public class AdminService {

    // The service needs all DAOs to do its job
    private final UserAuthDAO userAuthDAO;
    private final StudentDAO studentDAO;
    private final InstructorDAO instructorDAO;
    private final AdminDAO adminDAO;

    public AdminService() {
        this.userAuthDAO = new UserAuthDAO();
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
        this.adminDAO = new AdminDAO();
    }

    /**
     * The complete, multi-step process for creating a new user.
     * It hashes the password, creates the auth entry, then creates the profile.
     */
    public String createNewUser(String role, String username, String password, String fullName, 
                                String rollNo, String program, int year, String department) {
        
        // 1. Validate inputs
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty()) {
            return "Username, Password, and Full Name are required.";
        }

        // 2. Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // 3. Create the user in the Auth DB
        int newUserId = userAuthDAO.createUser(username, hashedPassword, role);

        if (newUserId == -1) {
            return "Error: Username '" + username + "' may already be taken.";
        }

        // 4. Create the user in the ERP DB (in the correct profile table)
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

        if (profileCreated) {
            return "Success: Created new " + role + " '" + username + "'.";
        } else {
            return "Critical Error: Could not create user profile. Roll No may be a duplicate.";
        }
    }

    /**
     * Gets the current status of Maintenance Mode.
     */
    public boolean getMaintenanceStatus() {
        return adminDAO.isMaintenanceModeOn();
    }

    /**
     * Sets the new status for Maintenance Mode.
     */
    public boolean setMaintenanceStatus(boolean isNowOn) {
        return adminDAO.setMaintenanceMode(isNowOn);
    }
}