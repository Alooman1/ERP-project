package edu.univ.erp.domain;

// Stores all information about a student
public class Student {
    private final int userId;
    private final String rollNo;
    private final String fullName;
    private final String program;
    private final int year;
    private final String username;

    // Creates a student record with personal and academic info
    public Student(int userId, String rollNo, String fullName, String program, int year, String username) {
        this.userId = userId;
        this.rollNo = rollNo;
        this.fullName = fullName;
        this.program = program;
        this.year = year;
        this.username = username;
    }

    // Get student's system ID
    public int getUserId() { return userId; }
    // Get student's roll number
    public String getRollNo() { return rollNo; }
    // Get student's complete name
    public String getFullName() { return fullName; }
    // Get student's degree program
    public String getProgram() { return program; }
    // Get student's current year of study
    public int getYear() { return year; }
    // Get student's login username
    public String getUsername() { return username; }
}