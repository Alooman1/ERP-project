package edu.univ.erp.data;

import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.Instructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for Instructor-related DB operations.
public class InstructorDAO {

    // Creates a new Instructor profile.
    public boolean createInstructorProfile(int userId, String fullName, String department) {
        String sql = "INSERT INTO instructors (user_id, full_name, department) VALUES (?, ?, ?)";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, fullName);
            stmt.setString(3, department);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves an instructor's profile (fetches username from Auth DB via Join).
    public Instructor getInstructorProfile(int userId) {
        String sql = "SELECT i.full_name, i.department, a.username " +
                     "FROM instructors i JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                     "WHERE i.user_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fullName = rs.getString("full_name");
                    String department = rs.getString("department");
                    String username = rs.getString("username");
                    return new Instructor(userId, fullName, department, username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates the instructor's display name.
    public boolean updateInstructorName(int userId, String newName) {
        String sql = "UPDATE instructors SET full_name = ? WHERE user_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1; // Success

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gets a list of sections assigned to a specific instructor.
    // Used to populate the "My Sections" grid on the Dashboard.
    public List<AssignedSection> getAssignedSections(int instructorId) {
        List<AssignedSection> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, c.code, c.title, s.day_time, s.room " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.code " +
                     "WHERE s.instructor_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(new AssignedSection(
                            rs.getInt("section_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("day_time"),
                            rs.getString("room")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    // Fetches all instructors in the system (for Admin assignment dropdowns).
    public List<edu.univ.erp.domain.Instructor> getAllInstructors() {
        List<edu.univ.erp.domain.Instructor> instructors = new java.util.ArrayList<>();
        String sql = "SELECT i.user_id, i.full_name, i.department, a.username " +
                     "FROM instructors i JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                     "ORDER BY i.full_name";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                instructors.add(new edu.univ.erp.domain.Instructor(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    // Deletes an Instructor Profile.
    // First unassigns them from sections, then deletes the record.
    public boolean deleteInstructorProfile(Connection conn, int userId) throws SQLException {
        String unassignSql = "UPDATE sections SET instructor_id = NULL WHERE instructor_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(unassignSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }

        String deleteSql = "DELETE FROM instructors WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        }
    }
}