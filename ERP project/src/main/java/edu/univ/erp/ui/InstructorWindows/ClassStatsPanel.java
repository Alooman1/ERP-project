package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.ClassStats;
import edu.univ.erp.domain.UserSession;
// Import CsvExporter
import edu.univ.erp.util.CsvExporter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClassStatsPanel extends JPanel {

    private final UserSession session;
    private AssignedSection section;
    private final InstructorApi instructorApi;
    private List<ClassStats> currentStats; // Store stats for download

    private JLabel titleLabel;
    private JLabel quizStatsLabel;
    private JLabel midsemStatsLabel;
    private JLabel endsemStatsLabel;

    // This panel shows class statistics like average scores
    public ClassStatsPanel(UserSession session, AssignedSection section) {
        this.session = session;
        this.instructorApi = new InstructorApi(session);
        this.currentStats = new ArrayList<>();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40)); // Add padding

        // Create the main title
        titleLabel = new JLabel("ERP / Class Stats");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36)); // Larger title
        titleLabel.setForeground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Main panel for showing stats
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Add sections for different types of exams
        mainPanel.add(createStatHeader("Quiz"));
        quizStatsLabel = createStatLabel("Loading...");
        mainPanel.add(quizStatsLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        mainPanel.add(createStatHeader("Midsem"));
        midsemStatsLabel = createStatLabel("Loading...");
        mainPanel.add(midsemStatsLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        mainPanel.add(createStatHeader("Endsem"));
        endsemStatsLabel = createStatLabel("Loading...");
        mainPanel.add(endsemStatsLabel);

        mainPanel.add(Box.createVerticalGlue());
        add(mainPanel, BorderLayout.CENTER);

        // Button to download stats as CSV
        JButton downloadButton = new JButton("Download Stats");
        downloadButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        downloadButton.setOpaque(true);
        downloadButton.setBackground(new Color(80, 80, 80));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(downloadButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        downloadButton.addActionListener(e -> handleDownload());

        if (section != null) {
            loadSection(section);
        }
    }

    // Load data for a specific section
    public void loadSection(AssignedSection section) {
        if (section == null) return;
        
        this.section = section;
        titleLabel.setText("ERP / " + section.getSubjectName() + " / Class Stats");

        // Reset labels while loading
        quizStatsLabel.setText("Loading...");
        midsemStatsLabel.setText("Loading...");
        endsemStatsLabel.setText("Loading...");

        loadStatsData();
    }
    
    // Create header for each stat section
    private JLabel createStatHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        return label;
    }
    
    // Create label for showing statistics
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(Color.LIGHT_GRAY);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    // Handle downloading stats as CSV file
    private void handleDownload() {
        if (currentStats == null || currentStats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No stats to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Prepare data for CSV export
        String[] headers = {"Component", "Average", "Highest", "Lowest", "Student Count"};
        List<String[]> data = new ArrayList<>();
        
        for (ClassStats stats : currentStats) {
            data.add(new String[]{
                    stats.getComponentName(),
                    String.format("%.2f", stats.getAverage()),
                    String.format("%.1f", stats.getMax()),
                    String.format("%.1f", stats.getMin()),
                    String.valueOf(stats.getCount())
            });
        }
        String filename = session.getUsername() + "_" + section.getCourseCode() + "_Stats.csv";
        CsvExporter.exportToCsv(this, headers, data, filename);
    }

    // Load statistics data from the server
    private void loadStatsData() {
        if (this.section == null) return;
        
        SwingWorker<List<ClassStats>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ClassStats> doInBackground() throws Exception {
                return instructorApi.getSectionStats(section.getSectionId());
            }

            @Override
            protected void done() {
                try {
                    currentStats = get();
                    // Set default text in case some stats are missing
                    quizStatsLabel.setText("No data");
                    midsemStatsLabel.setText("No data");
                    endsemStatsLabel.setText("No data");
                    
                    // Update labels with actual data
                    for (ClassStats stats : currentStats) {
                        String text = String.format(
                            "Average: %.2f  |  Highest: %.1f  |  Lowest: %.1f  (%d Students)",
                            stats.getAverage(), stats.getMax(), stats.getMin(), stats.getCount()
                        );
                        
                        // Put data in the right label based on exam type
                        switch (stats.getComponentName()) {
                            case "quiz":
                                quizStatsLabel.setText(text);
                                break;
                            case "midsem":
                                midsemStatsLabel.setText(text);
                                break;
                            case "endsem":
                                endsemStatsLabel.setText(text);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    quizStatsLabel.setText("Error loading stats.");
                    midsemStatsLabel.setText("Error loading stats.");
                    endsemStatsLabel.setText("Error loading stats.");
                }
            }
        };
        worker.execute();
    }
}