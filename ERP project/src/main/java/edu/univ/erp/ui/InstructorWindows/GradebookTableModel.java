package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.domain.GradebookRow;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

// This class handles how the gradebook table looks and behaves
public class GradebookTableModel extends AbstractTableModel {

    // Column names for the gradebook table
    private final String[] columns = {
            "S.N.",
            "Name",
            "Quiz (20)",
            "Midsem (30)",
            "Endsem (50)"
    };

    private List<GradebookRow> gradebook;

    public GradebookTableModel(List<GradebookRow> gradebook) {
        this.gradebook = (gradebook == null) ? java.util.Collections.emptyList() : gradebook;
    }

    public List<GradebookRow> getAllGradebookRows() {
        return gradebook;
    }

    @Override
    public int getRowCount() {
        return gradebook.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GradebookRow row = gradebook.get(rowIndex);

        // Get data for each column
        switch (columnIndex) {
            case 0: return rowIndex + 1;  // Serial number
            case 1: return row.getStudentName();  // Student name
            case 2: return row.getQuiz();  // Quiz score
            case 3: return row.getMidsem();  // Midsem score
            case 4: return row.getEndsem();  // Endsem score
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int col) {
        return col >= 2; // Only quiz, midsem, endsem columns can be edited
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int col) {
        GradebookRow row = gradebook.get(rowIndex);
        String component = "";
        double maxScore = 0;

        try {
            // Handle empty values (clear the score)
            if (aValue == null || aValue.toString().trim().isEmpty()) {
                switch (col) {
                    case 2: row.setQuiz(null); break;
                    case 3: row.setMidsem(null); break;
                    case 4: row.setEndsem(null); break;
                }
                fireTableCellUpdated(rowIndex, col);
                return;
            }
            double val = Double.parseDouble(aValue.toString());

            // Set limits for each type of exam
            switch (col) {
                case 2: component = "Quiz"; maxScore = 20; break;
                case 3: component = "Midsem"; maxScore = 30; break;
                case 4: component = "Endsem"; maxScore = 50; break;
            }

            // Validate the score
            if (val < 0) {
                JOptionPane.showMessageDialog(null, component + " score cannot be negative!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (val > maxScore) {
                JOptionPane.showMessageDialog(null, component + " score cannot exceed " + maxScore + "!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the score in the data
            switch (col) {
                case 2: row.setQuiz(val); break;
                case 3: row.setMidsem(val); break;
                case 4: row.setEndsem(val); break;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid score. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            fireTableCellUpdated(rowIndex, col);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        fireTableCellUpdated(rowIndex, col);
    }
}