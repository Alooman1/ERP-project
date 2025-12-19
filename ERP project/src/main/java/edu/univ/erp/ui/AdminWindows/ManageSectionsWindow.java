package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// This window lets admins create new class sections and edit existing ones
public class ManageSectionsWindow extends JPanel {

    private final CourseService courseService;

    // Fields for creating new sections
    private JComboBox<CatalogItem> courseCombo;
    private JTextField capacityField;
    private JTextField dayTimeField;
    private JTextField roomField;
    private JTextField semesterField;
    private JTextField yearField;

    // NEW FIELDS for registration and drop deadlines
    private JTextField regDeadlineField;
    private JTextField dropDeadlineField;

    private JLabel createStatusLabel;

    // Fields for editing existing sections
    private JComboBox<CatalogItem> sectionCombo;
    private JTextField editDayTimeField;
    private JTextField editRoomField;
    private JLabel editStatusLabel;

    public ManageSectionsWindow(UserSession session) {
        this.courseService = new CourseService();
        setLayout(new BorderLayout());
        initUI();
    }

    // Sets up the main interface with tabs
    private void initUI() {
        JLabel title = new JLabel("MANAGE SECTIONS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Two tabs: one for creating, one for editing sections
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Create Section", buildCreatePanel());
        tabs.addTab("Edit Section", buildEditPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // Builds the panel for creating new class sections
    private JPanel buildCreatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dropdown to select which course to create a section for
        courseCombo = new JComboBox<>();
        setupCatalogItemRenderer(courseCombo);
        reloadCourses();

        // Input fields for section details
        capacityField = new JTextField(10);
        dayTimeField = new JTextField(20);
        roomField = new JTextField(10);
        semesterField = new JTextField(10);
        yearField = new JTextField(6);

        // NEW: Input fields for registration and drop deadlines
        regDeadlineField = new JTextField(12);
        dropDeadlineField = new JTextField(12);

        createStatusLabel = new JLabel(" ");

        JButton createBtn = new JButton("Create Section");
        createBtn.addActionListener(e -> doCreateSection());

        int row = 0;

        // Course selection
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        panel.add(courseCombo, gbc);
        row++;

        // Capacity input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        panel.add(capacityField, gbc);
        row++;

        // Day and time input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Day/Time:"), gbc);
        gbc.gridx = 1;
        panel.add(dayTimeField, gbc);
        row++;

        // Format hint
        gbc.gridx = 1; gbc.gridy = row;
        JLabel formatLabel = new JLabel("Format: Mon/Wed 10:00-11:30");
        formatLabel.setFont(formatLabel.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(formatLabel, gbc);
        row++;

        // Room input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1;
        panel.add(roomField, gbc);
        row++;

        // Semester input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        panel.add(semesterField, gbc);
        row++;

        // Year input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        panel.add(yearField, gbc);
        row++;

        // --- NEW INPUTS for deadlines ---
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Reg Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(regDeadlineField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Drop Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dropDeadlineField, gbc);
        row++;
        // -------------------------------

        // Create button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(createBtn, gbc);
        row++;

        // Status message area
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        createStatusLabel.setForeground(Color.BLUE);
        panel.add(createStatusLabel, gbc);

        return panel;
    }

    // Creates a new section when the create button is clicked
    private void doCreateSection() {
        CatalogItem course = (CatalogItem) courseCombo.getSelectedItem();
        if (course == null) {
            createStatusLabel.setText("Please select a course.");
            return;
        }

        // Get all the input values
        String courseCode = course.getCourseCode();
        String capacity = capacityField.getText().trim();
        String dayTime = dayTimeField.getText().trim();
        String room = roomField.getText().trim();
        String semester = semesterField.getText().trim();
        String year = yearField.getText().trim();

        // Get new deadline fields
        String regDeadline = regDeadlineField.getText().trim();
        String dropDeadline = dropDeadlineField.getText().trim();

        // Updated Method Call with 8 arguments (including deadlines)
        String msg = courseService.createSection(courseCode, capacity, dayTime, room, semester, year, regDeadline, dropDeadline);
        createStatusLabel.setText(msg);
        reloadSections();
    }

    // Loads all available courses into the dropdown
    private void reloadCourses() {
        courseCombo.removeAllItems();
        List<CatalogItem> courses = courseService.getAllCourses();
        for (CatalogItem c : courses) {
            courseCombo.addItem(c);
        }
    }

    // Builds the panel for editing existing sections
    private JPanel buildEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dropdown to select which section to edit
        sectionCombo = new JComboBox<>();
        setupCatalogItemRenderer(sectionCombo);
        reloadSections();

        // When a section is selected, load its current details
        sectionCombo.addActionListener(e -> onSectionSelected());

        // Fields for editing day/time and room
        editDayTimeField = new JTextField(20);
        editRoomField = new JTextField(10);
        editStatusLabel = new JLabel(" ");

        JButton updateBtn = new JButton("Update Time/Room");
        updateBtn.addActionListener(e -> doUpdateSection());

        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> reloadSections());

        int row = 0;

        // Section selection with refresh button
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Section:"), gbc);
        gbc.gridx = 1;
        panel.add(sectionCombo, gbc);
        gbc.gridx = 2;
        panel.add(refreshBtn, gbc);
        row++;

        // New day/time input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("New Day/Time:"), gbc);
        gbc.gridx = 1;
        panel.add(editDayTimeField, gbc);
        row++;

        // Format hint
        gbc.gridx = 1; gbc.gridy = row;
        JLabel formatLabel = new JLabel("Format: Tue/Thu 14:00-15:30");
        formatLabel.setFont(formatLabel.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(formatLabel, gbc);
        row++;

        // New room input
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("New Room:"), gbc);
        gbc.gridx = 1;
        panel.add(editRoomField, gbc);
        row++;

        // Update button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(updateBtn, gbc);
        row++;

        // Status message area
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        editStatusLabel.setForeground(Color.BLUE);
        panel.add(editStatusLabel, gbc);

        return panel;
    }

    // Loads all existing sections into the dropdown
    private void reloadSections() {
        sectionCombo.removeAllItems();
        List<CatalogItem> sections = courseService.getFullCatalog();
        for (CatalogItem c : sections) {
            sectionCombo.addItem(c);
        }
        onSectionSelected();
    }

    // When a section is selected, show its current details in the edit fields
    private void onSectionSelected() {
        if (editDayTimeField == null || editRoomField == null) {
            return;
        }

        CatalogItem section = (CatalogItem) sectionCombo.getSelectedItem();

        if (section == null) {
            editDayTimeField.setText("");
            editRoomField.setText("");
            return;
        }

        // Fill the edit fields with current section details
        editDayTimeField.setText(section.getDayTime() == null ? "" : section.getDayTime());
        editRoomField.setText(section.getRoom() == null ? "" : section.getRoom());
    }

    // Updates the selected section with new day/time and room
    private void doUpdateSection() {
        CatalogItem section = (CatalogItem) sectionCombo.getSelectedItem();
        if (section == null) {
            editStatusLabel.setText("Please select a section.");
            return;
        }

        int sectionId = section.getSectionId();
        String newDayTime = editDayTimeField.getText().trim();
        String newRoom = editRoomField.getText().trim();

        String msg = courseService.updateSectionSchedule(sectionId, newDayTime, newRoom);
        editStatusLabel.setText(msg);

        reloadSections();
    }

    // Custom display for dropdown items - shows course codes and titles nicely
    private void setupCatalogItemRenderer(JComboBox<CatalogItem> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof CatalogItem item) {
                    String text;
                    if (item.getSectionId() == 0) {
                        // Show just course code and title for courses
                        text = item.getCourseCode() + " - " + item.getCourseTitle();
                    } else {
                        // Show section details with day/time for sections
                        String timePart = item.getDayTime() == null ? "" : " [" + item.getDayTime() + "]";
                        text = "Sec#" + item.getSectionId() + " | " +
                                item.getCourseCode() + " - " + item.getCourseTitle() + timePart;
                    }
                    setText(text);
                }

                return this;
            }
        });
    }
}