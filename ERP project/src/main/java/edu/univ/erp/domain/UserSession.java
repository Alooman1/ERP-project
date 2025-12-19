package edu.univ.erp.domain;

// Tracks who is currently logged into the system
public class UserSession {
    private final int userId;
    private final String username;
    private final String role;

    // Stores login information for current user
    public UserSession(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Get user's ID number
    public int getUserId() {
        return userId;
    }

    // Get user's login name
    public String getUsername() {
        return username;
    }

    // Get user type (Admin, Instructor, Student)
    public String getRole() {
        return role;
    }

    // Check if logged in user is admin
    public boolean isAdmin() {
        return "Admin".equals(role);
    }

    // Check if logged in user is instructor
    public boolean isInstructor() {
        return "Instructor".equals(role);
    }

    // Check if logged in user is student
    public boolean isStudent() {
        return "Student".equals(role);
    }
}