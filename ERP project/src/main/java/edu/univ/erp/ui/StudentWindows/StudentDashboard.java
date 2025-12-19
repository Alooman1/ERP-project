package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.RegisteredCourse;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.ui.CommonWindows.LoginWindow;
import edu.univ.erp.api.admin.AdminApi;
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

public class StudentDashboard extends JFrame {

    private UserSession session;
    private StudentApi studentApi;
    private JLabel maintenanceBanner;
    
    // Color scheme for navigation and UI
    private final Color NAV_COLOR = new Color(50, 50, 50);
    private final Color NAV_TEXT_COLOR = Color.WHITE;
    private final Color NAV_ACTIVE_BG = Color.WHITE;
    private final Color NAV_ACTIVE_TEXT = Color.DARK_GRAY;

    private final Color HEADER_COLOR = new Color(70, 70, 70);
    private JPanel navPanel; // The side menu with buttons
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<JButton> navButtons = new ArrayList<>();

    // Different panels for each section of the dashboard
    private CourseCatalogPanel catalogPanel;
    private ChangePasswordPanel changePasswordPanel;
    private GradesPanel gradesPanel;
    private RegisterDropPanel registerDropPanel;
    
    // Home page labels for student info
    private JLabel rollNoLabel;
    private JLabel programLabel;
    private JLabel yearLabel;
    private JPanel courseListPanel;
    private JLabel welcomeLabel;

    // Navigation buttons and profile elements
    private JButton homeButton, catalogButton, registerButton, gradesButton, changePassButton;
    private JLabel profileIcon;
    private JButton logoutButton;
    private boolean isNavMenuExpanded = true;


    public StudentDashboard(UserSession session) {
        this.session = session;
        this.studentApi = new StudentApi();

        setTitle("Student Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background with custom panel
        setContentPane(new BackgroundPanel());
        getContentPane().setLayout(new BorderLayout());

        this.navPanel = createNavPanel();
        add(this.navPanel, BorderLayout.WEST);

        // Main wrapper for header and content
        JPanel mainWrapperPanel = new JPanel(new BorderLayout());
        mainWrapperPanel.setOpaque(false);

        mainWrapperPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        this.contentPanel = createContentPanel();
        mainWrapperPanel.add(this.contentPanel, BorderLayout.CENTER);

        add(mainWrapperPanel, BorderLayout.CENTER);

        // Load initial data for home page
        refreshHomePageData();
        setVisible(true);
    }


    private JPanel createNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(NAV_COLOR);
        navPanel.setPreferredSize(new Dimension(250, 0));
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Create all navigation buttons
        homeButton = createNavButton("HOME");
        catalogButton = createNavButton("COURSE CATALOG");
        registerButton = createNavButton("REGISTER / DROP");
        gradesButton = createNavButton("GRADES");
        changePassButton = createNavButton("CHANGE PASSWORD");
        logoutButton = createNavButton("LOGOUT");

        // Connect buttons to show different panels
        homeButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "HOME");
            setActiveButton(homeButton);
        });
        catalogButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "CATALOG");
            setActiveButton(catalogButton);
        });
        registerButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "REGISTER");
            setActiveButton(registerButton);
        });
        gradesButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "GRADES");
            setActiveButton(gradesButton);
        });
        changePassButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "CHANGE_PASSWORD");
            setActiveButton(changePassButton);
        });
        logoutButton.addActionListener(e -> {
            // Confirm logout with user
            int choice = JOptionPane.showConfirmDialog(
                    StudentDashboard.this, "Are you sure you want to log out?",
                    "LOGOUT", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginWindow(); // Return to login screen
            }
        });

        navButtons.addAll(List.of(homeButton, catalogButton, registerButton, gradesButton, changePassButton, logoutButton));

        // Add buttons to navigation panel
        navPanel.add(homeButton);
        navPanel.add(catalogButton);
        navPanel.add(registerButton);
        navPanel.add(gradesButton);
        navPanel.add(changePassButton);
        navPanel.add(logoutButton);

        navPanel.add(Box.createVerticalGlue());
        
        // Profile icon with username at bottom
        profileIcon = new JLabel("  \uD83D\uDC64  " + session.getUsername());
        profileIcon.setFont(new Font("SansSerif", Font.BOLD, 16));
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileIcon.setHorizontalAlignment(SwingConstants.LEFT);

        profileIcon.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Make profile icon clickable to open profile window
        profileIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ProfileWindow(StudentDashboard.this, session, StudentDashboard.this);
            }
        });

        navPanel.add(profileIcon);

        // Start with home button active
        setActiveButton(homeButton);
        return navPanel;
    }

    // Show/hide navigation menu (for responsive design)
    private void toggleNavMenu() {
        isNavMenuExpanded = !isNavMenuExpanded;
        navPanel.setVisible(isNavMenuExpanded);

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private JPanel createContentPanel() {
        // Use card layout to switch between different panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Add all the different content panels
        contentPanel.add(createHomePanel(), "HOME");

        this.catalogPanel = new CourseCatalogPanel(session);
        contentPanel.add(this.catalogPanel, "CATALOG");

        this.registerDropPanel = new RegisterDropPanel(session,this);
        contentPanel.add(this.registerDropPanel, "REGISTER");

        this.gradesPanel = new GradesPanel(session);
        contentPanel.add(this.gradesPanel, "GRADES");

        this.changePasswordPanel = new ChangePasswordPanel(session);
        contentPanel.add(this.changePasswordPanel, "CHANGE_PASSWORD");

        return contentPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Menu icon to toggle navigation
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

        // Maintenance mode banner (shown when system is in maintenance)
        maintenanceBanner = new JLabel("MAINTENANCE MODE IS ON. ALL CHANGES ARE DISABLED.");
        maintenanceBanner.setFont(new Font("SansSerif", Font.BOLD, 16));
        maintenanceBanner.setForeground(Color.BLACK);
        maintenanceBanner.setBackground(Color.ORANGE); // Bright color for visibility
        maintenanceBanner.setOpaque(true);
        maintenanceBanner.setHorizontalAlignment(SwingConstants.CENTER);
        maintenanceBanner.setBorder(new EmptyBorder(5, 0, 5, 0));
        maintenanceBanner.setVisible(false); // Hide it by default
        headerPanel.add(maintenanceBanner, BorderLayout.CENTER);

        // Notification bell icon
        JLabel notificationIcon = new JLabel("\uD83D\uDD14"); // Bell
        notificationIcon.setFont(new Font("SansSerif", Font.BOLD, 18));
        notificationIcon.setForeground(Color.WHITE);
        notificationIcon.setBorder(new EmptyBorder(5, 10, 10, 15));
        notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Check for unread notifications and show red color if any
        edu.univ.erp.service.NotificationService ns = new edu.univ.erp.service.NotificationService();
        if (ns.hasUnread(session.getUserId())) {
            notificationIcon.setForeground(new Color(255, 100, 100)); // RED if unread
        }

        // Show notification popup when bell is clicked
        notificationIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new edu.univ.erp.ui.CommonWindows.NotificationPopup(
                        StudentDashboard.this,
                        session,
                        (Component)e.getSource(),
                        () -> {
                            // Update bell color after reading notifications
                            if (ns.hasUnread(session.getUserId())) {
                                notificationIcon.setForeground(new Color(255, 100, 100));
                            } else {
                                notificationIcon.setForeground(Color.WHITE);
                            }
                        }
                );
            }
        });

        headerPanel.add(notificationIcon, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        // Welcome message with username
        welcomeLabel = new JLabel("GOOD MORNING, " + session.getUsername().toUpperCase());
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Student information labels (will be filled with real data)
        rollNoLabel = createInfoLabel("Roll No: [Fetching...]");
        programLabel = createInfoLabel("Program: [Fetching...]");
        yearLabel = createInfoLabel("Year: [Fetching...]");

        panel.add(rollNoLabel);
        panel.add(programLabel);
        panel.add(yearLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Section title for registered courses
        JLabel coursesTitle = new JLabel("CURRENT REGISTERED COURSES");
        coursesTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        coursesTitle.setForeground(Color.WHITE);
        panel.add(coursesTitle);

        // Panel to list registered courses
        courseListPanel = new JPanel();
        courseListPanel.setOpaque(false);
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.add(createInfoLabel("  [Fetching courses...]"));

        panel.add(courseListPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Helper class to hold home page data
    private static class HomeData {
        final Student student;
        final List<RegisteredCourse> courses;
        HomeData(Student student, List<RegisteredCourse> courses) {
            this.student = student;
            this.courses = courses;
        }
    }

    // Refresh home page with latest data from server
    public void refreshHomePageData() {
        SwingWorker<HomeData, Void> worker = new SwingWorker<>() {

            @Override
            protected HomeData doInBackground() throws Exception {
                // Get student profile and registered courses in background
                Student profile = studentApi.getStudentProfile(session.getUserId());
                List<RegisteredCourse> courses = studentApi.getRegisteredCourses(session.getUserId());
                return new HomeData(profile, courses);
            }

            @Override
            protected void done() {
                try {
                    // Check maintenance mode status
                    AdminApi adminApi = new AdminApi();
                    boolean isMaintenanceOn = adminApi.isMaintenanceModeOn();
                    maintenanceBanner.setVisible(isMaintenanceOn);
                    
                    HomeData data = get();

                    if (data.student != null) {
                        // Update labels with real student data
                        welcomeLabel.setText("GOOD MORNING, " + data.student.getFullName().toUpperCase());
                        rollNoLabel.setText("Roll No: " + data.student.getRollNo());
                        programLabel.setText("Program: " + data.student.getProgram());
                        yearLabel.setText("Year: " + data.student.getYear());
                    } else {
                        // Show error if student data not found
                        rollNoLabel.setText("Roll No: Not found");
                        programLabel.setText("Program: Not found");
                        yearLabel.setText("Year: Not found");
                    }

                    // Update course list
                    courseListPanel.removeAll();
                    if (data.courses != null && !data.courses.isEmpty()) {
                        for (RegisteredCourse course : data.courses) {
                            String courseText = "  - " + course.getCourseCode() + ": " + course.getCourseTitle();
                            courseListPanel.add(createInfoLabel(courseText));
                        }
                    } else {
                        courseListPanel.add(createInfoLabel("  No courses registered."));
                    }
                    courseListPanel.revalidate();
                    courseListPanel.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                    rollNoLabel.setText("Error loading data.");
                }
            }
        };

        worker.execute();
    }


    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setForeground(Color.LIGHT_GRAY);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 36));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }

    // Create styled navigation button
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(15, 20, 15, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effects to buttons
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent evt) {
                if (button.getBackground() != NAV_ACTIVE_BG) {
                    button.setBackground(new Color(70, 70, 70)); // Darker on hover
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (button.getBackground() != NAV_ACTIVE_BG) {
                    button.setBackground(NAV_COLOR); // Return to normal
                }
            }
        });

        return button;
    }

    // Highlight the currently active navigation button
    private void setActiveButton(JButton activeButton) {
        for (JButton button : navButtons) {
            if (button == activeButton) {
                button.setBackground(NAV_ACTIVE_BG);
                button.setForeground(NAV_ACTIVE_TEXT);
            } else {
                button.setBackground(NAV_COLOR);
                button.setForeground(NAV_TEXT_COLOR);
            }
        }
    }

    // Custom panel for background image with dark overlay
    private class BackgroundPanel extends JPanel {
        private BufferedImage image;

        public BackgroundPanel() {
            try {
                // Load background image from resources
                image =ImageIO.read(getClass().getClassLoader().getResourceAsStream("student_bg.jpg"));
            } catch (IOException e) {
                System.err.println("Could not read background image 'student_bg.jpg'");
                image = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                // Draw background image stretched to fit window
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }
            // Add dark overlay for better text readability
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

}