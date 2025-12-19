package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.api.admin.AdminApi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// This window shows and lets instructors edit their profile
public class InstructorProfileWindow extends JDialog {

    private final UserSession session;
    private final InstructorApi instructorApi;
    private final InstructorDashboard dashboardToRefresh;
    private final AdminApi adminApi;
    
    // Fields for profile information
    private JTextField nameField;
    private JLabel departmentLabel;
    private JLabel usernameLabel;
    
    // Colors for the dark theme
    private final Color bgColor = new Color(50, 50, 50);
    private final Color fieldColor = new Color(60, 60, 60);
    private final Color labelColor = Color.LIGHT_GRAY;
    private final Color textColor = Color.WHITE;

    public InstructorProfileWindow(JFrame parent, UserSession session, InstructorDashboard dashboard) {
        super(parent, "My Profile", true); // Modal dialog (blocks other windows)
        this.session = session;
        this.instructorApi = new InstructorApi(session);
        this.dashboardToRefresh = dashboard;
        this.adminApi = new AdminApi();
        
        setSize(400, 300);
        setLocationRelativeTo(parent); // Center on parent window
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

        // Panel for profile information
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        nameField = createTextField();
        infoPanel.add(nameField, gbc);

        // Department field (read-only)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        departmentLabel = createInfoLabel("[Fetching...]");
        infoPanel.add(departmentLabel, gbc);
        
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Save and Close buttons
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(80, 140, 80)); // Green
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
        closeButton.addActionListener(e -> dispose()); // Close window
        saveButton.addActionListener(e -> handleSave()); // Save changes

        loadProfileData(); // Load profile when window opens
    }

    // Create label for profile fields
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(labelColor);
        return label;
    }

    // Create text field for editing
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(fieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor); // Cursor color
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5) // Padding inside field
        ));
        return field;
    }

    // Load profile data from server
    private void loadProfileData() {
        SwingWorker<Instructor, Void> worker = new SwingWorker<>() {
            @Override
            protected Instructor doInBackground() throws Exception {
                // Get instructor profile from server
                return instructorApi.getInstructorProfile(session.getUserId());
            }

            @Override
            protected void done() {
                try {
                    Instructor profile = get();
                    if (profile != null) {
                        // Fill fields with profile data
                        nameField.setText(profile.getFullName());
                        departmentLabel.setText(profile.getDepartment());
                    } else {
                        nameField.setText("Not Found");
                        departmentLabel.setText("Not Found");
                    }
                    setVisible(true); // Show window after loading
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    nameField.setText("Error loading profile.");
                    setVisible(true);
                }
            }
        };
        worker.execute();
    }

    // Handle saving profile changes
    private void handleSave() {
        // Check if system is in maintenance mode
        if (adminApi.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this, "System is in maintenance. Profile cannot be updated.", "Maintenance Mode", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newName = nameField.getText();
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Send name update to server
                return instructorApi.updateInstructorName(session.getUserId(), newName);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(InstructorProfileWindow.this, "Name updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh dashboard to show new name
                        dashboardToRefresh.refreshHomePageData();
                        
                        dispose(); // Close window after success
                    } else {
                        JOptionPane.showMessageDialog(InstructorProfileWindow.this, "Failed to update name.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(InstructorProfileWindow.this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}