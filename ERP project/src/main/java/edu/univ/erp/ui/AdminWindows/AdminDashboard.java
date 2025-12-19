package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Admin;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.ui.CommonWindows.LoginWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    private UserSession session;
    private AdminApi adminApi;

    // Colors for the UI
    private final Color NAV_COLOR = new Color(50, 50, 50);
    private final Color NAV_TEXT_COLOR = Color.WHITE;
    private final Color NAV_ACTIVE_BG = Color.WHITE;
    private final Color NAV_ACTIVE_TEXT = Color.DARK_GRAY;
    private final Color HEADER_COLOR = new Color(70, 70, 70);

    // Main UI components
    private JPanel navPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<JButton> navButtons = new ArrayList<>();

    // Different panels for different admin functions
    private AdminAddUserPanel addUserPanel;
    private AdminCoursesPanel coursesPanel;
    private AdminAssignInstructorPanel assignPanel;
    private AdminMaintenancePanel maintenancePanel;
    private AdminBackupRestorePanel backupPanel;
    private AdminViewTimetablesPanel timetablesPanel;
    private AdminSendNotificationPanel sendNotificationPanel;
    private AdminChangePasswordPanel changePasswordPanel;

    // Header components
    private JLabel welcomeLabel;
    private JLabel profileIcon;
    private JLabel maintenanceBanner; // Shows when maintenance mode is on

    // Navigation buttons
    private JButton homeButton;
    private JButton addUsersButton;
    private JButton coursesButton;
    private JButton assignButton;
    private JButton maintenanceButton;
    private JButton backupButton;
    private JButton notifyButton;
    private JButton timetablesButton;
    private JButton changePassButton;
    private JButton logoutButton;

    private boolean isNavMenuExpanded = true;

    // Main constructor - sets up the entire admin dashboard
    public AdminDashboard(UserSession session) {
        this.session = session;
        this.adminApi = new AdminApi();

        setTitle("Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new BackgroundPanel()); // Set background image
        getContentPane().setLayout(new BorderLayout());

        this.navPanel = createNavPanel();
        add(this.navPanel, BorderLayout.WEST);

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setOpaque(false);

        // Create header (which includes the banner)
        mainWrapper.add(createHeaderPanel(), BorderLayout.NORTH);

        this.contentPanel = createContentPanel();
        mainWrapper.add(this.contentPanel, BorderLayout.CENTER);

        add(mainWrapper, BorderLayout.CENTER);

        refreshHomePageData(); // Load user data and maintenance status
        setVisible(true);
    }

    // Creates the header with menu icon and maintenance banner
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Menu icon that toggles navigation panel
        JLabel menuIcon = new JLabel("\u2630  MENU");
        menuIcon.setFont(new Font("SansSerif", Font.BOLD, 20));
        menuIcon.setForeground(Color.WHITE);
        menuIcon.setBorder(new EmptyBorder(5, 10, 5, 20));
        menuIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleNavMenu();
            }
        });
        headerPanel.add(menuIcon, BorderLayout.WEST);

        // Maintenance banner (hidden by default)
        maintenanceBanner = new JLabel("MAINTENANCE MODE IS ON. ALL CHANGES ARE DISABLED.");
        maintenanceBanner.setFont(new Font("SansSerif", Font.BOLD, 16));
        maintenanceBanner.setForeground(Color.BLACK);
        maintenanceBanner.setBackground(Color.ORANGE);
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setHorizontalAlignment(SwingConstants.CENTER);
        maintenanceBanner.setBorder(new EmptyBorder(5, 0, 5, 0));
        maintenanceBanner.setVisible(false);

        headerPanel.add(maintenanceBanner, BorderLayout.CENTER);

        return headerPanel;
    }

    /**
     * Shows or hides the maintenance banner
     * Called when maintenance mode is toggled
     */
    public void updateMaintenanceBanner(boolean isNowOn) {
        if (maintenanceBanner != null) {
            maintenanceBanner.setVisible(isNowOn);
            // Refresh the display
            maintenanceBanner.getParent().revalidate();
            maintenanceBanner.getParent().repaint();
        }
    }

    // Creates the navigation panel on the left side
    private JPanel createNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(NAV_COLOR);
        navPanel.setPreferredSize(new Dimension(300, 0));
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Create all navigation buttons
        homeButton           = createNavButton("HOME");
        addUsersButton       = createNavButton("ADD USERS");
        coursesButton        = createNavButton("CREATE/EDIT COURSES");
        assignButton         = createNavButton("ASSIGN INSTRUCTOR");
        maintenanceButton    = createNavButton("TOGGLE MAINTENANCE");
        backupButton         = createNavButton("BACKUP & RESTORE");
        notifyButton         = createNavButton("SEND NOTIFICATIONS");
        timetablesButton     = createNavButton("VIEW TIMETABLES");
        changePassButton     = createNavButton("CHANGE PASSWORD");
        logoutButton         = createNavButton("LOGOUT");

        // Connect buttons to show different panels
        homeButton.addActionListener(e -> { cardLayout.show(contentPanel, "HOME"); setActiveButton(homeButton); });
        addUsersButton.addActionListener(e -> { cardLayout.show(contentPanel, "ADD_USERS"); setActiveButton(addUsersButton); });
        coursesButton.addActionListener(e -> { cardLayout.show(contentPanel, "COURSES"); setActiveButton(coursesButton); });
        assignButton.addActionListener(e -> { cardLayout.show(contentPanel, "ASSIGN"); setActiveButton(assignButton); });
        maintenanceButton.addActionListener(e -> { cardLayout.show(contentPanel, "MAINTENANCE"); setActiveButton(maintenanceButton); });
        backupButton.addActionListener(e -> { cardLayout.show(contentPanel, "BACKUP"); setActiveButton(backupButton); });
        notifyButton.addActionListener(e -> { cardLayout.show(contentPanel, "NOTIFY"); setActiveButton(notifyButton); });
        timetablesButton.addActionListener(e -> { cardLayout.show(contentPanel, "TIMETABLES"); setActiveButton(timetablesButton); });
        changePassButton.addActionListener(e -> { cardLayout.show(contentPanel, "CHANGE_PASSWORD"); setActiveButton(changePassButton); });

        // Logout button with confirmation
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    AdminDashboard.this,
                    "Are you sure you want to log out?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose(); // Close dashboard
                new LoginWindow(); // Go back to login
            }
        });

        // Keep track of all buttons for highlighting active one
        navButtons.addAll(List.of(
                homeButton, addUsersButton, coursesButton, assignButton,
                maintenanceButton, backupButton, notifyButton,
                timetablesButton, changePassButton, logoutButton
        ));

        // Add all buttons to navigation panel
        navPanel.add(homeButton);
        navPanel.add(addUsersButton);
        navPanel.add(coursesButton);
        navPanel.add(assignButton);
        navPanel.add(maintenanceButton);
        navPanel.add(backupButton);
        navPanel.add(notifyButton);
        navPanel.add(timetablesButton);
        navPanel.add(changePassButton);
        navPanel.add(logoutButton);

        navPanel.add(Box.createVerticalGlue());

        // Profile section at bottom of navigation
        profileIcon = new JLabel("  \uD83D\uDC64  " + session.getUsername());
        profileIcon.setFont(new Font("SansSerif", Font.BOLD, 16));
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileIcon.setHorizontalAlignment(SwingConstants.LEFT);
        profileIcon.setBorder(new EmptyBorder(20, 10, 20, 10));
        profileIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open profile window when clicked
                new edu.univ.erp.ui.AdminWindows.AdminProfileWindow(
                        AdminDashboard.this,
                        session,
                        AdminDashboard.this
                );
            }
        });
        navPanel.add(profileIcon);

        setActiveButton(homeButton); // Start with home button active
        return navPanel;
    }

    // Creates styled navigation buttons
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBorder(new EmptyBorder(15, 20, 15, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Change color when mouse hovers over button
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (button.getBackground() != NAV_ACTIVE_BG) {
                    button.setBackground(new Color(70, 70, 70));
                }
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                if (button.getBackground() != NAV_ACTIVE_BG) {
                    button.setBackground(NAV_COLOR);
                }
            }
        });

        return button;
    }

    // Highlights the active navigation button
    private void setActiveButton(JButton activeButton) {
        for (JButton button : navButtons) {
            if (button == activeButton) {
                button.setBackground(NAV_ACTIVE_BG); // White background
                button.setForeground(NAV_ACTIVE_TEXT); // Dark text
            } else {
                button.setBackground(NAV_COLOR); // Dark background
                button.setForeground(NAV_TEXT_COLOR); // White text
            }
        }
    }

    // Shows or hides the navigation menu
    private void toggleNavMenu() {
        isNavMenuExpanded = !isNavMenuExpanded;
        navPanel.setVisible(isNavMenuExpanded);
        revalidate();
        repaint();
    }

    // Creates the main content area that shows different panels
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(createHomePanel(), "HOME");

        // Create all the different admin panels
        addUserPanel        = new AdminAddUserPanel(session);
        coursesPanel        = new AdminCoursesPanel(session);
        assignPanel         = new AdminAssignInstructorPanel(session);
        backupPanel         = new AdminBackupRestorePanel(session);
        sendNotificationPanel = new AdminSendNotificationPanel(session);
        timetablesPanel     = new AdminViewTimetablesPanel(session);
        changePasswordPanel = new AdminChangePasswordPanel(session);

        // Pass 'this' dashboard to MaintenancePanel so it can update the banner
        maintenancePanel = new AdminMaintenancePanel(session, this);

        // Add all panels to content area
        contentPanel.add(addUserPanel, "ADD_USERS");
        contentPanel.add(coursesPanel, "COURSES");
        contentPanel.add(assignPanel, "ASSIGN");
        contentPanel.add(maintenancePanel, "MAINTENANCE");
        contentPanel.add(backupPanel, "BACKUP");
        contentPanel.add(sendNotificationPanel, "NOTIFY");
        contentPanel.add(timetablesPanel, "TIMETABLES");
        contentPanel.add(changePasswordPanel, "CHANGE_PASSWORD");

        return contentPanel;
    }

    // Creates the home panel with welcome message
    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        welcomeLabel = new JLabel("GOOD MORNING, " + session.getUsername().toUpperCase());
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);

        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        panel.add(createInfoLabel("Admin Dashboard"));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Loads user profile and maintenance status from database
    public void refreshHomePageData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private Admin profile;
            private boolean maintenanceOn;

            @Override
            protected Void doInBackground() throws Exception {
                // Get data from database
                profile = adminApi.getAdminProfile(session.getUserId());
                maintenanceOn = adminApi.isMaintenanceModeOn();
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (profile != null && profile.getFullName() != null) {
                        welcomeLabel.setText("GOOD MORNING, " + profile.getFullName().toUpperCase());
                    } else {
                        welcomeLabel.setText("GOOD MORNING, " + session.getUsername().toUpperCase());
                    }
                    updateMaintenanceBanner(maintenanceOn); // Show/hide maintenance banner
                } catch (Exception e) {
                    e.printStackTrace();
                    welcomeLabel.setText("Error loading profile.");
                }
            }
        };
        worker.execute();
    }

    // Helper to create info labels
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setForeground(Color.LIGHT_GRAY);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    // Panel with background image
    private class BackgroundPanel extends JPanel {
        private BufferedImage image;

        public BackgroundPanel() {
            try {
                // Load background image
                image =ImageIO.read(getClass().getClassLoader().getResourceAsStream("student_bg.jpg"));
            } catch (IOException e) {
                System.err.println("Could not read background image 'student_bg.jpg'");
                image = null;
            }
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }
            // Add dark overlay so text is readable
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
}