package edu.univ.erp.service;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.CatalogItem;
import java.util.List;

public class CatalogService {

    private final CourseDAO courseDAO;

    public CatalogService() {
        this.courseDAO = new CourseDAO();
    }

    // Get all available course sections for registration
    public List<CatalogItem> getFullCatalog() {
        return courseDAO.getFullCatalog();
    }

    // Get basic course information (for course catalog view)
    public List<CatalogItem> getAllCourses() {
        return courseDAO.getAllCoursesSimple();
    }

    // Get courses that a student is currently registered in
    public List<CatalogItem> getRegisteredCatalogItems(int studentId) {
        return courseDAO.getRegisteredCatalogItems(studentId);
    }
}