package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.domain.AssignedSection;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// This class handles how the timetable data is organized and displayed
public class InstructorTimetableModel extends AbstractTableModel {

    // Column names for days of the week
    final String[] columnNames = {"TIME", "MON", "TUE", "WED", "THR", "FRI", "SAT"};

    // Time slots for the timetable
    private final String[] timeSlots = {
        "08:00 - 09:00",
        "09:00 - 10:00",
        "10:00 - 11:00",
        "11:00 - 12:00",
        "12:00 - 13:00", // Lunch break
        "13:00 - 14:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
        "16:00 - 17:00"
    };

    private String[][] timetableData; // 2D array to store timetable data

    // Map to convert day names to column numbers
    private final Map<String, Integer> dayToColumn;

    public InstructorTimetableModel(List<AssignedSection> assignedSections) {
        // Initialize empty timetable
        timetableData = new String[timeSlots.length][columnNames.length];
        
        // Fill time column with time slots
        for(int i = 0; i < timeSlots.length; i++) {
            timetableData[i][0] = timeSlots[i];
        }

        // Setup day to column mapping
        dayToColumn = new HashMap<>();
        dayToColumn.put("MON", 1);
        dayToColumn.put("TUE", 2);
        dayToColumn.put("WED", 3);
        dayToColumn.put("THR", 4);
        dayToColumn.put("FRI", 5);
        dayToColumn.put("SAT", 6);

        populateTimetable(assignedSections); // Fill with actual data
    }

    // Fill timetable with class information
    private void populateTimetable(List<AssignedSection> sections) {
        for (AssignedSection section : sections) {
            // Create label like "CS101 (Room 201)"
            String label = section.getCourseCode() + " (" + section.getRoom() + ")";
            String dayTime = section.getDayTime();

            try {
                // Parse day and time information
                String[] parts = dayTime.split(" ");
                String[] days = parts[0].split("/"); // Multiple days like "MON/WED"
                String[] times = parts[1].split("-"); // Time range like "10:00-11:00"
                
                // Get start hour (e.g., 10 from "10:00")
                int startHour = Integer.parseInt(times[0].split(":")[0]);

                // Calculate which row (time slot) this class belongs to
                int rowIndex = startHour - 8; // 8:00 is row 0, 9:00 is row 1, etc.
                
                // Place class in the correct time slot
                if (rowIndex >= 0 && rowIndex < timeSlots.length) {
                    for (String day : days) {
                        if (dayToColumn.containsKey(day.toUpperCase())) {
                            int colIndex = dayToColumn.get(day.toUpperCase());
                            timetableData[rowIndex][colIndex] = label;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not parse timetable for: " + dayTime);
            }
        }
    }

    @Override
    public int getRowCount() {
        return timeSlots.length; // Number of time slots
    }

    @Override
    public int getColumnCount() {
        return columnNames.length; // Days + time column
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col]; // Get column header
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return timetableData[rowIndex][columnIndex]; // Get cell value
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return String.class; // All cells contain strings
    }
}