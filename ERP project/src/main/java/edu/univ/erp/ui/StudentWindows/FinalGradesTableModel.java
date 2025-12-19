package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.domain.FinalGradeRow;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FinalGradesTableModel extends AbstractTableModel {

    // Columns for final grade summary
    private final String[] columnNames = {"CODE", "TITLE", "CREDIT", "GRADE"};
    private final List<FinalGradeRow> finalGrades;

    public FinalGradesTableModel(List<FinalGradeRow> finalGrades) {
        this.finalGrades = finalGrades;
    }

    @Override
    public int getRowCount() {
        return finalGrades.size();
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
        FinalGradeRow row = finalGrades.get(rowIndex);
        switch (columnIndex) {
            case 0: return row.getCourseCode();
            case 1: return row.getCourseTitle();
            case 2: return row.getCredits();
            case 3: return row.getFinalGrade();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c == 2) {
            return Integer.class; // Credits column shows whole numbers
        }
        return String.class;
    }
}