package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.domain.DetailedGradeRow;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class DetailedGradesTableModel extends AbstractTableModel {

    // Columns for detailed grade breakdown
    private final String[] columnNames = {"CODE", "TITLE", "QUIZ (20)", "MIDSEM (30)", "ENDSEM (50)"};
    private final List<DetailedGradeRow> detailedGrades;

    public DetailedGradesTableModel(List<DetailedGradeRow> detailedGrades) {
        this.detailedGrades = detailedGrades;
    }

    @Override
    public int getRowCount() {
        return detailedGrades.size();
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
        DetailedGradeRow row = detailedGrades.get(rowIndex);
        switch (columnIndex) {
            case 0: return row.getCourseCode();
            case 1: return row.getCourseTitle();
            case 2: return row.getQuiz();
            case 3: return row.getMidsem();
            case 4: return row.getEndsem();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c > 1) {
            return Double.class; // Grade columns show decimal numbers
        }
        return String.class;
    }
}