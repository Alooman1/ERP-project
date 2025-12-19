package edu.univ.erp.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtil {

    // Format for time like "9:30" or "14:45"
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    // Check if two time slots overlap (like for class schedules)
    public static boolean checkOverlap(String timeSlot1, String timeSlot2) {
        // Skip check if either time slot is empty
        if (timeSlot1 == null || timeSlot2 == null) return false;
        if (timeSlot1.isEmpty() || timeSlot2.isEmpty()) return false;

        try {
            // Split "Monday 9:00-10:30" into day and time parts
            String[] parts1 = timeSlot1.split(" ");
            String[] parts2 = timeSlot2.split(" ");

            if (parts1.length < 2 || parts2.length < 2) return false;

            String day1 = parts1[0];
            String day2 = parts2[0];

            // If days are different, no overlap possible
            if (!day1.equalsIgnoreCase(day2)) {
                return false;
            }

            // Split time range like "9:00-10:30" into start and end times
            String[] range1 = parts1[1].split("-");
            String[] range2 = parts2[1].split("-");

            if (range1.length < 2 || range2.length < 2) return false;

            // Convert time strings to actual time objects
            LocalTime start1 = LocalTime.parse(range1[0], TIME_FMT);
            LocalTime end1   = LocalTime.parse(range1[1], TIME_FMT);
            LocalTime start2 = LocalTime.parse(range2[0], TIME_FMT);
            LocalTime end2   = LocalTime.parse(range2[1], TIME_FMT);

            // Check if time ranges overlap
            return start1.isBefore(end2) && start2.isBefore(end1);

        } catch (Exception e) {
            // Log error if time parsing fails
            System.err.println("Time overlap check failed for input: " + timeSlot1 + " vs " + timeSlot2);
            return false;
        }
    }
}