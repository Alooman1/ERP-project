package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.util.CsvExporter; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter; // Import
import java.awt.event.ComponentEvent;   // Import
import java.util.ArrayList;
import java.util.List;

// This panel shows the instructor's teaching timetable
public class InstructorTimetablePanel extends JPanel {

    private final UserSession session;
    private final InstructorApi instructorApi;

    // Colors for the timetable
    private final Color TABLE_BG_COLOR = new Color(0, 0, 0, 0);
    private final Color HEADER_BG_COLOR = new Color(45, 45, 45);

    private JTable timetable;
    private InstructorTimetableModel tableModel;
    private JLabel titleLabel;

    public InstructorTimetablePanel(UserSession session) {
        this.session = session;
        this.instructorApi = new InstructorApi(session);

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));

        // Load timetable when this panel becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadTimetableData();
            }
        });

        // Title label
        titleLabel = new JLabel("ERP / TIMETABLE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Download button for CSV export
        JButton downloadButton = new JButton("Download");
        downloadButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        downloadButton.setBackground(new Color(70, 70, 70));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Header panel with title and download button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(downloadButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Create empty timetable table
        tableModel = new InstructorTimetableModel(List.of());
        timetable = new JTable(tableModel);
        styleTable(timetable);

        JScrollPane scrollPane = new JScrollPane(timetable);
        styleScrollPane(scrollPane);
        scrollPane.setPreferredSize(new Dimension(800, 317));
        
        add(scrollPane, BorderLayout.CENTER);

        downloadButton.addActionListener(e -> handleDownload());

        loadTimetableData(); // Load initial data
    }

    // Handle downloading timetable as CSV
    private void handleDownload() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Prepare data for CSV export
                String[] headers = tableModel.columnNames;
                List<String[]> data = new ArrayList<>();
                
                // Convert table data to CSV format
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    String[] row = new String[tableModel.getColumnCount()];
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        row[c] = (String) tableModel.getValueAt(r, c);
                    }
                    data.add(row);
                }
                
                // Create filename with username
                String filename = session.getUsername() + "_Timetable.csv";
                CsvExporter.exportToCsv(InstructorTimetablePanel.this, headers, data, filename);
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Wait for export to complete
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(InstructorTimetablePanel.this, "Error exporting timetable.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Style the timetable table
    private void styleTable(JTable table) {
        table.setOpaque(false);
        table.setBackground(TABLE_BG_COLOR); 
        table.setForeground(Color.WHITE);
        table.setShowGrid(true); 
        table.setGridColor(new Color(80, 80, 80)); // Grid lines
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(0, 120, 215)); // Selection color
        table.setSelectionForeground(Color.WHITE);

        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true); 
        header.setBackground(HEADER_BG_COLOR); 
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(100, 40)); // Header height
    }

    // Style the scroll pane
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); // Transparent viewport
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80))); // Border
    }

    // Load timetable data from server
    private void loadTimetableData() {
        SwingWorker<List<AssignedSection>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AssignedSection> doInBackground() throws Exception {
                // Get assigned sections from server
                return instructorApi.getAssignedSections(session.getUserId());
            }

            @Override
            protected void done() {
                try {
                    List<AssignedSection> sections = get();
                    // Create new table model with the data
                    tableModel = new InstructorTimetableModel(sections);
                    timetable.setModel(tableModel);
                    styleTable(timetable); // Re-apply styling
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(InstructorTimetablePanel.this, "Error loading timetable data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}