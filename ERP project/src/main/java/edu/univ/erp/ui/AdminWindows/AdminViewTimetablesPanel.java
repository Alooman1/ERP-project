package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.ui.StudentWindows.TimetableModel;
import edu.univ.erp.ui.InstructorWindows.InstructorTimetableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.DefaultListCellRenderer;

import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

// This panel lets admins view anyone's schedule - students or teachers
public class AdminViewTimetablesPanel extends JPanel {

    private final UserSession session;
    private final AdminApi adminApi;

    // Dropdowns to pick user type and specific user
    private JComboBox<String> userTypeSelect;
    private JComboBox<Object> userSelect; // Can hold Student or Instructor objects
    private JTable timetableTable;
    private JLabel titleLabel;

    // Colors for making the table look nice
    private final Color TABLE_BG_COLOR = new Color(0, 0, 0, 0);
    private final Color HEADER_BG_COLOR = new Color(45, 45, 45);

    public AdminViewTimetablesPanel(UserSession session) {
        this.session = session;
        this.adminApi = new AdminApi();

        // Setup the main panel with transparent background
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // When this panel becomes visible, load the user list and timetable
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadUserList();
                loadTimetable();
            }
        });

        // Big title at the top
        titleLabel = new JLabel("VIEW TIMETABLES");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Panel with dropdowns to select which user's schedule to view
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        selectionPanel.setOpaque(false);

        // Dropdown to choose between Student or Instructor
        userTypeSelect = new JComboBox<>(new String[]{"Select Type...", "Student", "Instructor"});
        userTypeSelect.setPreferredSize(new Dimension(150, 30));
        userTypeSelect.addActionListener(e -> loadUserList());

        // Dropdown that shows actual students or teachers
        userSelect = new JComboBox<>();
        userSelect.setPreferredSize(new Dimension(300, 30));
        userSelect.setRenderer(new UserListRenderer());
        userSelect.addActionListener(e -> loadTimetable());

        selectionPanel.add(createLabel("View Schedule For:"));
        selectionPanel.add(userTypeSelect);
        selectionPanel.add(userSelect);

        // Table that shows the actual timetable
        timetableTable = new JTable();
        styleTable(timetableTable);
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        styleScrollPane(scrollPane);

        // Center area with selection panel and timetable
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(selectionPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    // Loads students or teachers into the dropdown based on selection
    private void loadUserList() {
        String type = (String) userTypeSelect.getSelectedItem();
        userSelect.removeAllItems();

        if ("Student".equals(type)) {
            // Load all students in background so UI doesn't freeze
            SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
                @Override protected List<Student> doInBackground() { return adminApi.getAllStudents(); }
                @Override protected void done() {
                    try {
                        for (Student s : get()) userSelect.addItem(s);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        } else if ("Instructor".equals(type)) {
            // Load all teachers in background
            SwingWorker<List<Instructor>, Void> worker = new SwingWorker<>() {
                @Override protected List<Instructor> doInBackground() { return adminApi.getAllInstructors(); }
                @Override protected void done() {
                    try {
                        for (Instructor i : get()) userSelect.addItem(i);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        }
    }

    // Loads the timetable for selected student or teacher
    private void loadTimetable() {
        Object selected = userSelect.getSelectedItem();
        if (selected == null || "Select Type...".equals(userTypeSelect.getSelectedItem())) {
            timetableTable.setModel(new javax.swing.table.DefaultTableModel());
            return;
        }

        if (selected instanceof Student) {
            // Get schedule for selected student
            int id = ((Student) selected).getUserId();
            SwingWorker<List<CatalogItem>, Void> worker = new SwingWorker<>() {
                @Override protected List<CatalogItem> doInBackground() { return adminApi.getStudentSchedule(id); }
                @Override protected void done() {
                    try {
                        timetableTable.setModel(new TimetableModel(get()));
                        styleTable(timetableTable);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        } else if (selected instanceof Instructor) {
            // Get schedule for selected teacher
            int id = ((Instructor) selected).getUserId();
            SwingWorker<List<AssignedSection>, Void> worker = new SwingWorker<>() {
                @Override protected List<AssignedSection> doInBackground() { return adminApi.getInstructorSchedule(id); }
                @Override protected void done() {
                    try {
                        timetableTable.setModel(new InstructorTimetableModel(get()));
                        styleTable(timetableTable);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        }
    }

    // Helper to create nice-looking labels
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        return l;
    }

    // Makes the timetable table look pretty with colors and fonts
    private void styleTable(JTable table) {
        table.setOpaque(false);
        table.setBackground(TABLE_BG_COLOR);
        table.setForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(new Color(80, 80, 80));
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(0, 120, 215));
        table.setSelectionForeground(Color.WHITE);

        // Style the column headers
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(HEADER_BG_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(100, 40));
    }

    // Makes the scrollbar area look nice
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    // Custom display for dropdown - shows names with roll numbers or departments
    private static class UserListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Student) {
                Student s = (Student) value;
                setText(s.getFullName() + " (" + s.getRollNo() + ")");
            } else if (value instanceof Instructor) {
                Instructor i = (Instructor) value;
                setText(i.getFullName() + " (" + i.getDepartment() + ")");
            }
            return this;
        }
    }
}