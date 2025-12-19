package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.GradebookRow;
import edu.univ.erp.domain.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GradebookPanel extends JPanel {

    private JTable table;
    private GradebookTableModel tableModel;
    private UserSession session;
    private AssignedSection currentSection;
    private JLabel titleLabel;

    // This panel shows the gradebook where teachers can enter scores
    public GradebookPanel(UserSession session, List<GradebookRow> gradebook) {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(60, 63, 65));
        this.session = session;
        tableModel = new GradebookTableModel(gradebook);
        table = new JTable(tableModel);

        applyDarkTheme(table);
        applyPadding(table);

        // Top panel with title and save button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        titleLabel = new JLabel("ERP / Gradebook");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Button to save all grade changes
        JButton saveButton = new JButton("Save All Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(80, 140, 80)); // Green
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> handleSaveAll());
        topPanel.add(saveButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(60, 63, 65)); // dark viewport

        add(scrollPane, BorderLayout.CENTER);
    }

    // Make the table look nice with dark theme
    private void applyDarkTheme(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(60, 63, 65));
        table.setShowGrid(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(43, 43, 43));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
    }

    // Add padding to table cells
    private void applyPadding(JTable table) {
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);
        left.setBorder(new EmptyBorder(0, 10, 0, 0));
        left.setForeground(Color.WHITE);
        left.setBackground(new Color(60, 63, 65));

        table.getColumnModel().getColumn(0).setCellRenderer(left);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(left);
    }

    // Load gradebook for a specific section
    public void loadSection(AssignedSection section) {
        if (section == null) return;
        this.currentSection = section;
        titleLabel.setText("ERP / " + section.getSubjectName() + " / Gradebook");

        try {
            InstructorApi api = new InstructorApi(session);

            List<GradebookRow> rows = api.getGradebook(section.getSectionId());
            if (rows == null)
                rows = java.util.Collections.emptyList();

            this.tableModel = new GradebookTableModel(rows);
            table.setModel(tableModel);

            applyDarkTheme(table);
            applyPadding(table);

            revalidate();
            repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load gradebook:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Save all grade changes to the server
    private void handleSaveAll() {
        if (currentSection == null) {
            JOptionPane.showMessageDialog(this, "No section loaded to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<GradebookRow> rowsToSave = tableModel.getAllGradebookRows();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                InstructorApi api = new InstructorApi(session);

                // Save each student's grades
                boolean allSuccess = true;
                for (GradebookRow row : rowsToSave) {
                    int enrollmentId = row.getEnrollmentId();
                    if (!api.saveGrade(enrollmentId, "quiz", row.getQuiz())) allSuccess = false;
                    if (!api.saveGrade(enrollmentId, "midsem", row.getMidsem())) allSuccess = false;
                    if (!api.saveGrade(enrollmentId, "endsem", row.getEndsem())) allSuccess = false;
                }
                return allSuccess;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(GradebookPanel.this,
                                "All changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(GradebookPanel.this,
                                "Warning: Some grades failed to save. This is likely due to security or maintenance mode.",
                                "Partial Failure", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GradebookPanel.this,
                            "Critical error during save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}