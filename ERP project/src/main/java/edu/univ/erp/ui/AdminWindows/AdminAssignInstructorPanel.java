package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class AdminAssignInstructorPanel extends JPanel {

    private final UserSession session;
    private final AdminApi adminApi;

    // Dropdowns for selecting section and instructor
    private JComboBox<CatalogItem> sectionSelect;
    private JComboBox<Instructor> instructorSelect;
    private JButton assignButton;

    // Main constructor - sets up instructor assignment panel
    public AdminAssignInstructorPanel(UserSession session) {
        this.session = session;
        this.adminApi = new AdminApi();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Load data when this panel becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadDropdownData();
            }
        });
        
        // Main title
        JLabel titleLabel = new JLabel("ASSIGN INSTRUCTOR");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Section selection dropdown
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(createLabel("Select Course Section:"), gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        sectionSelect = new JComboBox<>();
        sectionSelect.setRenderer(new SectionComboBoxRenderer());
        formPanel.add(sectionSelect, gbc);

        gbc.gridx = 1; gbc.weightx = 0.0;
        JButton refreshSectionsBtn = new JButton("Refresh");
        refreshSectionsBtn.addActionListener(e -> loadDropdownData());
        formPanel.add(refreshSectionsBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 1.0; gbc.gridwidth = 2;
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        
        // Instructor selection dropdown
        gbc.gridy = 3;
        formPanel.add(createLabel("Select Instructor to Assign:"), gbc);
        
        gbc.gridy = 4; gbc.gridwidth = 1;
        instructorSelect = new JComboBox<>();
        instructorSelect.setRenderer(new InstructorComboBoxRenderer());
        formPanel.add(instructorSelect, gbc);

        gbc.gridx = 1; gbc.weightx = 0.0;
        JButton refreshInstBtn = new JButton("Refresh");
        refreshInstBtn.addActionListener(e -> loadDropdownData());
        formPanel.add(refreshInstBtn, gbc);

        // Assign button
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 1.0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 5, 5, 5);
        assignButton = new JButton("Assign Instructor");
        assignButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        assignButton.setOpaque(true);
        assignButton.setBackground(new Color(80, 140, 80)); // Green color
        assignButton.setForeground(Color.WHITE);
        assignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(assignButton, gbc);

        assignButton.addActionListener(e -> handleAssign());

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        add(formPanel, BorderLayout.CENTER);

        loadDropdownData(); // Load initial data
    }
    
    // Loads sections and instructors from database into dropdowns
    private void loadDropdownData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<CatalogItem> sections;
            List<Instructor> instructors;

            @Override
            protected Void doInBackground() throws Exception {
                // Get data from database
                sections = adminApi.getAllSectionsDetailed();
                instructors = adminApi.getAllInstructors();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); 

                    // Populate sections dropdown
                    Object selectedSection = sectionSelect.getSelectedItem();
                    sectionSelect.removeAllItems();
                    sectionSelect.addItem(null); // Empty option
                    for (CatalogItem section : sections) {
                        sectionSelect.addItem(section);
                    }

                    // Populate instructors dropdown
                    Object selectedInst = instructorSelect.getSelectedItem();
                    instructorSelect.removeAllItems();
                    instructorSelect.addItem(null); // Empty option
                    for (Instructor instructor : instructors) {
                        instructorSelect.addItem(instructor);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminAssignInstructorPanel.this, "Error loading data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Handles assigning instructor to section when button clicked
    private void handleAssign() {
        CatalogItem selectedSection = (CatalogItem) sectionSelect.getSelectedItem();
        Instructor selectedInstructor = (Instructor) instructorSelect.getSelectedItem();

        // Check if both section and instructor are selected
        if (selectedSection == null || selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "Please select both a section and an instructor.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int sectionId = selectedSection.getSectionId();
        int instructorId = selectedInstructor.getUserId();
        
        // Actually assign instructor to section in database
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminApi.assignInstructorToSection(sectionId, instructorId);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminAssignInstructorPanel.this, result, "Assignment Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        loadDropdownData(); // Refresh to show the new assignment
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAssignInstructorPanel.this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Helper to create styled labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }
    
    // Shows section info in dropdown (course code, title, current instructor)
    private static class SectionComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof CatalogItem) {
                CatalogItem item = (CatalogItem) value;
                setText(item.getCourseCode() + " (" + item.getCourseTitle() + ") - Currently: " + item.getInstructorName());
            } else {
                setText("Select a Course Section...");
            }
            return this;
        }
    }
    
    // Shows instructor info in dropdown (name and department)
    private static class InstructorComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Instructor) {
                Instructor inst = (Instructor) value;
                setText(inst.getFullName() + " (" + inst.getDepartment() + ")");
            } else {
                setText("Select an Instructor...");
            }
            return this;
        }
    }
}