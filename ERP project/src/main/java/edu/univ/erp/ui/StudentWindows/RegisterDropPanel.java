package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.ui.StudentWindows.StudentDashboard;

import edu.univ.erp.api.catalog.CatalogApi;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.api.admin.AdminApi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class RegisterDropPanel extends JPanel {

    private final UserSession session;
    private final StudentApi studentApi;
    private final CatalogApi catalogApi;
    private final StudentDashboard dashboardToRefresh;

    // Color scheme for dark theme
    private final Color BOX_BG_COLOR = new Color(60, 60, 60, 220);
    private final Color TABLE_BG_COLOR = new Color(0, 0, 0, 0);
    private final Color HEADER_BG_COLOR = new Color(45, 45, 45);
    private final AdminApi adminApi;
    
    // Tables for different sections
    private JTable timetable;
    private JTable registerTable;
    private JTable registeredTable;

    // Data models for tables
    private TimetableModel timetableModel;
    private RegisterCoursesModel registerModel;
    private RegisteredCoursesModel registeredModel;

    private JPanel mainContent;

    public RegisterDropPanel(UserSession session,StudentDashboard dashboard) {
        this.session = session;
        this.studentApi = new StudentApi();
        this.catalogApi = new CatalogApi();
        this.dashboardToRefresh = dashboard;
        this.adminApi = new AdminApi();
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Main title
        JLabel titleLabel = new JLabel("REGISTER / DROP");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Main content area with collapsible sections
        mainContent = new JPanel();
        mainContent.setOpaque(false);
        mainContent.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Create three collapsible sections
        gbc.gridy = 0;
        mainContent.add(createCollapsibleSection("View Time Table", createTimeTablePanel()), gbc);

        gbc.gridy = 1;
        mainContent.add(createCollapsibleSection("Register Courses", createRegisterCoursesPanel()), gbc);

        gbc.gridy = 2;
        mainContent.add(createCollapsibleSection("Registered Courses", createDropCoursesPanel()), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        mainContent.add(Box.createVerticalGlue(), gbc);

        // Make content scrollable
        JScrollPane scrollPane = new JScrollPane(mainContent,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        add(scrollPane, BorderLayout.CENTER);

        // Load data when panel is created
        loadAllData();
        
        // Reload data when this panel becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadAllData();
            }
        });
    }

    // Create collapsible section with header
    private JPanel createCollapsibleSection(String title, JPanel contentPanel) {

        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setOpaque(false);

        // Rounded header panel that can be clicked to expand/collapse
        RoundedPanel headerPanel = new RoundedPanel(BOX_BG_COLOR, 15);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Arrow that changes direction when expanded/collapsed
        JLabel arrow = new JLabel("\u2304  ");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 18));
        arrow.setForeground(Color.WHITE);
        arrow.setBorder(new EmptyBorder(15, 15, 15, 15));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(arrow, BorderLayout.EAST);

        contentPanel.setVisible(false); // Start collapsed

        // Toggle visibility when header is clicked
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                boolean show = !contentPanel.isVisible();
                contentPanel.setVisible(show);

                arrow.setText(show ? "\u2303  " : "\u2304  "); // Up/down arrow

                mainContent.revalidate();
                mainContent.repaint();
            }
        });

        sectionPanel.add(headerPanel, BorderLayout.NORTH);
        sectionPanel.add(contentPanel, BorderLayout.CENTER);

        return sectionPanel;
    }

    private JPanel createTimeTablePanel() {
        timetableModel = new TimetableModel(List.of());
        timetable = new JTable(timetableModel);
        styleTable(timetable);

        JScrollPane sp = new JScrollPane(timetable);
        styleScrollPane(sp);
        sp.setPreferredSize(new Dimension(800, 317));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.add(sp, BorderLayout.CENTER);

        // Download timetable button
        JButton downloadBtn = new JButton("Download Timetable");
        setupActionButton(downloadBtn);

        downloadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Download Timetable");
            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    // Save timetable as text file
                    java.io.FileWriter fw = new java.io.FileWriter(chooser.getSelectedFile());
                    fw.write(timetableModel.exportAsText());
                    fw.close();

                    JOptionPane.showMessageDialog(this, "Timetable downloaded successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            }
        });

        JPanel downloadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        downloadPanel.setOpaque(false);
        downloadPanel.add(downloadBtn);

        panel.add(downloadPanel, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createRegisterCoursesPanel() {
        registerModel = new RegisterCoursesModel(List.of());
        registerTable = new JTable(registerModel);
        styleTable(registerTable);

        JScrollPane sp = new JScrollPane(registerTable);
        styleScrollPane(sp);
        sp.setPreferredSize(new Dimension(800, 200));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.add(sp, BorderLayout.CENTER);

        // Register button for selected course
        JButton btn = new JButton("Register Selected Course");
        setupActionButton(btn);
        btn.addActionListener(e -> handleRegister());

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setOpaque(false);
        bp.add(btn);
        panel.add(bp, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDropCoursesPanel() {
        registeredModel = new RegisteredCoursesModel(List.of());
        registeredTable = new JTable(registeredModel);
        styleTable(registeredTable);

        JScrollPane sp = new JScrollPane(registeredTable);
        styleScrollPane(sp);
        sp.setPreferredSize(new Dimension(800, 200));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.add(sp, BorderLayout.CENTER);

        // Drop button for selected course
        JButton btn = new JButton("Drop Selected Course");
        setupActionButton(btn);
        btn.addActionListener(e -> handleDrop());

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setOpaque(false);
        bp.add(btn);
        panel.add(bp, BorderLayout.SOUTH);

        return panel;
    }

    // Handle course registration
    private void handleRegister() {
        if (adminApi.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this, "System is in maintenance. Registration is disabled.", "Maintenance Mode", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = registerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course.");
            return;
        }

        CatalogItem item = registerModel.getItemAt(row);

        // Register for course in background thread
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() throws Exception {
                return studentApi.registerForSection(session.getUserId(), item.getSectionId());
            }
            @Override protected void done() {
                try {
                    JOptionPane.showMessageDialog(RegisterDropPanel.this, get());
                    dashboardToRefresh.refreshHomePageData(); // Update dashboard
                    loadAllData(); // Refresh all tables
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    // Handle course dropping
    private void handleDrop() {
        if (adminApi.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this, "System is in maintenance. Dropping courses is disabled.", "Maintenance Mode", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = registeredTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course.");
            return;
        }

        CatalogItem item = registeredModel.getItemAt(row);

        // Drop course in background thread
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() throws Exception {
                return studentApi.dropSection(session.getUserId(), item.getSectionId());
            }
            @Override protected void done() {
                try {
                    JOptionPane.showMessageDialog(RegisterDropPanel.this, get());
                    dashboardToRefresh.refreshHomePageData(); // Update dashboard
                    loadAllData(); // Refresh all tables
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    // Style action buttons consistently
    private void setupActionButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(8, 15, 8, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Apply dark theme styling to tables
    private void styleTable(JTable t) {
        t.setOpaque(false);
        t.setBackground(TABLE_BG_COLOR);
        t.setForeground(Color.WHITE);
        t.setShowGrid(false);
        t.setRowHeight(30);
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Style table header
        JTableHeader h = t.getTableHeader();
        h.setOpaque(true);
        h.setBackground(HEADER_BG_COLOR);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    // Style scroll panes
    private void styleScrollPane(JScrollPane sp) {
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));
    }

    // Load all data for the panel (courses, timetable, registrations)
    private void loadAllData() {
        SwingWorker<AllData, Void> worker = new SwingWorker<>() {
            @Override protected AllData doInBackground() throws Exception {
                return new AllData(
                        catalogApi.getCatalog(), // All available courses
                        catalogApi.getRegisteredCatalogItems(session.getUserId()) // Student's registered courses
                );
            }
            @Override protected void done() {
                try {
                    AllData d = get();

                    // Update register table with all available courses
                    registerModel = new RegisterCoursesModel(d.allCourses);
                    registerTable.setModel(registerModel);

                    // Update registered table with student's courses
                    registeredModel = new RegisteredCoursesModel(d.registeredCourses);
                    registeredTable.setModel(registeredModel);

                    // Update timetable with student's schedule
                    timetableModel = new TimetableModel(d.registeredCourses);
                    timetable.setModel(timetableModel);

                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    // Helper class to hold all data needed for the panel
    private static class AllData {
        final List<CatalogItem> allCourses;
        final List<CatalogItem> registeredCourses;
        AllData(List<CatalogItem> a, List<CatalogItem> r) {
            allCourses = a;
            registeredCourses = r;
        }
    }

    // Custom panel with rounded corners for section headers
    private static class RoundedPanel extends JPanel {
        private final Color bg;
        private final int radius;
        public RoundedPanel(Color bg, int radius) {
            this.bg = bg;
            this.radius = radius;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
        }
    }
}