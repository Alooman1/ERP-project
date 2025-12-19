package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.domain.CatalogItem;
import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableModel extends AbstractTableModel {

    // Column names for days of the week
    private final String[] columnNames = {"TIME", "MON", "TUE", "WED", "THR", "FRI", "SAT"};

    // All available time slots for classes
    private final String[] timeSlots = {
        "08:00 - 09:00",
        "09:00 - 10:00",
        "10:00 - 11:00",
        "11:00 - 12:00",
        "12:00 - 13:00",
        "13:00 - 14:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
        "16:00 - 17:00"
    };
    
    // Stores the actual timetable data (course codes and rooms)
    private String[][] timetableData;
    // Maps day names to column numbers (MON=1, TUE=2, etc.)
    private final Map<String, Integer> dayToColumn;

    public TimetableModel(List<CatalogItem> registeredCourses) {
        // Create empty timetable grid
        timetableData = new String[timeSlots.length][columnNames.length];

        // Setup mapping from day names to column numbers
        dayToColumn = new HashMap<>();
        dayToColumn.put("MON", 1); dayToColumn.put("TUE", 2); dayToColumn.put("WED", 3);
        dayToColumn.put("THR", 4); dayToColumn.put("FRI", 5); dayToColumn.put("SAT", 6);

        // Fill first column with time slots
        for(int i = 0; i < timeSlots.length; i++) {
            timetableData[i][0] = timeSlots[i];
        }

        // Fill timetable with actual course data
        populateTimetable(registeredCourses);
    }

    // Fill the timetable with registered courses
    private void populateTimetable(List<CatalogItem> sections) {
        for (CatalogItem section : sections) {
            String timeStr = section.getDayTime(); 
            if (timeStr == null || timeStr.isEmpty()) continue;

            // Create display label like "CS101 (Room 201)"
            String label = section.getCourseCode() + " (" + section.getRoom() + ")";
            
            try {
                // Split "MON/WED 9:00-10:30" into days and time
                String[] parts = timeStr.split(" "); 
                String[] days = parts[0].split("/"); 
                String startTime = parts[1].split("-")[0];
                
                // Convert time like "9:00" to row number (9-8 = row 1)
                int startHour = Integer.parseInt(startTime.split(":")[0]);
                int rowIndex = startHour - 8;

                // Place course in correct timetable cell
                if (rowIndex >= 0 && rowIndex < timeSlots.length) {
                    for (String day : days) {
                        Integer colIndex = dayToColumn.get(day.toUpperCase());
                        if (colIndex != null) {
                            if (timetableData[rowIndex][colIndex] == null) {
                                timetableData[rowIndex][colIndex] = label;
                            } else {
                                // If cell already has a course, add this one with slash
                                timetableData[rowIndex][colIndex] += " / " + label; 
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not parse time: " + timeStr);
            }
        }
    }
    
    // Export timetable as text for downloading
    public String exportAsText() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                sb.append(getValueAt(r, c)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public int getRowCount() {
        return timeSlots.length;
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
        return timetableData[rowIndex][columnIndex];
    }
    
    @Override
    public Class<?> getColumnClass(int c) {
        return String.class;
    }
}