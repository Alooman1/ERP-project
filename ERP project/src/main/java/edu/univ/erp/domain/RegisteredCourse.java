package edu.univ.erp.domain;

// Shows which courses a student has registered for
public class RegisteredCourse {
    private final String courseCode;
    private final String courseTitle;

    // Creates a course that student is taking
    public RegisteredCourse(String courseCode, String courseTitle) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
    }

    // Get short course code like "CS101"
    public String getCourseCode() { return courseCode; }
    // Get full course name
    public String getCourseTitle() { return courseTitle; }
}