package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminCoursesPanel extends JPanel {

    private final UserSession session;
    private final AdminApi adminApi;

    private final Color BOX_BG_COLOR = new Color(60, 60, 60, 220);

    private JPanel mainContent;

    // Fields for creating courses
    private JTextField createCodeField, createTitleField, createCreditsField;

    // Fields for creating sections
    private JComboBox<CatalogItem> createSectionCourseSelect;
    private JTextField sectionDayTimeField, sectionRoomField, sectionCapacityField, sectionSemesterField, sectionYearField;
    private JTextField regDeadlineField, dropDeadlineField; // Registration deadlines

    // Fields for editing courses
    private JComboBox<CatalogItem> editCourseSelect;
    private JTextField editTitleField, editCreditsField;
    private CatalogItem selectedCourseToEdit;

    // Fields for editing sections
    private JComboBox<CatalogItem> editSectionSelect;
    private JTextField editSecDayTimeField, editSecRoomField, editSecCapacityField, editSecSemesterField, editSecYearField;
    private CatalogItem selectedSectionToEdit;

    // Fields for deleting courses
    private JComboBox<CatalogItem> deleteCourseSelect;
    private JButton deleteCourseRefreshButton;

    // Main constructor - sets up course and section management panel
    public AdminCoursesPanel(UserSession session) {
        this.session = session;
        this.adminApi = new AdminApi();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Main title
        JLabel titleLabel = new JLabel("CREATE/EDIT COURSES & SECTIONS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

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

        mainContent.add(titleLabel, gbc);

        // Add all collapsible sections for different actions
        gbc.gridy = 1;
        JPanel createFormPanel = createCreateCourseForm();
        createFormPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Create New Course", createFormPanel), gbc);

        gbc.gridy = 2;
        JPanel editFormPanel = createEditCourseForm();
        editFormPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Edit Course", editFormPanel), gbc);

        gbc.gridy = 3;
        JPanel sectionFormPanel = createCreateSectionForm();
        sectionFormPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Create New Section", sectionFormPanel), gbc);

        gbc.gridy = 4;
        JPanel editSectionPanel = createEditSectionForm();
        editSectionPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Edit/Manage Section", editSectionPanel), gbc);

        gbc.gridy = 5;
        JPanel deleteCoursePanel = createDeleteCourseForm();
        deleteCoursePanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Delete Course", deleteCoursePanel), gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        mainContent.add(Box.createVerticalGlue(), gbc);

        // Make panel scrollable
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        loadAllData(); // Load initial data
    }

    // Creates expandable/collapsible sections
    private JPanel createCollapsibleSection(String title, JPanel contentPanelToToggle) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setOpaque(false);

        // Clickable header
        RoundedPanel headerPanel = new RoundedPanel(BOX_BG_COLOR, 15);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel arrowLabel = new JLabel("\u2304  "); // Down arrow
        arrowLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        arrowLabel.setForeground(Color.WHITE);
        arrowLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(arrowLabel, BorderLayout.EAST);

        // Toggle visibility when header clicked
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = !contentPanelToToggle.isVisible();
                contentPanelToToggle.setVisible(isVisible);
                arrowLabel.setText(isVisible ? "\u2303  " : "\u2304  "); // Change arrow
                mainContent.revalidate();
                mainContent.repaint();
            }
        });

        sectionPanel.add(headerPanel, BorderLayout.NORTH);
        sectionPanel.add(contentPanelToToggle, BorderLayout.CENTER);

        return sectionPanel;
    }

    // --- Forms for different actions ---

    // Form for creating new courses
    private JPanel createCreateCourseForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        createCodeField = createTextField();
        createTitleField = createTextField();
        createCreditsField = createTextField();

        // Add course creation fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Course Code (e.g. CS101):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Course Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createTitleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Credits:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createCreditsField, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = createSaveButton("Create Course");
        saveButton.addActionListener(e -> handleCreateCourse());
        formPanel.add(saveButton, gbc);

        return formPanel;
    }

    // Form for editing existing courses
    private JPanel createEditCourseForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        editCourseSelect = new JComboBox<>();
        editCourseSelect.setRenderer(new CourseComboBoxRenderer());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(editCourseSelect, gbc);
        gbc.gridwidth = 1;

        editTitleField = createTextField();
        editCreditsField = createTextField();

        // Add course editing fields
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Course Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(editTitleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Credits:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(editCreditsField, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = createSaveButton("Update Course");
        saveButton.addActionListener(e -> handleUpdateCourse());
        formPanel.add(saveButton, gbc);

        // When course selected, populate fields with its data
        editCourseSelect.addActionListener(e -> {
            selectedCourseToEdit = (CatalogItem) editCourseSelect.getSelectedItem();
            if (selectedCourseToEdit != null) {
                editTitleField.setText(selectedCourseToEdit.getCourseTitle());
                editCreditsField.setText(String.valueOf(selectedCourseToEdit.getCredits()));
            } else {
                editTitleField.setText("");
                editCreditsField.setText("");
            }
        });

        return formPanel;
    }

    // Form for creating new course sections
    private JPanel createCreateSectionForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        createSectionCourseSelect = new JComboBox<>();
        createSectionCourseSelect.setRenderer(new CourseComboBoxRenderer());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(createSectionCourseSelect, gbc);
        gbc.gridwidth = 1;

        sectionDayTimeField = createTextField();
        sectionRoomField = createTextField();
        sectionCapacityField = createTextField();
        sectionSemesterField = createTextField();
        sectionYearField = createTextField();
        regDeadlineField = createTextField();
        dropDeadlineField = createTextField();

        // Set default values
        sectionSemesterField.setText("Fall");
        sectionYearField.setText("2025");

        // Add all section creation fields
        addFormRow(formPanel, gbc, 1, "Day/Time (e.g. Mon/Wed 10:00):", sectionDayTimeField);
        addFormRow(formPanel, gbc, 2, "Room:", sectionRoomField);
        addFormRow(formPanel, gbc, 3, "Capacity:", sectionCapacityField);
        addFormRow(formPanel, gbc, 4, "Semester:", sectionSemesterField);
        addFormRow(formPanel, gbc, 5, "Year:", sectionYearField);
        addFormRow(formPanel, gbc, 6, "Reg Deadline (YYYY-MM-DD):", regDeadlineField);
        addFormRow(formPanel, gbc, 7, "Drop Deadline (YYYY-MM-DD):", dropDeadlineField);

        gbc.gridx = 1; gbc.gridy = 8; gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = createSaveButton("Create Section");
        saveButton.addActionListener(e -> handleCreateSection());
        formPanel.add(saveButton, gbc);

        return formPanel;
    }

    // Form for editing existing sections
    private JPanel createEditSectionForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        editSectionSelect = new JComboBox<>();
        editSectionSelect.setRenderer(new SectionComboBoxRenderer());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(editSectionSelect, gbc);
        gbc.gridwidth = 1;

        editSecDayTimeField = createTextField();
        editSecRoomField = createTextField();

        addFormRow(formPanel, gbc, 1, "New Day/Time:", editSecDayTimeField);
        addFormRow(formPanel, gbc, 2, "New Room:", editSecRoomField);

        // Buttons for delete and update
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton deleteButton = createDeleteButton("DELETE SECTION");
        deleteButton.addActionListener(e -> handleDeleteSection());

        JButton updateButton = createSaveButton("Update Schedule");
        updateButton.addActionListener(e -> handleUpdateSection());

        btnPanel.add(deleteButton);
        btnPanel.add(updateButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnPanel, gbc);

        // When section selected, populate fields with its data
        editSectionSelect.addActionListener(e -> {
            selectedSectionToEdit = (CatalogItem) editSectionSelect.getSelectedItem();
            if (selectedSectionToEdit != null) {
                editSecDayTimeField.setText(selectedSectionToEdit.getDayTime());
                editSecRoomField.setText(selectedSectionToEdit.getRoom());
            } else {
                editSecDayTimeField.setText("");
                editSecRoomField.setText("");
            }
        });

        return formPanel;
    }

    // Helper to add form rows consistently
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    // Form for deleting courses
    private JPanel createDeleteCourseForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        deleteCourseSelect = new JComboBox<>();
        deleteCourseSelect.setRenderer(new CourseComboBoxRenderer());

        deleteCourseRefreshButton = createSaveButton("Refresh");
        deleteCourseRefreshButton.addActionListener(e -> loadAllData());

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Select Course to Delete:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deleteCourseSelect, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deleteCourseRefreshButton, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        JButton deleteButton = createDeleteButton("DELETE COURSE");
        deleteButton.addActionListener(e -> handleDeleteCourse());
        formPanel.add(deleteButton, gbc);

        return formPanel;
    }

    // --- Action handlers ---

    // Loads courses and sections from database
    private void loadAllData() {
        SwingWorker<List<CatalogItem>, Void> courseWorker = new SwingWorker<>() {
            @Override
            protected List<CatalogItem> doInBackground() { return adminApi.getAllCourses(); }
            @Override
            protected void done() {
                try {
                    List<CatalogItem> courses = get();
                    // Populate all course dropdowns
                    editCourseSelect.removeAllItems();
                    createSectionCourseSelect.removeAllItems();
                    deleteCourseSelect.removeAllItems();

                    editCourseSelect.addItem(null);
                    createSectionCourseSelect.addItem(null);
                    deleteCourseSelect.addItem(null);

                    for (CatalogItem course : courses) {
                        editCourseSelect.addItem(course);
                        createSectionCourseSelect.addItem(course);
                        deleteCourseSelect.addItem(course);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        courseWorker.execute();

        SwingWorker<List<CatalogItem>, Void> sectionWorker = new SwingWorker<>() {
            @Override
            protected List<CatalogItem> doInBackground() { return adminApi.getAllSectionsDetailed(); }
            @Override
            protected void done() {
                try {
                    List<CatalogItem> sections = get();
                    editSectionSelect.removeAllItems();
                    editSectionSelect.addItem(null);
                    for (CatalogItem sec : sections) {
                        editSectionSelect.addItem(sec);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        sectionWorker.execute();
    }

    // Creates a new course in database
    private void handleCreateCourse() {
        String code = createCodeField.getText();
        String title = createTitleField.getText();
        int credits;
        try {
            credits = Integer.parseInt(createCreditsField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() { return adminApi.createCourse(code, title, credits); }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminCoursesPanel.this, result, "Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        // Clear form and refresh data
                        createCodeField.setText(""); createTitleField.setText(""); createCreditsField.setText("");
                        loadAllData();
                    }
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    // Creates a new section for a course
    private void handleCreateSection() {
        CatalogItem selectedCourse = (CatalogItem) createSectionCourseSelect.getSelectedItem();
        if (selectedCourse == null) return;

        String code = selectedCourse.getCourseCode();
        String cap = sectionCapacityField.getText();
        String dt = sectionDayTimeField.getText();
        String room = sectionRoomField.getText();
        String sem = sectionSemesterField.getText();
        String yr = sectionYearField.getText();
        String reg = regDeadlineField.getText();
        String drop = dropDeadlineField.getText();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                // Create section with deadlines
                return adminApi.createSection(code, cap, dt, room, sem, yr, reg, drop);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminCoursesPanel.this, result, "Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        // Clear form and refresh data
                        sectionCapacityField.setText(""); sectionDayTimeField.setText(""); sectionRoomField.setText("");
                        regDeadlineField.setText(""); dropDeadlineField.setText("");
                        loadAllData();
                    }
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    // Updates section schedule and room
    private void handleUpdateSection() {
        if (selectedSectionToEdit == null) {
            JOptionPane.showMessageDialog(this, "Please select a section.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final String newDayTime = editSecDayTimeField.getText().trim();
        final String newRoom = editSecRoomField.getText().trim();
        final int sectionId = selectedSectionToEdit.getSectionId();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return adminApi.updateSectionSchedule(sectionId, newDayTime, newRoom);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminCoursesPanel.this, result, "Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) loadAllData();
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    // Deletes a section from database
    private void handleDeleteSection() {
        if (selectedSectionToEdit == null) {
            JOptionPane.showMessageDialog(this, "Please select a section.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int sectionId = selectedSectionToEdit.getSectionId();
        String label = selectedSectionToEdit.getCourseCode() + " (Section " + sectionId + ")";

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + label + "?\nThis will fail if students are enrolled.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return adminApi.deleteSection(sectionId);
                }
                @Override
                protected void done() {
                    try {
                        String result = get();
                        JOptionPane.showMessageDialog(AdminCoursesPanel.this, result, "Status", JOptionPane.INFORMATION_MESSAGE);
                        if (result.startsWith("Success")) loadAllData();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        }
    }

    // Updates course information
    private void handleUpdateCourse() {
        if (selectedCourseToEdit == null) return;
        String code = selectedCourseToEdit.getCourseCode();
        String title = editTitleField.getText();
        int credits;
        try { credits = Integer.parseInt(editCreditsField.getText()); } catch (Exception e) { return; }

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() { return adminApi.updateCourse(code, title, credits); }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminCoursesPanel.this, result, "Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) loadAllData();
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    // Deletes a course from database
    private void handleDeleteCourse() {
        CatalogItem selectedCourse = (CatalogItem) deleteCourseSelect.getSelectedItem();
        if (selectedCourse == null) return;
        String code = selectedCourse.getCourseCode();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + code + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() { return adminApi.deleteCourse(code); }
                @Override
                protected void done() {
                    try {
                        String result = get();
                        JOptionPane.showMessageDialog(AdminCoursesPanel.this, result, "Status", JOptionPane.INFORMATION_MESSAGE);
                        if (result.startsWith("Success")) loadAllData();
                    } catch (Exception e) {}
                }
            };
            worker.execute();
        }
    }

    // --- Helper methods for styling ---

    // Creates styled labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    // Creates styled text fields
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
        return field;
    }

    // Creates green save buttons
    private JButton createSaveButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(new Color(80, 140, 80));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Creates red delete buttons
    private JButton createDeleteButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(new Color(200, 50, 50));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Shows course info in dropdown (code and title)
    private static class CourseComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof CatalogItem) {
                CatalogItem item = (CatalogItem) value;
                setText(item.getCourseCode() + ": " + item.getCourseTitle());
            } else setText("Select a course...");
            return this;
        }
    }

    // Shows section info in dropdown (course code, section ID, schedule)
    private static class SectionComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof CatalogItem) {
                CatalogItem item = (CatalogItem) value;
                setText(item.getCourseCode() + " (Sec " + item.getSectionId() + ") - " + item.getDayTime());
            } else setText("Select a section...");
            return this;
        }
    }

    // Panel with rounded corners for section headers
    private static class RoundedPanel extends JPanel {
        private Color backgroundColor;
        private int cornerRadius;
        public RoundedPanel(Color bgColor, int radius) {
            super(); this.backgroundColor = bgColor; this.cornerRadius = radius; setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}