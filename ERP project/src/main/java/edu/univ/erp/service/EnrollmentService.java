package edu.univ.erp.service;

import edu.univ.erp.data.DBConnectionManager;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.util.TimeUtil;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class EnrollmentService {

    private final EnrollmentDAO enrollmentDAO;
    private final GradeDAO gradeDAO;
    private final CourseDAO courseDAO;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.gradeDAO = new GradeDAO();
        this.courseDAO = new CourseDAO();
    }

    // Register a student for a course section
    public String registerForSection(int studentId, int sectionId) {
        try (Connection conn = DBConnectionManager.getErpConnection()) {
            conn.setAutoCommit(false);

            // 1. Check if registration deadline has passed
            Map<String, java.sql.Date> deadlines = courseDAO.getSectionDeadlines(sectionId);
            if (deadlines.containsKey("reg") && deadlines.get("reg") != null) {
                LocalDate deadline = deadlines.get("reg").toLocalDate();
                if (LocalDate.now().isAfter(deadline)) {
                    conn.rollback();
                    return "Registration closed. Deadline was " + deadline;
                }
            }

            // 2. Check if student is already in this section
            if (enrollmentDAO.isStudentEnrolled(conn, studentId, sectionId)) {
                conn.rollback();
                return "You are already registered in this section.";
            }

            // 3. Check if student is already in another section of same course
            String targetCourseCode = courseDAO.getCourseCodeForSection(sectionId);
            List<CatalogItem> currentSchedule = courseDAO.getRegisteredCatalogItems(studentId);

            for (CatalogItem item : currentSchedule) {
                if (item.getCourseCode().equals(targetCourseCode)) {
                    conn.rollback();
                    return "Duplicate Course! You are already in " + item.getCourseCode() +
                            " (Section " + item.getSectionId() + "). Drop it first.";
                }
            }

            // 4. Check for time conflicts with existing schedule
            String newSectionTime = courseDAO.getSectionTime(sectionId);
            for (CatalogItem item : currentSchedule) {
                if (TimeUtil.checkOverlap(newSectionTime, item.getDayTime())) {
                    conn.rollback();
                    return "Time Conflict! Clashes with " + item.getCourseCode();
                }
            }

            // 5. Check if section has available seats
            int capacity = 0;
            String sqlCap = "SELECT capacity FROM sections WHERE section_id = ? FOR UPDATE";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCap)) {
                stmt.setInt(1, sectionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) capacity = rs.getInt("capacity");
                }
            }

            int enrolledCount = enrollmentDAO.getEnrolledCount(conn, sectionId);
            if (enrolledCount >= capacity) {
                conn.rollback();
                return "Section is full.";
            }

            // 6. Finally register the student
            boolean success = enrollmentDAO.registerStudent(conn, studentId, sectionId);
            if (success) {
                conn.commit();
                return "Successfully registered!";
            } else {
                conn.rollback();
                return "Registration failed.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Registration failed." + e.getMessage();
        }
    }

    // Drop a student from a course section
    public String dropSection(int studentId, int sectionId) {
        try (Connection conn = DBConnectionManager.getErpConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Check if drop deadline has passed
                Map<String, java.sql.Date> deadlines = courseDAO.getSectionDeadlines(sectionId);
                if (deadlines.containsKey("drop") && deadlines.get("drop") != null) {
                    LocalDate deadline = deadlines.get("drop").toLocalDate();
                    if (LocalDate.now().isAfter(deadline)) {
                        conn.rollback();
                        return "Drop deadline passed (" + deadline + "). Cannot drop.";
                    }
                }

                // 2. Find the enrollment record
                int enrollmentId = enrollmentDAO.getEnrollmentId(conn, studentId, sectionId);

                if (enrollmentId == -1) {
                    conn.rollback();
                    return "Drop failed. You are not enrolled in this section.";
                }

                // 3. Delete any grades for this enrollment
                gradeDAO.deleteGradesForEnrollment(conn, enrollmentId);

                // 4. Remove the enrollment
                boolean success = enrollmentDAO.dropStudent(conn, studentId, sectionId);

                if (success) {
                    conn.commit();
                    return "Successfully dropped section.";
                } else {
                    conn.rollback();
                    return "Drop failed for an unknown reason.";
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Could not connect to database.";
        }
    }
}