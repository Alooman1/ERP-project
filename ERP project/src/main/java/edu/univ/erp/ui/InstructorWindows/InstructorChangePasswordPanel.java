package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.auth.AuthApi;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.api.admin.AdminApi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

// This panel lets instructors change their password
public class InstructorChangePasswordPanel extends JPanel {

    private final UserSession session;
    private final AuthApi authApi;
    private final AdminApi adminApi;

    // Password fields for old and new passwords
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField retypePasswordField;
    private JButton saveButton;

    private final Color fieldColor = new Color(60, 60, 60);
    private final Color textColor = Color.WHITE;

    public InstructorChangePasswordPanel(UserSession session) {
        this.session = session;
        this.authApi = new AuthApi();
        this.adminApi = new AdminApi();
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(25, 40, 25, 40));
        
        // Main form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(400, 400));

        Font titleFont = new Font("SansSerif", Font.BOLD, 36);

        // Title label
        JLabel titleLabel = new JLabel("CHANGE PASSWORD");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Old password field
        formPanel.add(createLabel("Old Password"));
        oldPasswordField = createPasswordField();
        formPanel.add(createPasswordPanelWithToggle(oldPasswordField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // New password field
        formPanel.add(createLabel("New Password"));
        newPasswordField = createPasswordField();
        formPanel.add(createPasswordPanelWithToggle(newPasswordField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Retype password field
        formPanel.add(createLabel("Retype New Password"));
        retypePasswordField = createPasswordField();
        formPanel.add(createPasswordPanelWithToggle(retypePasswordField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Save button
        saveButton = new JButton("SAVE");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setOpaque(true);
        saveButton.setBackground(new Color(80, 80, 80));
        saveButton.setForeground(textColor);
        formPanel.add(saveButton);

        addSaveButtonListener();

        add(formPanel);
    }

    // Create label for password fields
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    // Create password field with styling
    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pf.setForeground(textColor);
        pf.setBackground(fieldColor);
        pf.setCaretColor(textColor);
        return pf;
    }

    // Create password field with show/hide toggle button
    private JPanel createPasswordPanelWithToggle(JPasswordField pf) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(400, 40));
        panel.setBackground(fieldColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        pf.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(pf, BorderLayout.CENTER);

        // Eye button to show/hide password
        JToggleButton showPasswordButton = new JToggleButton("\uD83D\uDC41"); // Eye Emoji
        showPasswordButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        showPasswordButton.setOpaque(false);
        showPasswordButton.setForeground(new Color(150, 180, 255));
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Toggle password visibility
        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.isSelected()) {
                pf.setEchoChar((char) 0); // Show password
            } else {
                pf.setEchoChar('•'); // Hide password
            }
        });
        panel.add(showPasswordButton, BorderLayout.EAST);
        return panel;
    }

    // Handle save button click
    private void addSaveButtonListener() {
        saveButton.addActionListener(e -> {
            // Check if system is in maintenance mode
            if (adminApi.isMaintenanceModeOn()) {
                showError("System is in maintenance. Password cannot be changed.");
                return;
            }
            
            // Get passwords from fields
            final String oldPass = new String(oldPasswordField.getPassword());
            final String newPass = new String(newPasswordField.getPassword());
            final String retypePass = new String(retypePasswordField.getPassword());

            // Validate inputs
            if (oldPass.isEmpty() || newPass.isEmpty() || retypePass.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            if (!newPass.equals(retypePass)) {
                showError("New passwords do not match.");
                return;
            }
            if (oldPass.equals(newPass)) {
                showError("New password cannot be the same as the old password.");
                return;
            }

            // Send password change request to server
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return authApi.changePassword(session.getUserId(), oldPass, newPass);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccess("Password changed successfully!");
                            // Clear fields after success
                            oldPasswordField.setText("");
                            newPasswordField.setText("");
                            retypePasswordField.setText("");
                        } else {
                            showError("Password change failed. Please check your old password.");
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        showError("An unexpected error occurred during save.");
                    }
                }
            };
            worker.execute();
        });
    }

    // Show error message
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Show success message
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}