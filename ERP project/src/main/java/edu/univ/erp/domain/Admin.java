package edu.univ.erp.domain;

// Stores information about admin users
public class Admin {
    private final int userId;
    private final String fullName;
    private final String username;

    // Creates an admin user record
    public Admin(int userId, String fullName, String username) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
    }

    // Get admin's user ID
    public int getUserId() {
        return userId;
    }

    // Get admin's full name
    public String getFullName() {
        return fullName;
    }

    // Get admin's login username
    public String getUsername() {
        return username;
    }
}