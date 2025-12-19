package edu.univ.erp.data;

import edu.univ.erp.domain.ClassStats;
import edu.univ.erp.domain.DetailedGradeRow;
import edu.univ.erp.domain.FinalGradeRow;
import edu.univ.erp.domain.GradebookRow; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Data Access Object for handling student grades and class statistics.
public class GradeDAO {

    // Deletes grades linked to a specific enrollment (used when dropping a course).
    public boolean deleteGradesForEnrollment(Connection conn, int enrollmentId) throws SQLException {
        String sql = "DELETE FROM grades WHERE enrollment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate(); 
            return true;
        }
    }

    // Deletes grades by enrollment ID (Standalone transaction version).
    public boolean deleteGradesByEnrollmentId(int enrollmentId) {
        String sql = "DELETE FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deletes all grades associated with a student (used when deleting a user).
    public boolean deleteGradesByStudentId(int studentId) {
        String sql = "DELETE FROM grades WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE student_id = ?)";
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
    
    // Fetches calculated Final Grades for a student's transcript.
    public List<FinalGradeRow> getFinalGrades(int studentId) {
        List<FinalGradeRow> grades = new ArrayList<>();
        String sql = "SELECT c.code, c.title, c.credits, g.final_grade " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.code " +
                     "WHERE e.student_id = ? AND g.final_grade IS NOT NULL " + 
                     "GROUP BY c.code, c.title, c.credits, g.final_grade";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new FinalGradeRow(
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getInt("credits"),
                            rs.getString("final_grade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    // Fetches detailed component scores (Quiz, Midsem, Endsem) for a student.
    // Uses Conditional Aggregation (MAX + CASE) to pivot rows into columns.
    public List<DetailedGradeRow> getComponentGrades(int studentId) {
        List<DetailedGradeRow> grades = new ArrayList<>();
        String sql = "SELECT " +
                     "  c.code, c.title, " +
                     "  MAX(CASE WHEN g.component = 'quiz' THEN g.score ELSE NULL END) AS quiz_score, " +
                     "  MAX(CASE WHEN g.component = 'midsem' THEN g.score ELSE NULL END) AS midsem_score, " +
                     "  MAX(CASE WHEN g.component = 'endsem' THEN g.score ELSE NULL END) AS endsem_score " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.code " +
                     "WHERE e.student_id = ? " +
                     "GROUP BY c.code, c.title";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new DetailedGradeRow(
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getObject("quiz_score", Double.class), 
                            rs.getObject("midsem_score", Double.class),
                            rs.getObject("endsem_score", Double.class)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    // Fetches raw marks for specific calculations.
    public Map<String, Object> getRawMarksAndEnrollmentId(int studentId, String courseCode) {
        String sql = "SELECT " +
                     "  e.enrollment_id, " +
                     "  MAX(CASE WHEN g.component = 'quiz' THEN g.score ELSE NULL END) AS quiz_score, " +
                     "  MAX(CASE WHEN g.component = 'midsem' THEN g.score ELSE NULL END) AS midsem_score, " +
                     "  MAX(CASE WHEN g.component = 'endsem' THEN g.score ELSE NULL END) AS endsem_score " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id " +
                     "WHERE e.student_id = ? AND s.course_id = ? " +
                     "GROUP BY e.enrollment_id";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, courseCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("enrollment_id", rs.getInt("enrollment_id"));
                    data.put("quiz_score", rs.getObject("quiz_score", Double.class));
                    data.put("midsem_score", rs.getObject("midsem_score", Double.class));
                    data.put("endsem_score", rs.getObject("endsem_score", Double.class));
                    return data;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Saves the computed final grade (A, B, C...) to the database.
    public boolean updateFinalGrade(int enrollmentId, String finalGrade) {
        String sql = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, finalGrade);
            stmt.setInt(2, enrollmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves the Gradebook view for an Instructor (list of students in a section with their scores).
    public List<GradebookRow> getGradebookForSection(int sectionId) {
        List<GradebookRow> gradebook = new ArrayList<>();
        String sql = "SELECT " +
                     "  s.user_id, e.enrollment_id, s.roll_no, s.full_name, " +
                     "  MAX(CASE WHEN g.component = 'quiz' THEN g.score ELSE NULL END) AS quiz_score, " +
                     "  MAX(CASE WHEN g.component = 'midsem' THEN g.score ELSE NULL END) AS midsem_score, " +
                     "  MAX(CASE WHEN g.component = 'endsem' THEN g.score ELSE NULL END) AS endsem_score " +
                     "FROM students s " +
                     "JOIN enrollments e ON s.user_id = e.student_id " +
                     "LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id " +
                     "WHERE e.section_id = ? AND e.status = 'enrolled' " +
                     "GROUP BY s.user_id, e.enrollment_id, s.roll_no, s.full_name " +
                     "ORDER BY s.roll_no";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    gradebook.add(new GradebookRow(
                            rs.getInt("user_id"),
                            rs.getInt("enrollment_id"),
                            rs.getString("roll_no"),
                            rs.getString("full_name"),
                            rs.getObject("quiz_score", Double.class),
                            rs.getObject("midsem_score", Double.class),
                            rs.getObject("endsem_score", Double.class)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gradebook;
    }

    // Inserts or Updates a grade score. Uses MySQL's "ON DUPLICATE KEY UPDATE" to handle both cases.
    public boolean saveOrUpdateGrade(int enrollmentId, String component, Double score) {
        String sql = "INSERT INTO grades (enrollment_id, component, score) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = VALUES(score)";
        
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            stmt.setString(2, component);
            
            if (score == null) {
                stmt.setNull(3, java.sql.Types.DOUBLE);
            } else {
                stmt.setDouble(3, score);
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Calculates Statistics (Avg, Min, Max) for a section.
    public List<ClassStats> getStatsForSection(int sectionId) {
        List<ClassStats> statsList = new ArrayList<>();
        // Calculates stats for each component (quiz, midsem, etc.) separately
        String sql = "SELECT " +
                "  g.component, " +
                "  AVG(g.score) AS average, " +
                "  MIN(g.score) AS min_score, " +
                "  MAX(g.score) AS max_score, " +
                "  COUNT(g.score) AS student_count " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "WHERE e.section_id = ? AND g.score IS NOT NULL " +
                "GROUP BY g.component"; 

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(new ClassStats(
                            rs.getString("component"),
                            rs.getDouble("average"),
                            rs.getDouble("min_score"),
                            rs.getDouble("max_score"),
                            rs.getInt("student_count")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statsList;
    }
}