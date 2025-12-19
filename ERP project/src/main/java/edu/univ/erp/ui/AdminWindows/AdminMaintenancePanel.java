package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// This panel lets admins turn maintenance mode on/off to block students and teachers
public class AdminMaintenancePanel extends JPanel {

    private final UserSession session;
    private final AdminApi adminApi;

    // Toggle switch and status display
    private JToggleButton toggleSwitch;
    private JLabel statusLabel;
    private final AdminDashboard dashboardReference;

    public AdminMaintenancePanel(UserSession session, AdminDashboard dashboard) {
        this.session = session;
        this.adminApi = new AdminApi();
        this.dashboardReference = dashboard;

        // Setup main panel
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Big title
        JLabel titleLabel = new JLabel("TOGGLE MAINTENANCE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center area with controls
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Label above toggle
        JLabel modeLabel = new JLabel("MAINTENANCE MODE");
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        modeLabel.setForeground(Color.WHITE);

        // The on/off toggle button
        toggleSwitch = new JToggleButton("OFF");
        toggleSwitch.setFont(new Font("SansSerif", Font.BOLD, 16));
        toggleSwitch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleSwitch.setPreferredSize(new Dimension(100, 40));

        // Shows current status
        statusLabel = new JLabel("Current Status: Loading...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);

        // Update visuals when toggle is clicked
        toggleSwitch.addActionListener(e -> updateToggleVisuals());

        gbc.gridy = 0;
        mainPanel.add(modeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(toggleSwitch, gbc);

        gbc.gridy = 2;
        mainPanel.add(statusLabel, gbc);

        // Button to save the changes
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setOpaque(true);
        saveButton.setBackground(new Color(80, 140, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> handleSave());

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(saveButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Load current maintenance mode status
        loadInitialState();
    }

    // Loads whether maintenance mode is currently on or off
    private void loadInitialState() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminApi.isMaintenanceModeOn();
            }

            @Override
            protected void done() {
                try {
                    boolean isModeOn = get();
                    toggleSwitch.setSelected(isModeOn);
                    updateToggleVisuals();
                } catch (Exception e) {
                    statusLabel.setText("Error loading status.");
                }
            }
        };
        worker.execute();
    }

    // Updates the toggle button colors and status text
    private void updateToggleVisuals() {
        if (toggleSwitch.isSelected()) {
            // Red for ON mode
            toggleSwitch.setText("ON");
            toggleSwitch.setBackground(new Color(220, 50, 50)); // RED
            toggleSwitch.setForeground(Color.WHITE);
            statusLabel.setText("Current Status: ON (Students/Instructors are blocked)");
        } else {
            // Gray for OFF mode
            toggleSwitch.setText("OFF");
            toggleSwitch.setBackground(new Color(80, 80, 80)); // GRAY
            toggleSwitch.setForeground(Color.LIGHT_GRAY);
            statusLabel.setText("Current Status: OFF (System is normal)");
        }
    }

    // Saves the maintenance mode setting to the database
    private void handleSave() {
        boolean newState = toggleSwitch.isSelected();

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminApi.setMaintenanceMode(newState);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(AdminMaintenancePanel.this,
                                "Maintenance mode set to " + (newState ? "ON" : "OFF"),
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Important: Update the main dashboard banner immediately
                        if (dashboardReference != null) {
                            dashboardReference.updateMaintenanceBanner(newState);
                        }
                    } else {
                        JOptionPane.showMessageDialog(AdminMaintenancePanel.this,
                                "Failed to save setting.", "Error", JOptionPane.ERROR_MESSAGE);
                        loadInitialState(); // Revert if failed
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminMaintenancePanel.this,
                            "Error saving changes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}