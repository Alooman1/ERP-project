package edu.univ.erp.ui.CommonWindows;

import edu.univ.erp.auth.AuthApi;
import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.ui.AdminWindows.AdminProfileWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ChangePasswordDialog extends JDialog {

    private final UserSession session;
    private final AuthApi authApi;
    private final AdminApi adminApi;
    
    // Password input fields
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField retypePasswordField;
    private JButton saveButton;

    // Color scheme for dark theme
    private final Color bgColor = new Color(50, 50, 50);
    private final Color fieldColor = new Color(60, 60, 60);
    private final Color textColor = Color.WHITE;

    public ChangePasswordDialog(JFrame parent, UserSession session) {
        super(parent, "Change Password", true); // Modal dialog
        this.session = session;
        this.authApi = new AuthApi();
        this.adminApi = new AdminApi();

        setSize(450, 450);
        setLocationRelativeTo(parent); // Center on parent window
        setResizable(false);

        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        setupListeners(); // Connect button actions
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Padding around edges

        // Title at top
        JLabel titleLabel = new JLabel("CHANGE PASSWORD");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form with password fields
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        formPanel.add(createLabel("Old Password"));
        formPanel.add(createPasswordFieldWithToggle("old")); // With show/hide eye
        
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(createLabel("New Password"));
        formPanel.add(createPasswordFieldWithToggle("new"));

        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(createLabel("Retype New Password"));
        formPanel.add(createPasswordFieldWithToggle("retype"));

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(80, 80, 80));
        closeButton.setForeground(textColor);
        closeButton.addActionListener(e -> dispose()); // Close dialog
        buttonPanel.add(closeButton);

        saveButton = new JButton("Save Password");
        saveButton.setBackground(new Color(80, 140, 80)); // Green color
        saveButton.setForeground(textColor);
        buttonPanel.add(saveButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Create styled label for form
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(new Color(180, 180, 180)); // Light gray
        label.setBorder(new EmptyBorder(5, 0, 5, 0)); // Spacing
        return label;
    }

    // Create password field with show/hide toggle button
    private JPanel createPasswordFieldWithToggle(String fieldKey) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(450, 40));

        JPasswordField pf = new JPasswordField(20);
        pf.setFont(new Font("SansSerif", Font.PLAIN, 16));
        pf.setForeground(textColor);
        pf.setBackground(fieldColor);
        pf.setCaretColor(textColor); // Cursor color
        pf.setBorder(new EmptyBorder(5, 10, 5, 5));
        
        // Assign to correct field variable
        switch (fieldKey) {
            case "old": oldPasswordField = pf; break;
            case "new": newPasswordField = pf; break;
            case "retype": retypePasswordField = pf; break;
        }
        
        panel.setBackground(fieldColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Border around field
        panel.add(pf, BorderLayout.CENTER);

        // Eye button to show/hide password
        JToggleButton toggle = new JToggleButton("\uD83D\uDC41"); // Eye emoji
        toggle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toggle.setOpaque(false);
        toggle.setForeground(new Color(150, 180, 255)); // Light blue
        toggle.setContentAreaFilled(false);
        toggle.setBorderPainted(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Toggle between showing dots or actual text
        toggle.addActionListener(e -> {
            if (toggle.isSelected()) {
                pf.setEchoChar((char) 0); // Show password
            } else {
                pf.setEchoChar('•'); // Hide password with dots
            }
        });
        panel.add(toggle, BorderLayout.EAST);
        return panel;
    }

    // Connect save button to action
    private void setupListeners() {
        saveButton.addActionListener(e -> handleSavePassword());
    }

    // Handle password change when save is clicked
    private void handleSavePassword() {
        // Get passwords from fields
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String retypePass = new String(retypePasswordField.getPassword());

        // Validate inputs
        if (oldPass.isEmpty() || newPass.isEmpty() || retypePass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPass.equals(retypePass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (oldPass.equals(newPass)) {
            JOptionPane.showMessageDialog(this, "New password cannot be the same as the old password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if system is in maintenance mode
        if (adminApi.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this, "System is in maintenance. Password cannot be changed.", "Maintenance Mode", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Change password in background thread
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
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Clear fields after success
                        oldPasswordField.setText("");
                        newPasswordField.setText("");
                        retypePasswordField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this, "Password change failed. Please check your old password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(ChangePasswordDialog.this, "An unexpected error occurred during save.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}