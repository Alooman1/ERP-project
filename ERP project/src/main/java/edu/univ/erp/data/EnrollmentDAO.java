package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for managing student course enrollments.
public class EnrollmentDAO {

    // Counts how many students are currently enrolled in a specific section.
    // Used to check capacity limits.
    public int getEnrolledCount(Connection conn, int sectionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND status = 'enrolled'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Retrieves the unique Enrollment ID for a specific Student-Section pair.
    // Useful for deleting or modifying a specific enrollment record.
    public int getEnrollmentId(Connection conn, int studentId, int sectionId) throws SQLException {
        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("enrollment_id");
                }
            }
        }
        return -1; // Return -1 if not found
    }

    // Removes all enrollment records for a student (used when deleting a user).
    public boolean deleteEnrollmentsByStudentId(int studentId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns a list of User IDs for all students in a given section.
    // Used for sending broadcast notifications to a class.
    public List<Integer> getStudentUserIdsBySection(int sectionId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT student_id FROM enrollments WHERE section_id = ? AND status = 'enrolled'";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("student_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    // Checks if a student is already registered for a specific section.
    public boolean isStudentEnrolled(Connection conn, int studentId, int sectionId) throws SQLException {
        String sql = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'enrolled' LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if a row is found
            }
        }
    }

    // Registers a student for a section.
    public boolean registerStudent(Connection conn, int studentId, int sectionId) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1; // true if insert was successful
        }
    }

    // Drops (deletes) a student's registration for a section.
    public boolean dropStudent(Connection conn, int studentId, int sectionId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1; // true if delete was successful
        }
    }
}