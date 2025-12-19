package edu.univ.erp.domain;

// Shows available courses in catalog that students can register for
public class CatalogItem {

    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final int credits;
    private final String instructorName;
    private final String dayTime;
    private final String room;
    private final int capacity;
    private final int enrolled;
    // New Fields for registration deadlines
    private final String regDeadline;
    private final String dropDeadline;

    // Creates a course offering with all details including deadlines
    public CatalogItem(int sectionId, String courseCode, String courseTitle, int credits,
                       String instructorName, String dayTime, String room, int capacity, int enrolled,
                       String regDeadline, String dropDeadline) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.instructorName = instructorName;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.regDeadline = regDeadline;
        this.dropDeadline = dropDeadline;
    }

    // Get section ID number
    public int getSectionId() { return sectionId; }
    // Get course code like "CS101"
    public String getCourseCode() { return courseCode; }
    // Get full course name
    public String getCourseTitle() { return courseTitle; }
    // Get how many credits this course gives
    public int getCredits() { return credits; }
    // Get teacher's name
    public String getInstructorName() { return instructorName; }
    // Get class schedule (days and time)
    public String getDayTime() { return dayTime; }
    // Get classroom location
    public String getRoom() { return room; }
    // Get maximum students allowed
    public int getCapacity() { return capacity; }
    // Get how many students already enrolled
    public int getEnrolled() { return enrolled; }
    // Get last date to register
    public String getRegDeadline() { return regDeadline; }
    // Get last date to drop course
    public String getDropDeadline() { return dropDeadline; }

    // Show how many seats taken vs total available
    public String getAvailability() {
        return enrolled + " / " + capacity;
    }
}