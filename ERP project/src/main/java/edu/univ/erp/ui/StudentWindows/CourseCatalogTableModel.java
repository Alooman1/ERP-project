package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.domain.CatalogItem;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CourseCatalogTableModel extends AbstractTableModel {

    // Only show basic course info (removed capacity and instructor)
    private final String[] columnNames = {"CODE", "TITLE", "CREDITS"};

    private final List<CatalogItem> catalogItems;

    public CourseCatalogTableModel(List<CatalogItem> catalogItems) {
        this.catalogItems = catalogItems;
    }

    @Override
    public int getRowCount() {
        return catalogItems.size();
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
        CatalogItem item = catalogItems.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return item.getCourseCode();
            case 1:
                return item.getCourseTitle();
            case 2:
                return item.getCredits();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c == 2) return Integer.class; // Credits column shows numbers
        return String.class;
    }
}