package edu.univ.erp.domain;

// Stores login information for user authentication
public class AuthCredentials {
    private final int userId;
    private final String username;
    private final String role;
    private final String passwordHash;

    // Creates login credentials for a user
    public AuthCredentials(int userId, String username, String role, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    // Get user's ID number
    public int getUserId() {
        return userId;
    }

    // Get user's login name
    public String getUsername() {
        return username;
    }

    // Get user's role (Admin/Instructor/Student)
    public String getRole() {
        return role;
    }

    // Get encrypted password for security check
    public String getPasswordHash() {
        return passwordHash;
    }
}