package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.domain.CatalogItem;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class RegisteredCoursesModel extends AbstractTableModel {

    // Columns for registered courses table
    private final String[] columnNames = {"CODE", "TITLE", "CREDIT", "CAPACITY", "INSTRUCTOR"};
    private final List<CatalogItem> registeredItems;

    public RegisteredCoursesModel(List<CatalogItem> registeredItems) {
        this.registeredItems = registeredItems;
    }

    // Get course item from specific row
    public CatalogItem getItemAt(int rowIndex) {
        return registeredItems.get(rowIndex);
    }
    
    @Override
    public int getRowCount() {
        return registeredItems.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CatalogItem item = registeredItems.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getCourseCode();
            case 1: return item.getCourseTitle();
            case 2: return item.getCredits();
            case 3: return item.getAvailability();
            case 4: return item.getInstructorName();
            default: return null;
        }
    }
}