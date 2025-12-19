package edu.univ.erp.data;

import edu.univ.erp.domain.CatalogItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Data Access Object for handling Courses and Sections in the ERP Database.
public class CourseDAO {

    // Retrieves all sections, including deadline info and calculated section numbers.
    // Uses ROW_NUMBER() to dynamically assign section numbers (1, 2...) for each course.
    // REMOVED "WHERE s.year = 2025" to allow all created courses to show
    public List<CatalogItem> getFullCatalog() {
        List<CatalogItem> catalog = new ArrayList<>();

        String sql = "SELECT " +
                "  s.section_id, c.code, c.title, c.credits, " +
                "  COALESCE(i.full_name, 'TBA') AS instructor_name, " +
                "  s.day_time, s.room, s.capacity, s.reg_deadline, s.drop_deadline, " +
                "  (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) AS enrolled " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                "ORDER BY c.code, s.section_id";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CatalogItem item = new CatalogItem(
                        rs.getInt("section_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("instructor_name"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled"),
                        rs.getString("reg_deadline"),
                        rs.getString("drop_deadline")
                );
                catalog.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalog;
    }

    // Fetches the specific sections a student has registered for.
    public List<CatalogItem> getRegisteredCatalogItems(int studentId) {
        List<CatalogItem> catalog = new ArrayList<>();
        String sql = "SELECT " +
                "  s.section_id, c.code, c.title, c.credits, " +
                "  COALESCE(i.full_name, 'TBA') AS instructor_name, " +
                "  s.day_time, s.room, s.capacity, s.reg_deadline, s.drop_deadline, " +
                "  (SELECT COUNT(*) FROM enrollments e_count WHERE e_count.section_id = s.section_id) AS enrolled " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "JOIN enrollments e ON s.section_id = e.section_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                "WHERE e.student_id = ? AND e.status = 'enrolled'";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CatalogItem item = new CatalogItem(
                            rs.getInt("section_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getInt("credits"),
                            rs.getString("instructor_name"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getInt("capacity"),
                            rs.getInt("enrolled"),
                            rs.getString("reg_deadline"),
                            rs.getString("drop_deadline")
                    );
                    catalog.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalog;
    }

    // --- Basic Course Operations ---

    // Creates a new course in the database.
    public boolean createCourse(String code, String title, int credits) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updates an existing course's details.
    public boolean updateCourse(String code, String newTitle, int newCredits) {
        String sql = "UPDATE courses SET title = ?, credits = ? WHERE code = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newTitle);
            stmt.setInt(2, newCredits);
            stmt.setString(3, code);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetches simplified course list (no section details) for Admin Course dropdowns.
    public List<CatalogItem> getAllCoursesSimple() {
        List<CatalogItem> courses = new ArrayList<>();
        String sql = "SELECT code, title, credits FROM courses ORDER BY code";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new CatalogItem(
                        0, rs.getString("code"), rs.getString("title"), rs.getInt("credits"),
                        "", "", "", 0, 0, null, null
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return courses;
    }

    // --- Section Operations ---

    // Creates a new section, optionally with Registration/Drop deadlines.
    public boolean createSection(String courseCode, int capacity, String dayTime,
                                 String room, String semester, int year,
                                 String regDeadline, String dropDeadline) { // UPDATED
        String sql = "INSERT INTO sections (course_id, capacity, day_time, room, semester, year, reg_deadline, drop_deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseCode);
            stmt.setInt(2, capacity);
            stmt.setString(3, dayTime);
            stmt.setString(4, room);
            stmt.setString(5, semester);
            stmt.setInt(6, year);

            // Handle Dates
            if (regDeadline == null || regDeadline.trim().isEmpty()) stmt.setNull(7, java.sql.Types.DATE);
            else stmt.setString(7, regDeadline); // Ensure format YYYY-MM-DD

            if (dropDeadline == null || dropDeadline.trim().isEmpty()) stmt.setNull(8, java.sql.Types.DATE);
            else stmt.setString(8, dropDeadline);

            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deletes a section (only if no students are enrolled).
    public boolean deleteSection(int sectionId) {
        // First check for enrollments
        String checkSql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
        String deleteSql = "DELETE FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnectionManager.getErpConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sectionId);
                try(ResultSet rs = checkStmt.executeQuery()) {
                    if(rs.next() && rs.getInt(1) > 0) return false; // Has enrollments, cannot delete
                }
            }

            try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                delStmt.setInt(1, sectionId);
                return delStmt.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper to get raw deadlines for logic checks
    public Map<String, java.sql.Date> getSectionDeadlines(int sectionId) {
        String sql = "SELECT reg_deadline, drop_deadline FROM sections WHERE section_id = ?";
        Map<String, java.sql.Date> deadlines = new HashMap<>();
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    deadlines.put("reg", rs.getDate("reg_deadline"));
                    deadlines.put("drop", rs.getDate("drop_deadline"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return deadlines;
    }

    // Returns the Course Code associated with a Section ID (useful for duplicate checks).
    public String getCourseCodeForSection(int sectionId) {
        String sql = "SELECT course_id FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) return rs.getString("course_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Deletes all sections for a course (used when deleting a course).
    public boolean deleteSectionsByCourse(Connection conn, String courseCode) throws SQLException {
        String sql = "DELETE FROM sections WHERE course_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            stmt.executeUpdate();
            return true;
        }
    }

    // Deletes a course from the database.
    public boolean deleteCourse(Connection conn, String courseCode) throws SQLException {
        String sql = "DELETE FROM courses WHERE code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            return stmt.executeUpdate() == 1;
        }
    }

    // Fetches all sections with details for Admin management.
    public List<CatalogItem> getAllSectionsDetailed() {
        List<CatalogItem> sections = new ArrayList<>();
        String sql = "SELECT " +
                "  s.section_id, c.code, c.title, s.day_time, s.room, s.capacity, s.reg_deadline, s.drop_deadline, " +
                "  COALESCE(i.full_name, 'Not Assigned') AS instructor_name " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                "ORDER BY c.code, s.section_id";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sections.add(new CatalogItem(
                        rs.getInt("section_id"), rs.getString("code"), rs.getString("title"), 0,
                        rs.getString("instructor_name"), rs.getString("day_time"), rs.getString("room"),
                        rs.getInt("capacity"), 0, rs.getString("reg_deadline"), rs.getString("drop_deadline")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sections;
    }

    // --- Utility Methods ---
    
    // Assigns an instructor to a section.
    public boolean assignInstructorToSection(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gets the time string for a section (for conflict checking).
    public String getSectionTime(int sectionId) {
        String sql = "SELECT day_time FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("day_time");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    // Retrieves basic course details (Title, Credits) by Course Code.
    public Map<String, Object> getCourseDetailsByCode(String courseCode) {
        String sql = "SELECT title, credits FROM courses WHERE code = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> details = new HashMap<>();
                    details.put("title", rs.getString("title"));
                    details.put("credits", rs.getInt("credits"));
                    return details;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Finds sections matching specific room and semester (to prevent double booking rooms).
    public List<CatalogItem> getSectionsByRoomAndTerm(String room, String semester, int year) {
        List<CatalogItem> list = new ArrayList<>();
        String sql = "SELECT s.section_id, c.code, c.title, s.day_time, s.room " +
                "FROM sections s JOIN courses c ON s.course_id = c.code " +
                "WHERE s.room = ? AND s.semester = ? AND s.year = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room);
            stmt.setString(2, semester);
            stmt.setInt(3, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new CatalogItem(
                            rs.getInt("section_id"), rs.getString("code"), rs.getString("title"), 0, "",
                            rs.getString("day_time"), rs.getString("room"), 0, 0, null, null
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Gets semester and year for a section.
    public Map<String, Object> getSectionSemesterYear(int sectionId) {
        String sql = "SELECT semester, year FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("semester", rs.getString("semester"));
                    map.put("year", rs.getInt("year"));
                    return map;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Gets the Instructor ID assigned to a section.
    public Integer getSectionInstructorId(int sectionId) {
        String sql = "SELECT instructor_id FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("instructor_id");
                    return rs.wasNull() ? null : id;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Updates section schedule.
    public boolean updateSectionSchedule(int sectionId, String dayTime, String room) {
        String sql = "UPDATE sections SET day_time = ?, room = ? WHERE section_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dayTime);
            stmt.setString(2, room);
            stmt.setInt(3, sectionId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}