package edu.univ.erp.data;

import edu.univ.erp.domain.RegisteredCourse;
import edu.univ.erp.domain.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Handles database operations for Student profiles in the ERP database.
public class StudentDAO {

    // Creates a new student profile linked to an existing user_id from the Auth DB.
    public boolean createStudentProfile(int userId, String fullName, String rollNo, String program, int year) {
        String sql = "INSERT INTO students (user_id, full_name, roll_no, program, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, fullName);
            stmt.setString(3, rollNo);
            stmt.setString(4, program);
            stmt.setInt(5, year);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1; // Return true if exactly one row was inserted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves a single student's profile details using their User ID.
    // Also joins with the Auth DB to fetch the username.
    public Student getStudentProfile(int userId) {
        String sql = "SELECT s.roll_no, s.full_name, s.program, s.year, a.username " +
                "FROM students s JOIN auth_db.users_auth a ON s.user_id = a.user_id " +
                "WHERE s.user_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            userId,
                            rs.getString("roll_no"),
                            rs.getString("full_name"),
                            rs.getString("program"),
                            rs.getInt("year"),
                            rs.getString("username") // Pass the fetched username
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if student not found
    }

    // Fetches a list of ALL students in the system (for Admin view).
    public java.util.List<edu.univ.erp.domain.Student> getAllStudents() {
        java.util.List<edu.univ.erp.domain.Student> students = new java.util.ArrayList<>();
        String sql = "SELECT s.user_id, s.roll_no, s.full_name, s.program, s.year, a.username " +
                "FROM students s JOIN auth_db.users_auth a ON s.user_id = a.user_id " +
                "ORDER BY s.roll_no";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(new edu.univ.erp.domain.Student(
                        rs.getInt("user_id"),
                        rs.getString("roll_no"),
                        rs.getString("full_name"),
                        rs.getString("program"),
                        rs.getInt("year"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Gets a list of courses a specific student is currently enrolled in.
    public List<RegisteredCourse> getRegisteredCourses(int userId) {
        List<RegisteredCourse> courses = new ArrayList<>();
        String sql = "SELECT c.code, c.title " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.code " +
                "WHERE e.student_id = ? AND e.status = 'enrolled'";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new RegisteredCourse(
                            rs.getString("code"),
                            rs.getString("title")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Updates the student's full name in the database.
    public boolean updateStudentName(int userId, String newName) {
        String sql = "UPDATE students SET full_name = ? WHERE user_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Deletes a student profile. Note: Uses an external connection to allow transaction management.
    public boolean deleteStudentProfile(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM students WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        }
    }
}