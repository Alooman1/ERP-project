package edu.univ.erp.util;

import com.opencsv.CSVWriter;
import javax.swing.*;
import java.awt.Component; 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    // Export data to CSV file with user choosing where to save
    public static void exportToCsv(Component parentComponent, String[] headers, List<String[]> data, String defaultFileName) {
        // Show file chooser dialog for user to pick save location
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript As...");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(parentComponent);

        // If user clicked OK/Save button
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Make sure file ends with .csv extension
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            // Write data to CSV file
            try (FileWriter fw = new FileWriter(fileToSave);
                 CSVWriter writer = new CSVWriter(fw)) {

                // Write column headers first
                writer.writeNext(headers);
                // Write all the data rows
                writer.writeAll(data);

                // Show success message to user
                JOptionPane.showMessageDialog(
                        parentComponent,
                        "Transcript saved successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException e) {
                e.printStackTrace();
                // Show error message if saving fails
                JOptionPane.showMessageDialog(
                        parentComponent,
                        "Error saving file: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}