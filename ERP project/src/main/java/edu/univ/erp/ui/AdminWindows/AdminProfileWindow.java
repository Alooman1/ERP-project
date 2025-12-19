package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Admin;
import edu.univ.erp.domain.UserSession;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// This window shows the admin's profile and lets them update their name
public class AdminProfileWindow extends JDialog {

    private final UserSession session;
    private final AdminApi adminApi;
    private final AdminDashboard dashboardToRefresh;

    // Profile fields
    private JTextField nameField;
    private JLabel usernameLabel;
    private JLabel departmentLabel;

    // Colors for dark theme
    private final Color bgColor = new Color(50, 50, 50);
    private final Color fieldColor = new Color(60, 60, 60);
    private final Color labelColor = Color.LIGHT_GRAY;
    private final Color textColor = Color.WHITE;

    public AdminProfileWindow(JFrame parent, UserSession session, AdminDashboard dashboard) {
        super(parent, "My Profile", true);
        this.session = session;
        this.adminApi = new AdminApi();
        this.dashboardToRefresh = dashboard;

        // Setup window size and position
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Main panel with dark background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Big title showing username
        JLabel titleLabel = new JLabel(session.getUsername());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center area with profile information
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username row (read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        usernameLabel = createInfoLabel(session.getUsername());
        infoPanel.add(usernameLabel, gbc);

        // Full name row (editable)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(createInfoLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        nameField = createTextField();
        infoPanel.add(nameField, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Buttons at bottom
        JButton saveButton = new JButton("Save Profile Name");
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

        // Load the admin's current profile data
        loadProfileData();
    }

    // Creates nicely styled labels
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(labelColor);
        return label;
    }

    // Creates text fields with dark theme
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

    // Loads the admin's profile information from the database
    private void loadProfileData() {
        SwingWorker<Admin, Void> worker = new SwingWorker<>() {
            @Override
            protected Admin doInBackground() throws Exception {
                return adminApi.getAdminProfile(session.getUserId());
            }

            @Override
            protected void done() {
                try {
                    Admin profile = get();
                    if (profile != null) {
                        nameField.setText(profile.getFullName());
                    } else {
                        nameField.setText("Not Found");
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

    // Saves the updated name when save button is clicked
    private void handleSave() {
        String newName = nameField.getText();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminApi.updateAdminName(session.getUserId(), newName);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(AdminProfileWindow.this, "Name updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dashboardToRefresh.refreshHomePageData(); // Refresh the dashboard title
                        dispose(); // Close window
                    } else {
                        JOptionPane.showMessageDialog(AdminProfileWindow.this, "Failed to update name.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminProfileWindow.this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}