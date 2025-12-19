package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDeleteDataPanel extends JPanel {

    private final AdminApi adminApi;

    // Main constructor - sets up data deletion panel
    public AdminDeleteDataPanel(UserSession session) {
        this.adminApi = new AdminApi();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Main title
        JLabel titleLabel = new JLabel("MANAGE DATA DELETION");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Section for deleting users
        mainPanel.add(createDeletionSection("Delete User (Student/Instructor/Admin)", 
            "Enter Username to DELETE:", "DELETE USER", this::handleDeleteUser));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Section for deleting courses
        mainPanel.add(createDeletionSection("Delete Course and All Sections", 
            "Enter Course Code (e.g. CS101):", "DELETE COURSE", this::handleDeleteCourse));

        mainPanel.add(Box.createVerticalGlue());
        add(mainPanel, BorderLayout.CENTER);
    }

    // Creates a deletion section with title, input field and button
    private JPanel createDeletionSection(String title, String prompt, String buttonText, Runnable action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section title in red
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.RED);
        panel.add(titleLabel);

        // Input field for username/course code
        JTextField inputField = new JTextField(20);
        inputField.setToolTipText(prompt);

        // Delete button with confirmation
        JButton deleteButton = new JButton(buttonText);
        deleteButton.setBackground(new Color(220, 50, 50)); // Red color
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> {
            // Show confirmation dialog before deleting
            int confirm = JOptionPane.showConfirmDialog(this, "WARNING: This is permanent. Proceed?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                action.run(); // Run the delete action
            }
        });
        return panel;
    }

    // Handles user deletion (currently shows message that it's not implemented)
    private void handleDeleteUser() {
        JOptionPane.showMessageDialog(this, "User deletion logic is complex (FK dependencies). This function is marked for completion.", "Feature To Be Completed", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Handles course deletion (currently shows message that it's not implemented)
    private void handleDeleteCourse() {
        JOptionPane.showMessageDialog(this, "Course deletion logic is complex (FK dependencies). This function is marked for completion.", "Feature To Be Completed", JOptionPane.INFORMATION_MESSAGE);
    }
}