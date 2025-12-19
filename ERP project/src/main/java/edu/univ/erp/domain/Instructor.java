package edu.univ.erp.domain;

// Stores information about instructor users
public class Instructor {
    private final int userId;
    private final String fullName;
    private final String department;
    private final String username;

    // Creates an instructor record with department info
    public Instructor(int userId, String fullName, String department, String username) {
        this.userId = userId;
        this.fullName = fullName;
        this.department = department;
        this.username = username;
    }

    // Get instructor's user ID
    public int getUserId() { return userId; }
    // Get instructor's full name
    public String getFullName() { return fullName; }
    // Get instructor's department
    public String getDepartment() { return department; }
    // Get instructor's login username
    public String getUsername() { return username; }
}