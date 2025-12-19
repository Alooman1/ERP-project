package edu.univ.erp.domain;

// Shows final grades and credits for completed courses
public class FinalGradeRow {
    private final String courseCode;
    private final String courseTitle;
    private final int credits;
    private final String finalGrade;

    // Creates final grade record for a completed course
    public FinalGradeRow(String courseCode, String courseTitle, int credits, String finalGrade) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.finalGrade = finalGrade;
    }

    // Get course code
    public String getCourseCode() { return courseCode; }
    // Get course name
    public String getCourseTitle() { return courseTitle; }
    // Get credit hours earned
    public int getCredits() { return credits; }
    // Get final grade (A, B, C, etc.)
    public String getFinalGrade() { return finalGrade; }
}