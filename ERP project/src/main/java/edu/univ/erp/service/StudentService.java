package edu.univ.erp.service;

import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.DBConnectionManager;
import edu.univ.erp.domain.RegisteredCourse;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.util.TimeUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }
    public StudentService(StudentDAO studentDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.enrollmentDAO = enrollmentDAO;
    }

    // Get student's own profile information
    public Student getStudentProfile(int userId) {
        return studentDAO.getStudentProfile(userId);
    }

    // Update student's name
    public boolean updateStudentName(int userId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false; 
        }
        return studentDAO.updateStudentName(userId, newName);
    }

    // Get courses the student is registered in
    public List<RegisteredCourse> getRegisteredCourses(int userId) {
        return studentDAO.getRegisteredCourses(userId);
    }

    // Register for a course (with time conflict checking)
    public String registerCourse(int studentId, int sectionId) {
        // Get the section time to check for conflicts
        String newSectionTime = courseDAO.getSectionTime(sectionId);
        
        if (newSectionTime != null && !newSectionTime.isEmpty()) {
            List<CatalogItem> currentSchedule = courseDAO.getRegisteredCatalogItems(studentId);

            // Check if new section conflicts with existing schedule
            for (CatalogItem item : currentSchedule) {
                if (TimeUtil.checkOverlap(newSectionTime, item.getDayTime())) {
                    return "Conflict! You have class " + item.getCourseCode() + 
                           " at " + item.getDayTime() + ". Please drop it first.";
                }
            }
        }

        // Register the student
        try (Connection conn = DBConnectionManager.getErpConnection()) {
            boolean success = enrollmentDAO.registerStudent(conn, studentId, sectionId);
            
            if (success) {
                return "Success: Registered for course.";
            } else {
                return "Error: Registration failed (Class full or already enrolled).";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Database error during registration.";
        }
    }

    // Drop a course
    public String dropCourse(int studentId, int sectionId) {
        try (Connection conn = DBConnectionManager.getErpConnection()) {
            boolean success = enrollmentDAO.dropStudent(conn, studentId, sectionId);
            return success ? "Success: Course dropped." : "Error: Could not drop course.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Database error during drop.";
        }
    }
}