package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.UserSession;
// --- ADD THIS IMPORT ---
import edu.univ.erp.ui.StudentWindows.StudentDashboard;
import edu.univ.erp.api.admin.AdminApi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfileWindow extends JDialog {

    private final UserSession session;
    private final StudentApi studentApi;

    private final StudentDashboard dashboardToRefresh; // Reference to the main dashboard
    private final AdminApi adminApi;

    // Profile form fields
    private JTextField nameField;
    private JLabel rollNoLabel;
    private JLabel programLabel;
    private JLabel yearLabel;

    // Color scheme for dark theme
    private final Color bgColor = new Color(50, 50, 50);
    private final Color fieldColor = new Color(60, 60, 60);
    private final Color labelColor = Color.LIGHT_GRAY;
    private final Color textColor = Color.WHITE;

    public ProfileWindow(JFrame parent, UserSession session, StudentDashboard dashboard) {
        super(parent, "My Profile", true);
        this.session = session;
        this.studentApi = new StudentApi();
        this.dashboardToRefresh = dashboard;
        this.adminApi = new AdminApi();

        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Title with username
        JLabel titleLabel = new JLabel(session.getUsername());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Information form panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field (editable)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        nameField = createTextField();
        infoPanel.add(nameField, gbc);

        // Roll number (read-only)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Roll No:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        rollNoLabel = createInfoLabel("[Fetching...]");
        infoPanel.add(rollNoLabel, gbc);

        // Program (read-only)
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Program:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        programLabel = createInfoLabel("[Fetching...]");
        infoPanel.add(programLabel, gbc);

        // Year (read-only)
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Year:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        yearLabel = createInfoLabel("[Fetching...]");
        infoPanel.add(yearLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Action buttons
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(80, 140, 80));
        saveButton.setForeground(Color.WHITE);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(80, 80, 80));
        closeButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        closeButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> handleSave());

        // Load profile data when window opens
        loadProfileData();
    }

    // Create styled label
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(labelColor);
        return label;
    }

    // Create styled text field
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(fieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    // Load student profile from server
    private void loadProfileData() {
        SwingWorker<Student, Void> worker = new SwingWorker<>() {
            @Override
            protected Student doInBackground() throws Exception {
                return studentApi.getStudentProfile(session.getUserId());
            }

            @Override
            protected void done() {
                try {
                    Student profile = get();
                    if (profile != null) {
                        // Fill form with student data
                        nameField.setText(profile.getFullName());
                        rollNoLabel.setText(profile.getRollNo());
                        programLabel.setText(profile.getProgram());
                        yearLabel.setText(String.valueOf(profile.getYear()));
                    } else {
                        // Show error if profile not found
                        nameField.setText("Not Found");
                        rollNoLabel.setText("Not Found");
                        programLabel.setText("Not Found");
                        yearLabel.setText("Not Found");
                    }
                    setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    nameField.setText("Error loading profile.");
                    setVisible(true);
                }
            }
        };
        worker.execute();
    }

    // Handle save button click
    private void handleSave() {
        // Check if system is in maintenance mode
        if (adminApi.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this, "System is in maintenance. Profile cannot be updated.", "Maintenance Mode", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String newName = nameField.getText();

        // Update name in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return studentApi.updateStudentName(session.getUserId(), newName);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ProfileWindow.this, "Name updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh dashboard and close window
                        dashboardToRefresh.refreshHomePageData();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ProfileWindow.this, "Failed to update name.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ProfileWindow.this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}