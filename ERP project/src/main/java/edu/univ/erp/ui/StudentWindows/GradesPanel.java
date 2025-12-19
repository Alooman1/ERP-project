package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.DetailedGradeRow;
import edu.univ.erp.domain.FinalGradeRow;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.GradeService; 
import edu.univ.erp.util.CsvExporter; // For exporting grades to CSV

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GradesPanel extends JPanel {

    private final UserSession session;
    private final StudentApi studentApi;
    private final GradeService gradeService; 

    // Tables for displaying grades
    private JTable finalGradesTable;
    private FinalGradesTableModel finalGradesModel;
    private JScrollPane finalGradesScrollPane;

    private JTable detailedGradesTable;
    private DetailedGradesTableModel detailedGradesModel;
    private JScrollPane detailedGradesScrollPane;

    // Color scheme for dark theme
    private final Color BOX_BG_COLOR = new Color(50, 50, 50, 200);
    private final Color TABLE_BG_COLOR = new Color(0, 0, 0, 0);
    private final Color HEADER_BG_COLOR = new Color(45, 45, 45);

    private JPanel mainContent;

    public GradesPanel(UserSession session) {
        this.session = session;
        this.studentApi = new StudentApi();
        this.gradeService = new GradeService();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Main content area with collapsible sections
        mainContent = new JPanel();
        mainContent.setOpaque(false);
        mainContent.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 15, 0);
        

        // Title label
        JLabel titleLabel = new JLabel("GRADES");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContent.add(titleLabel, gbc);

        // Create collapsible sections for grades
        gbc.gridy = 1;
        JPanel finalResultTablePanel = createFinalGradesTable();
        finalResultTablePanel.setVisible(false); // Start collapsed
        mainContent.add(createCollapsibleSection("View Final Result", finalResultTablePanel), gbc);

        gbc.gridy = 2;
        JPanel currentMarksTablePanel = createCurrentMarksTable();
        currentMarksTablePanel.setVisible(false); // Start collapsed
        mainContent.add(createCollapsibleSection("Current Marks", currentMarksTablePanel), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainContent.add(Box.createVerticalGlue(), gbc);

        // Make content scrollable
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Download transcript button
        JButton downloadButton = new JButton("Download Transcript");
        downloadButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        downloadButton.setOpaque(true);
        downloadButton.setBackground(new Color(80, 80, 80));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(downloadButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        downloadButton.addActionListener(e -> {
            handleDownloadTranscript();
        });

        // Load grades when panel is created
        loadGradesData();
    }

    // Create collapsible section with header
    private JPanel createCollapsibleSection(String title, JPanel contentPanelToToggle) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setOpaque(false);
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));

        // Rounded header panel that can be clicked
        RoundedPanel headerPanel = new RoundedPanel(BOX_BG_COLOR, 30);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.setBorder(new LineBorder(new Color(90, 90, 90, 150), 1, true));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(14, 25, 14, 25));

        // Arrow that changes direction when expanded/collapsed
        JLabel arrowLabel = new JLabel("\u2304");
        arrowLabel.setForeground(Color.WHITE);
        arrowLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        arrowLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        arrowLabel.setBorder(new EmptyBorder(14, 25, 14, 25));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(arrowLabel, BorderLayout.EAST);

        // Toggle visibility when header is clicked
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = !contentPanelToToggle.isVisible();
                contentPanelToToggle.setVisible(isVisible);
                arrowLabel.setText(isVisible ? "\u2303" : "\u2304"); // Up/down arrow

                mainContent.revalidate();
                mainContent.repaint();
            }
        });

        JPanel centeredWrapper = new JPanel();
        centeredWrapper.setOpaque(false);
        centeredWrapper.setLayout(new BoxLayout(centeredWrapper, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanelToToggle.setAlignmentX(Component.CENTER_ALIGNMENT);

        int preferredWidth = 850;
        headerPanel.setMaximumSize(new Dimension(preferredWidth, 70));
        contentPanelToToggle.setMaximumSize(new Dimension(preferredWidth, Integer.MAX_VALUE));

        centeredWrapper.add(headerPanel);
        centeredWrapper.add(Box.createVerticalStrut(6));
        centeredWrapper.add(contentPanelToToggle);

        sectionPanel.add(centeredWrapper);

        return sectionPanel;
    }

    // Create table for final grades
    private JPanel createFinalGradesTable() {
        finalGradesModel = new FinalGradesTableModel(List.of());
        finalGradesTable = new JTable(finalGradesModel);
        styleTable(finalGradesTable); 

        finalGradesScrollPane = new JScrollPane(finalGradesTable);
        styleScrollPane(finalGradesScrollPane); 

        finalGradesScrollPane.setPreferredSize(new Dimension(600, 200)); 
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 10, 10, 10)); 
        tablePanel.add(finalGradesScrollPane, BorderLayout.CENTER);

        // Download button for final grades
        JButton downloadFinalButton = new JButton("Download Final Grade Report"); 
        setupSmallDownloadButton(downloadFinalButton);
        
        JPanel finalButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        finalButtonPanel.setOpaque(false);
        finalButtonPanel.add(downloadFinalButton);
        tablePanel.add(finalButtonPanel, BorderLayout.SOUTH);
        
        downloadFinalButton.addActionListener(e -> {
            handleDownloadTranscript();
        });

        return tablePanel;
    }

    // Create table for current marks (detailed grades)
    private JPanel createCurrentMarksTable() {
        detailedGradesModel = new DetailedGradesTableModel(List.of());
        detailedGradesTable = new JTable(detailedGradesModel);
        styleTable(detailedGradesTable);
        
        detailedGradesScrollPane = new JScrollPane(detailedGradesTable);
        styleScrollPane(detailedGradesScrollPane); 
        
        detailedGradesScrollPane.setPreferredSize(new Dimension(600, 200));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 10, 10, 10)); 
        tablePanel.add(detailedGradesScrollPane, BorderLayout.CENTER);

        // Download button for current marks
        JButton downloadCurrentButton = new JButton("Download Current Marks");
        setupSmallDownloadButton(downloadCurrentButton);

        JPanel currentButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        currentButtonPanel.setOpaque(false);
        currentButtonPanel.add(downloadCurrentButton);
        tablePanel.add(currentButtonPanel, BorderLayout.SOUTH);
        
        downloadCurrentButton.addActionListener(e -> {
            handleDownloadCurrentMarks();
        });

        return tablePanel;
    }

    // Style small download buttons
    private void setupSmallDownloadButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Apply dark theme styling to tables
    private void styleTable(JTable table) {
        table.setOpaque(false);
        table.setBackground(TABLE_BG_COLOR); 
        table.setForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setGridColor(new Color(0, 0, 0, 0)); 
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(60, 100, 140));
        table.setSelectionForeground(Color.WHITE);

        // Style table cells
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setOpaque(false);
        leftRenderer.setForeground(Color.WHITE);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 10)); 

        table.setDefaultRenderer(Object.class, leftRenderer);
        table.setDefaultRenderer(Integer.class, leftRenderer);
        table.setDefaultRenderer(Double.class, leftRenderer);

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true); 
        header.setBackground(HEADER_BG_COLOR); 
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(100, 40));
    }

    // Style scroll panes
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    // Helper class to hold both types of grade data
    private static class GradesData {
        final List<FinalGradeRow> finalGrades;
        final List<DetailedGradeRow> detailedGrades;
        GradesData(List<FinalGradeRow> finalGrades, List<DetailedGradeRow> detailedGrades) {
            this.finalGrades = finalGrades;
            this.detailedGrades = detailedGrades;
        }
    }

    // Load grades data from server
    private void loadGradesData() {
        SwingWorker<GradesData, Void> worker = new SwingWorker<>() {
            @Override
            protected GradesData doInBackground() throws Exception {
                // Get detailed component grades (quiz, midsem, endsem)
                List<DetailedGradeRow> detailedGrades = studentApi.getComponentGrades(session.getUserId());

                // Get calculated final grades
                List<FinalGradeRow> finalGrades = gradeService.getCalculatedFinalGradesForDisplay(session.getUserId());
                
                return new GradesData(finalGrades, detailedGrades);
            }

            @Override
            protected void done() {
                try {
                    GradesData data = get();

                    // Update final grades table
                    finalGradesModel = new FinalGradesTableModel(data.finalGrades);
                    finalGradesTable.setModel(finalGradesModel);

                    // Update detailed grades table
                    detailedGradesModel = new DetailedGradesTableModel(data.detailedGrades);
                    detailedGradesTable.setModel(detailedGradesModel);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GradesPanel.this, 
                                "Error loading grades: " + e.getMessage(), 
                                "Data Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Custom panel with rounded corners
    class RoundedPanel extends JPanel {
        private final Color backgroundColor;
        private final int cornerRadius;

        public RoundedPanel(Color bgColor, int radius) {
            this.backgroundColor = bgColor;
            this.cornerRadius = radius;
            setOpaque(false); // IMPORTANT
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();
        }
    }

    // Download final transcript as CSV
    private void handleDownloadTranscript() {
        SwingWorker<List<FinalGradeRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FinalGradeRow> doInBackground() throws Exception {
                // Get final grades for download
                return gradeService.getCalculatedFinalGradesForDisplay(session.getUserId()); 
            }

            @Override
            protected void done() {
                try {
                    List<FinalGradeRow> finalGrades = get();
                    if (finalGrades == null || finalGrades.isEmpty()) {
                        JOptionPane.showMessageDialog(GradesPanel.this, "No final grades to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Prepare CSV data
                    String[] headers = {"Course Code", "Course Title", "Credits", "Grade"};
                    List<String[]> data = new ArrayList<>();
                    for (FinalGradeRow row : finalGrades) {
                        data.add(new String[]{
                                row.getCourseCode(),
                                row.getCourseTitle(),
                                String.valueOf(row.getCredits()),
                                row.getFinalGrade()
                        });
                    }
                    
                    String defaultFileName = session.getUsername() + "_Transcript.csv";
                    CsvExporter.exportToCsv(GradesPanel.this, headers, data, defaultFileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GradesPanel.this, "Error fetching transcript data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Download current marks as CSV
    private void handleDownloadCurrentMarks() {
        SwingWorker<List<DetailedGradeRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<DetailedGradeRow> doInBackground() throws Exception {
                return studentApi.getComponentGrades(session.getUserId());
            }

            @Override
            protected void done() {
                try {
                    List<DetailedGradeRow> detailedGrades = get();
                    if (detailedGrades == null || detailedGrades.isEmpty()) {
                        JOptionPane.showMessageDialog(GradesPanel.this, "No current marks to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Prepare CSV data for current marks
                    String[] headers = {"Course Code", "Course Title", "Quiz (20)", "Midsem (30)", "Endsem (50)"};
                    List<String[]> data = new ArrayList<>();
                    
                    for (DetailedGradeRow row : detailedGrades) {
                        data.add(new String[]{
                                row.getCourseCode(),
                                row.getCourseTitle(),
                                (row.getQuiz() != null) ? String.valueOf(row.getQuiz()) : null,
                                (row.getMidsem() != null) ? String.valueOf(row.getMidsem()) : null,
                                (row.getEndsem() != null) ? String.valueOf(row.getEndsem()) : null
                        });
                    }
                    
                    String defaultFileName = session.getUsername() + "_CurrentMarks.csv";
                    CsvExporter.exportToCsv(GradesPanel.this, headers, data, defaultFileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GradesPanel.this, "Error fetching marks data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}