package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class AdminAddUserPanel extends JPanel {

    private final UserSession session;
    private final AdminApi adminApi;
    private JComboBox<String> deleteUserSelect;

    private final Color BOX_BG_COLOR = new Color(60, 60, 60, 220);
    private static final Color DELETE_USER_COLOR = new Color(255, 100, 100);
    private static final Color USER_ROLE_COLOR = Color.WHITE;

    private JPanel mainContent;

    // Fields for creating different types of users
    private JTextField studentNameField, studentUserField, studentRollNoField, studentProgramField, studentYearField;
    private JPasswordField studentPassField;

    private JTextField instNameField, instUserField, instDeptField;
    private JPasswordField instPassField;

    private JTextField adminNameField, adminUserField;
    private JPasswordField adminPassField;

    // Main constructor - sets up the whole user management panel
    public AdminAddUserPanel(UserSession session) {
        this.session = session;
        this.adminApi = new AdminApi();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Create the main title
        JLabel titleLabel = new JLabel("ADD USERS");
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

        // Add all the collapsible sections for different user types
        gbc.gridy = 1;
        JPanel studentFormPanel = createStudentForm();
        studentFormPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Add Student", studentFormPanel), gbc);

        gbc.gridy = 2;
        JPanel instructorFormPanel = createInstructorForm();
        instructorFormPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Add Instructor", instructorFormPanel), gbc);

        gbc.gridy = 3;
        JPanel adminFormPanel = createAdminForm();
        adminFormPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Add Admin", adminFormPanel), gbc);

        gbc.gridy = 4;
        JPanel deleteUserPanel = createDeleteUserForm();
        deleteUserPanel.setVisible(false);
        mainContent.add(createCollapsibleSection("Delete User", deleteUserPanel), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        mainContent.add(Box.createVerticalGlue(), gbc);

        // Make the panel scrollable
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        loadAllUsernames(); // Load users for deletion dropdown
    }

    // Creates sections that can be expanded/collapsed by clicking
    private JPanel createCollapsibleSection(String title, JPanel contentPanelToToggle) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setOpaque(false);

        // Header panel that you click to expand/collapse
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
        
        // When header is clicked, show/hide the content
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = !contentPanelToToggle.isVisible();
                contentPanelToToggle.setVisible(isVisible);
                arrowLabel.setText(isVisible ? "\u2303  " : "\u2304  "); // Change arrow direction
                SwingUtilities.getWindowAncestor(AdminAddUserPanel.this).revalidate();
                SwingUtilities.getWindowAncestor(AdminAddUserPanel.this).repaint();
            }
        });

        sectionPanel.add(headerPanel, BorderLayout.NORTH);
        sectionPanel.add(contentPanelToToggle, BorderLayout.CENTER);
        
        return sectionPanel;
    }

    // Form for deleting users with dropdown selection
    private JPanel createDeleteUserForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        deleteUserSelect = new JComboBox<>();
        deleteUserSelect.setEditable(false);
        deleteUserSelect.setRenderer(new UserComboBoxRenderer());
        deleteUserSelect.setToolTipText("Select Username to delete");

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Select User to DELETE:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deleteUserSelect, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        JButton refreshButton = createSaveButton("Refresh");
        refreshButton.addActionListener(e -> loadAllUsernames());
        formPanel.add(refreshButton, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        JButton deleteButton = createDeleteButton("DELETE USER");
        deleteButton.addActionListener(e -> handleDeleteUser());
        formPanel.add(deleteButton, gbc);

        return formPanel;
    }

    // Handles the actual user deletion when button is clicked
    private void handleDeleteUser() {
        String selectedItem = (String) deleteUserSelect.getSelectedItem();

        // Check if a valid user is selected
        if (selectedItem == null || selectedItem.startsWith("Select User") || selectedItem.startsWith("Loading")) {
            JOptionPane.showMessageDialog(this, "Please select a valid user to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract username and role from selection
        String usernameToDelete = selectedItem.split(" \\(")[0];
        String role = selectedItem.contains("(Admin)") ? "Admin" : (selectedItem.contains("(Instructor)") ? "Instructor" : "Student");

        // Show confirmation dialog before deleting
        int confirm = JOptionPane.showConfirmDialog(this,
                "WARNING: This will permanently delete user " + usernameToDelete + " (" + role + ") and all their records. Are you sure?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return adminApi.deleteUser(usernameToDelete); 
                }
                @Override
                protected void done() {
                    try {
                        String result = get();
                        JOptionPane.showMessageDialog(AdminAddUserPanel.this, result, "Deletion Status", JOptionPane.INFORMATION_MESSAGE);
                        if (result.startsWith("Success")) {
                            loadAllUsernames(); // Refresh the user list
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AdminAddUserPanel.this, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    // Loads all usernames from the database for the delete dropdown
    private void loadAllUsernames() {
        deleteUserSelect.removeAllItems();
        deleteUserSelect.addItem("Loading users...");
        
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                // Get all users from database
                List<String> students = adminApi.getAllStudents().stream().map(s -> s.getUsername() + " (Student)").collect(Collectors.toList());
                List<String> instructors = adminApi.getAllInstructors().stream().map(i -> i.getUsername() + " (Instructor)").collect(Collectors.toList());
                List<String> admins = adminApi.getAllAdmins().stream()
                                      .map(a -> a.getUsername() + " (Admin)")
                                      .collect(Collectors.toList());

                // Combine all users
                List<String> allUsers = new ArrayList<>(students);
                allUsers.addAll(instructors);
                allUsers.addAll(admins);

                allUsers.remove(session.getUsername() + " (Admin)"); // Can't delete yourself
                return allUsers;
            }

            @Override
            protected void done() {
                deleteUserSelect.removeAllItems();
                try {
                    List<String> usernames = get();

                    deleteUserSelect.addItem("Select User to DELETE"); 
                    
                    if (usernames.isEmpty()) {
                        deleteUserSelect.addItem("No users found.");
                    } else {
                        usernames.forEach(deleteUserSelect::addItem);
                    }
                    deleteUserSelect.setSelectedIndex(0);
                    
                } catch (Exception e) {
                    deleteUserSelect.addItem("Error loading users.");
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // Makes admin users show up in red color in dropdown
    private static class UserComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof String) {
                String user = (String) value;

                if (user.contains("(Admin)")) {
                    setForeground(DELETE_USER_COLOR); // Red color for admins
                } else {
                    setForeground(list.getForeground()); 
                }

                if (index == -1 && user.contains(" (")) {
                     setText(user.split(" \\(")[0]); // Show only username when selected
                }
            }
            return this;
        }
    }

    // Form for creating new students
    private JPanel createStudentForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        studentNameField = createTextField();
        studentUserField = createTextField();
        studentPassField = createPasswordField();
        studentRollNoField = createTextField();
        studentProgramField = createTextField();
        studentYearField = createTextField();

        // Add all the student fields to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(studentNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Username (Login ID):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(studentUserField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapPasswordWithEye(studentPassField), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Roll No:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(studentRollNoField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Program (e.g. B.Tech CS):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(studentProgramField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Year (e.g. 1, 2):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(studentYearField, gbc);

        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = createSaveButton("Create Student");
        saveButton.addActionListener(e -> createStudent());
        formPanel.add(saveButton, gbc);

        return formPanel;
    }

    // Form for creating new instructors
    private JPanel createInstructorForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        instNameField = createTextField();
        instUserField = createTextField();
        instPassField = createPasswordField();
        instDeptField = createTextField();

        // Add instructor fields to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(instNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Username (Login ID):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(instUserField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapPasswordWithEye(instPassField), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(instDeptField, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = createSaveButton("Create Instructor");
        saveButton.addActionListener(e -> createInstructor());
        formPanel.add(saveButton, gbc);

        return formPanel;
    }

    // Form for creating new admins
    private JPanel createAdminForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        adminNameField = createTextField();
        adminUserField = createTextField();
        adminPassField = createPasswordField();

        // Add admin fields to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(adminNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Username (Login ID):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(adminUserField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapPasswordWithEye(adminPassField), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = createSaveButton("Create Admin");
        saveButton.addActionListener(e -> createAdmin());
        formPanel.add(saveButton, gbc);

        return formPanel;
    }

    // Actually creates a student in the database
    private void createStudent() {
        String name = studentNameField.getText();
        String user = studentUserField.getText();
        String pass = new String(studentPassField.getPassword());
        String roll = studentRollNoField.getText();
        String prog = studentProgramField.getText();
        int year;
        try {
            year = Integer.parseInt(studentYearField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminApi.createStudent(user, pass, name, roll, prog, year);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminAddUserPanel.this, result, "Create Student Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        // Clear form after successful creation
                        studentNameField.setText("");
                        studentUserField.setText("");
                        studentPassField.setText("");
                        studentRollNoField.setText("");
                        studentProgramField.setText("");
                        studentYearField.setText("");
                        loadAllUsernames(); // Refresh delete list
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAddUserPanel.this, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    // Actually creates an instructor in the database
    private void createInstructor() {
        String name = instNameField.getText();
        String user = instUserField.getText();
        String pass = new String(instPassField.getPassword());
        String dept = instDeptField.getText();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminApi.createInstructor(user, pass, name, dept);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminAddUserPanel.this, result, "Create Instructor Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        // Clear form after success
                        instNameField.setText("");
                        instUserField.setText("");
                        instPassField.setText("");
                        instDeptField.setText("");
                        loadAllUsernames(); // Refresh delete list
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAddUserPanel.this, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    // Actually creates an admin in the database
    private void createAdmin() {
        String name = adminNameField.getText();
        String user = adminUserField.getText();
        String pass = new String(adminPassField.getPassword());

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminApi.createAdmin(user, pass, name);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AdminAddUserPanel.this, result, "Create Admin Status", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        // Clear form after success
                        adminNameField.setText("");
                        adminUserField.setText("");
                        adminPassField.setText("");
                        loadAllUsernames(); // Refresh delete list
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAddUserPanel.this, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Helper method to create styled labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    // Helper method to create styled text fields
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    // Wraps password field with eye icon to show/hide password
    private JPanel wrapPasswordWithEye(JPasswordField pf) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        wrapper.add(pf, BorderLayout.CENTER);

        // Eye icon for showing/hiding password
        JLabel eye = new JLabel("\uD83D\uDC41");
        eye.setFont(new Font("SansSerif", Font.PLAIN, 18));
        eye.setForeground(Color.WHITE);
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.setBorder(new EmptyBorder(0, 10, 0, 5));

        eye.addMouseListener(new MouseAdapter() {
            boolean visible = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                visible = !visible;

                if (visible) {
                    pf.setEchoChar((char)0); // Show password
                    eye.setText("\uD83D\uDC41\uFE0F");
                } else {
                    pf.setEchoChar('•'); // Hide password
                    eye.setText("\uD83D\uDC41");
                }
            }
        });

        wrapper.add(eye, BorderLayout.EAST);
        return wrapper;
    }

    // Helper method to create styled password fields
    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setFont(new Font("SansSerif", Font.PLAIN, 16));
        pf.setBackground(new Color(60, 60, 60));
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return pf;
    }
    
    // Helper method to create green save buttons
    private JButton createSaveButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(new Color(80, 140, 80)); // Green color
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Panel with rounded corners for section headers
    private static class RoundedPanel extends JPanel {
        private Color backgroundColor;
        private int cornerRadius;

        public RoundedPanel(Color bgColor, int radius) {
            super();
            this.backgroundColor = bgColor;
            this.cornerRadius = radius;
            setOpaque(false);
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

    // Helper method to create red delete buttons
    private JButton createDeleteButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(new Color(200, 50, 50)); // Red color
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}