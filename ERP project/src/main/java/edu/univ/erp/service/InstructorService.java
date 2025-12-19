package edu.univ.erp.service;

import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.Instructor;
import java.util.List;

public class InstructorService {

    private final InstructorDAO instructorDAO;

    public InstructorService() {
        this.instructorDAO = new InstructorDAO();
    }
    public InstructorService(InstructorDAO instructorDAO) {
        this.instructorDAO = instructorDAO;
    }

    // Get instructor's own profile information
    public Instructor getInstructorProfile(int userId) {
        return instructorDAO.getInstructorProfile(userId);
    }
    
    // Get all sections assigned to an instructor
    public List<AssignedSection> getAssignedSections(int instructorId) {
        return instructorDAO.getAssignedSections(instructorId);
    }

    // Get list of all instructors in the system
    public List<edu.univ.erp.domain.Instructor> getAllInstructors() {
        return instructorDAO.getAllInstructors();
    }

    // Update instructor's name
    public boolean updateInstructorName(int userId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false; // Don't allow empty names
        }
        return instructorDAO.updateInstructorName(userId, newName);
    }
}