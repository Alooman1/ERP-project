package edu.univ.erp.domain;

// Shows which course sections are assigned to an instructor
public class AssignedSection {
    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final String dayTime;
    private final String room;

    // Creates a section that instructor teaches
    public AssignedSection(int sectionId, String courseCode, String courseTitle, String dayTime, String room) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.dayTime = dayTime;
        this.room = room;
    }

    // Get section ID number
    public int getSectionId() { return sectionId; }
    // Get course code
    public String getCourseCode() { return courseCode; }
    // Get course name
    public String getCourseTitle() { return courseTitle; }
    // Get class timing
    public String getDayTime() { return dayTime; }
    // Get classroom
    public String getRoom() { return room; }
    // Get course code and name together
    public String getSubjectName() {
        return this.courseCode + ": " + this.courseTitle;
    }
}