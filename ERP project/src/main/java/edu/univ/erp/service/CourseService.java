package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.util.TimeUtil;

import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseService {

    private final CourseDAO courseDAO;
    private final InstructorDAO instructorDAO;
    private final EnrollmentDAO enrollmentDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.instructorDAO = new InstructorDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    // Get all courses in the system
    public List<CatalogItem> getAllCourses() {
        return courseDAO.getAllCoursesSimple();
    }

    // Get full catalog with section details
    public List<CatalogItem> getFullCatalog() {
        return courseDAO.getFullCatalog();
    }

    // Get detailed information about all sections
    public List<CatalogItem> getAllSectionsDetailed() {
        return courseDAO.getAllSectionsDetailed();
    }

    // Assign an instructor to teach a section
    public String assignInstructorToSection(int sectionId, int instructorId) {
        // Get the section time to check for conflicts
        String newSectionTime = courseDAO.getSectionTime(sectionId);
        if (newSectionTime != null && !newSectionTime.isEmpty()) {
            List<AssignedSection> currentSchedule = instructorDAO.getAssignedSections(instructorId);

            // Check if instructor has time conflict
            for (AssignedSection existing : currentSchedule) {
                if (existing.getSectionId() == sectionId) continue;
                if (TimeUtil.checkOverlap(newSectionTime, existing.getDayTime())) {
                    return "Conflict! Instructor is teaching " +
                            existing.getCourseCode() + " at that time.";
                }
            }
        }

        boolean success = courseDAO.assignInstructorToSection(sectionId, instructorId);
        return success ? "Success: Instructor assigned."
                : "Error: Could not assign instructor.";
    }

    // Create a new course in the system
    public String createCourse(String code, String title, int credits) {
        if (code == null || code.trim().isEmpty() ||
                title == null || title.trim().isEmpty()) {
            return "Error: Course Code and Title are required.";
        }
        if (credits <= 0) {
            return "Error: Credits must be a positive number.";
        }

        boolean success = courseDAO.createCourse(code.toUpperCase(), title, credits);

        if (success) {
            return "Success: Course " + code.toUpperCase() + " created.";
        } else {
            return "Error: Course Code '" + code.toUpperCase() + "' may already exist.";
        }
    }

    // Update an existing course's information
    public String updateCourse(String code, String title, int credits) {
        if (code == null) {
            return "Error: No course selected.";
        }
        if (title == null || title.trim().isEmpty() || credits <= 0) {
            return "Error: Title and positive credits are required.";
        }

        boolean success = courseDAO.updateCourse(code, title, credits);
        return success ? "Success: Course " + code + " updated."
                : "Error: Could not update course.";
    }

    // Create a new section for a course
    public String createSection(String courseCode, String capacityStr,
                                String dayTime, String room,
                                String semester, String yearStr,
                                String regDeadline, String dropDeadline) {

        if (courseCode == null) return "Error: You must select a course.";
        if (dayTime == null || dayTime.trim().isEmpty() ||
                room == null || room.trim().isEmpty() ||
                semester == null || semester.trim().isEmpty()) {
            return "Error: Day/Time, Room, and Semester are required.";
        }

        // Convert string inputs to numbers
        int capacity;
        int year;
        try {
            capacity = Integer.parseInt(capacityStr);
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return "Error: Capacity and Year must be numbers.";
        }

        if (capacity <= 0 || year < 2020) {
            return "Error: Please enter valid Capacity and Year.";
        }

        // Check for room conflicts
        List<CatalogItem> sameRoomSections =
                courseDAO.getSectionsByRoomAndTerm(room, semester, year);

        for (CatalogItem existing : sameRoomSections) {
            if (TimeUtil.checkOverlap(dayTime, existing.getDayTime())) {
                return "Error: Room clash. " + room + " already has " +
                        existing.getCourseCode() + " at " + existing.getDayTime() +
                        ". Please choose a different time or room.";
            }
        }

        // Create the section with deadlines
        boolean success = courseDAO.createSection(courseCode, capacity, dayTime, room, semester, year, regDeadline, dropDeadline);

        if (success) {
            return "Success: New section for " + courseCode + " created.";
        } else {
            return "Error: Could not create section.";
        }
    }

    // Update a section's schedule information
    public String updateSectionSchedule(int sectionId, String dayTime, String room) {
        if (dayTime == null || dayTime.trim().isEmpty() ||
                room == null || room.trim().isEmpty()) {
            return "Error: Day/Time and Room are required.";
        }

        // Get section's current term information
        Map<String, Object> term = courseDAO.getSectionSemesterYear(sectionId);
        if (term == null) {
            return "Error: Section not found.";
        }
        String semester = (String) term.get("semester");
        int year = (int) term.get("year");

        // Check for room conflicts
        List<CatalogItem> sameRoomSections =
                courseDAO.getSectionsByRoomAndTerm(room, semester, year);

        for (CatalogItem existing : sameRoomSections) {
            if (existing.getSectionId() == sectionId) continue;
            if (TimeUtil.checkOverlap(dayTime, existing.getDayTime())) {
                return "Error: Room clash with " +
                        existing.getCourseCode() + " (" + existing.getDayTime() + ").";
            }
        }

        boolean success = courseDAO.updateSectionSchedule(sectionId, dayTime, room);
        return success ? "Success: Section schedule updated."
                : "Error: Could not update section schedule.";
    }

    // Delete a course and all its sections
    public String deleteCourseAndSections(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return "Error: Course code is required.";
        }

        try (Connection conn = DBConnectionManager.getErpConnection()) {
            conn.setAutoCommit(false);

            // Check if there are any active enrollments
            String checkSql = "SELECT COUNT(*) FROM enrollments e " +
                    "JOIN sections s ON e.section_id = s.section_id " +
                    "WHERE s.course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setString(1, courseCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return "Error: Cannot delete course. " +
                                rs.getInt(1) + " active enrollments exist.";
                    }
                }
            }

            // Delete all sections for this course
            courseDAO.deleteSectionsByCourse(conn, courseCode);

            // Delete the course itself
            if (courseDAO.deleteCourse(conn, courseCode)) {
                conn.commit();
                return "Success: Course " + courseCode +
                        " and all associated sections deleted.";
            } else {
                conn.rollback();
                return "Error: Could not delete course record.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Critical Error: Deletion failed due to database issue.";
        }
    }

    // Delete a single section
    public String deleteSection(int sectionId) {
        if (courseDAO.deleteSection(sectionId)) {
            return "Success: Section deleted.";
        } else {
            return "Error: Could not delete section (Check if students are enrolled).";
        }
    }
}