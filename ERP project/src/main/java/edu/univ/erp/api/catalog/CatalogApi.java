package edu.univ.erp.api.catalog;

import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.service.CatalogService;
import java.util.List;

public class CatalogApi {

    private final CatalogService catalogService;

    public CatalogApi() {
        // Set up the catalog service
        this.catalogService = new CatalogService();
    }

    // Get all available sections that students can register for
    public List<CatalogItem> getCatalog() {
        return catalogService.getFullCatalog();
    }

    // Get just the course list (without sections)
    public List<CatalogItem> getAllCourses() {
        return catalogService.getAllCourses();
    }

    // Get the classes a specific student is already registered in
    public List<CatalogItem> getRegisteredCatalogItems(int studentId) {
        return catalogService.getRegisteredCatalogItems(studentId);
    }
}