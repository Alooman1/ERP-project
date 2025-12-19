package edu.univ.erp.ui.InstructorWindows;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.ui.CommonWindows.LoginWindow;
import edu.univ.erp.api.admin.AdminApi;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Main dashboard for instructors - this is like the home screen
public class InstructorDashboard extends JFrame {

    private UserSession session;
    private InstructorApi instructorApi;
    private JLabel maintenanceBanner;
    
    // Colors for the dashboard
    private final Color NAV_COLOR = new Color(50, 50, 50);
    private final Color NAV_TEXT_COLOR = Color.WHITE;
    private final Color NAV_ACTIVE_BG = Color.WHITE;
    private final Color NAV_ACTIVE_TEXT = Color.DARK_GRAY;
    private final Color HEADER_COLOR = new Color(70, 70, 70);
    
    private JPanel navPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<JButton> navButtons = new ArrayList<>();

    // Various panels for different sections
    private JLabel welcomeLabel;
    private JLabel departmentLabel;
    private JPanel teachingListPanel; // For the Home Page list
    private JPanel mySectionsGridPanel; // For the "My Section" grid
    private JPanel enterScoresPanel;
    private GradebookPanel gradebookPanel;
    private ClassStatsPanel classStatsPanel;
    private InstructorTimetablePanel timetablePanel;
    private InstructorChangePasswordPanel changePasswordPanel;

    private boolean isNavMenuExpanded = true;

    public InstructorDashboard(UserSession session) {
        this.session = session;
        this.instructorApi = new InstructorApi(session);

        setTitle("Instructor Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        
        // Background panel with image
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        this.navPanel = createNavPanel();
        mainPanel.add(this.navPanel, BorderLayout.WEST);
        this.contentPanel = createContentPanel();
        mainPanel.add(this.contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        loadDashboardData();
        setVisible(true);
    }

    // Create the top header with menu and notifications
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(new Color(70, 70, 70));
        headerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Menu icon on the left
        JLabel menuIcon = new JLabel("\u2630  MENU");
        menuIcon.setFont(new Font("SansSerif", Font.BOLD, 20));
        menuIcon.setForeground(Color.WHITE);
        menuIcon.setBorder(new EmptyBorder(5, 10, 5, 20));
        menuIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleNavMenu(); // Show/hide navigation
            }
        });
        headerPanel.add(menuIcon, BorderLayout.WEST);

        // Notification icon on the right
        JLabel notificationIcon = new JLabel("\uD83D\uDD14");
        notificationIcon.setFont(new Font("SansSerif", Font.BOLD, 18));
        notificationIcon.setForeground(Color.WHITE);
        notificationIcon.setBorder(new EmptyBorder(5, 10, 10, 15));
        notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Check if there are unread notifications
        edu.univ.erp.service.NotificationService ns = new edu.univ.erp.service.NotificationService();
        if (ns.hasUnread(session.getUserId())) {
            notificationIcon.setForeground(new Color(255, 100, 100)); // Red for unread
        }

        // Show notifications when clicked
        notificationIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new edu.univ.erp.ui.CommonWindows.NotificationPopup(
                        InstructorDashboard.this,
                        session,
                        (Component)e.getSource(),
                        () -> {
                            // Update notification icon color
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

        // Maintenance mode banner
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

    // Create the left navigation panel
    private JPanel createNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(NAV_COLOR);
        navPanel.setPreferredSize(new Dimension(250, 0));
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create navigation buttons
        JButton homeButton = createNavButton("HOME");
        JButton mySectionButton = createNavButton("MY SECTION");
        JButton enterScoresButton = createNavButton("ENTER SCORES");
        JButton changePassButton = createNavButton("CHANGE PASSWORD");
        JButton logoutButton = createNavButton("LOGOUT");
        
        // Button actions
        homeButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "HOME");
            setActiveButton(homeButton);
        });
        mySectionButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "MY_SECTION");
            setActiveButton(mySectionButton);
        });
        
        enterScoresButton.addActionListener(e -> {
             cardLayout.show(contentPanel, "ENTER_SCORES");
             setActiveButton(enterScoresButton);
        });

        changePassButton.addActionListener(e -> {
             cardLayout.show(contentPanel, "CHANGE_PASSWORD");
             setActiveButton(changePassButton);
        });
        
        // Logout confirmation
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                InstructorDashboard.this, "Are you sure you want to log out?",
                "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginWindow(); // Go back to login screen
            }
        });

        navButtons.addAll(List.of(homeButton, mySectionButton, enterScoresButton, changePassButton, logoutButton));
        navPanel.add(homeButton);
        navPanel.add(mySectionButton);
        navPanel.add(enterScoresButton);
        navPanel.add(changePassButton);
        navPanel.add(logoutButton);

        navPanel.add(Box.createVerticalGlue()); 
        
        // Profile section at bottom
        JLabel profileIcon = new JLabel("  \uD83D\uDC64  " + session.getUsername()); 
        profileIcon.setFont(new Font("SansSerif", Font.BOLD, 16));
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileIcon.setHorizontalAlignment(SwingConstants.LEFT); 
        profileIcon.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        profileIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new InstructorProfileWindow(InstructorDashboard.this, session, InstructorDashboard.this);
            }
        });
        
        navPanel.add(profileIcon);
        
        setActiveButton(homeButton);
        return navPanel;
    }

    // Show/hide the navigation menu
    private void toggleNavMenu() {
        isNavMenuExpanded = !isNavMenuExpanded;
        navPanel.setVisible(isNavMenuExpanded);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    // Create the main content area with different panels
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Add all the different screens
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(createMySectionPanel(), "MY_SECTION");
        enterScoresPanel = createEnterScoresPanel();
        contentPanel.add(enterScoresPanel, "ENTER_SCORES");
        gradebookPanel = new GradebookPanel(session, null); 
        contentPanel.add(gradebookPanel, "GRADEBOOK");
        classStatsPanel = new ClassStatsPanel(session, null);
        contentPanel.add(classStatsPanel, "CLASS_STATS");
        timetablePanel = new InstructorTimetablePanel(session);
        contentPanel.add(timetablePanel, "TIMETABLE");
        changePasswordPanel = new InstructorChangePasswordPanel(session);
        contentPanel.add(changePasswordPanel, "CHANGE_PASSWORD");
        
        return contentPanel;
    }

    // Create the home panel with welcome message
    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        welcomeLabel = createInfoLabel("GOOD MORNING, " + session.getUsername().toUpperCase(), 36, Color.WHITE);
        departmentLabel = createInfoLabel("Department: [Fetching...]", 18, Color.LIGHT_GRAY);
        
        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(departmentLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel teachingTitle = createInfoLabel("CURRENTLY TEACHING", 22, Color.WHITE);
        panel.add(teachingTitle);
        
        teachingListPanel = new JPanel();
        teachingListPanel.setOpaque(false);
        teachingListPanel.setLayout(new BoxLayout(teachingListPanel, BoxLayout.Y_AXIS));
        teachingListPanel.add(createInfoLabel("  [Fetching courses...]", 18, Color.LIGHT_GRAY));
        
        panel.add(teachingListPanel);
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // Create "My Sections" panel
    private JPanel createMySectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        JLabel title = new JLabel("MY SECTIONS");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        
        mySectionsGridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        mySectionsGridPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(mySectionsGridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button to view full timetable
        JButton viewTimetableButton = new JButton("View Full Timetable");
        viewTimetableButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        viewTimetableButton.setOpaque(true);
        viewTimetableButton.setBackground(new Color(80, 80, 80));
        viewTimetableButton.setForeground(Color.WHITE);
        viewTimetableButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "TIMETABLE");
            setActiveButton(null);
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewTimetableButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Create "Enter Scores" panel
    private JPanel createEnterScoresPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        JLabel title = new JLabel("ENTER SCORES");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // Create placeholder panel (not used in final version)
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 36));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }

    // Helper to create info labels
    private JLabel createInfoLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(color);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    // Create navigation buttons with hover effects
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
        
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (button.getBackground() != NAV_ACTIVE_BG) {
                    button.setBackground(new Color(70, 70, 70)); // Darker on hover
                }
            }
            public void mouseExited(MouseEvent evt) {
                if (button.getBackground() != NAV_ACTIVE_BG) {
                    button.setBackground(NAV_COLOR); // Back to normal
                }
            }
        });
        return button;
    }

    // Highlight the active navigation button
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
    
    // Helper class to hold instructor data
    private static class InstructorData {
        final Instructor profile;
        final List<AssignedSection> sections;
        InstructorData(Instructor p, List<AssignedSection> s) {
            this.profile = p;
            this.sections = s;
        }
    }

    // Refresh data on the home page
    public void refreshHomePageData() {
        SwingWorker<InstructorData, Void> worker = new SwingWorker<>() {
            @Override
            protected InstructorData doInBackground() throws Exception {
                Instructor profile = instructorApi.getInstructorProfile(session.getUserId());
                List<AssignedSection> sections = instructorApi.getAssignedSections(session.getUserId());
                return new InstructorData(profile, sections);
            }

            @Override
            protected void done() {
                try {
                    AdminApi adminApi = new AdminApi();
                    boolean isMaintenanceOn = adminApi.isMaintenanceModeOn();
                    maintenanceBanner.setVisible(isMaintenanceOn); // Show maintenance warning
                    
                    InstructorData data = get(); 
                    
                    if (data.profile != null) {
                        welcomeLabel.setText("GOOD MORNING, " + data.profile.getFullName().toUpperCase());
                        departmentLabel.setText("Department: " + data.profile.getDepartment());
                    }

                    // Refresh all the panels with new data
                    mySectionsGridPanel.removeAll();
                    JPanel enterScoresGrid = (JPanel)((JScrollPane)enterScoresPanel.getComponent(1)).getViewport().getView();
                    enterScoresGrid.removeAll();
                    teachingListPanel.removeAll();
                    
                    if (data.sections != null && !data.sections.isEmpty()) {
                        for (AssignedSection section : data.sections) {
                            teachingListPanel.add(createInfoLabel("  - " + section.getSubjectName(), 18, Color.LIGHT_GRAY));
                            mySectionsGridPanel.add(createSubjectBoxHome(section));
                            enterScoresGrid.add(createSubjectBoxEnterScores(section));
                        }
                    } else {
                        teachingListPanel.add(createInfoLabel("  No courses assigned.", 18, Color.LIGHT_GRAY));
                        mySectionsGridPanel.add(createInfoLabel("No sections assigned.", 18, Color.LIGHT_GRAY));
                        enterScoresGrid.add(createInfoLabel("No sections assigned.", 18, Color.LIGHT_GRAY));
                    }
                    
                    // Refresh the display
                    teachingListPanel.revalidate();
                    teachingListPanel.repaint();
                    mySectionsGridPanel.revalidate();
                    mySectionsGridPanel.repaint();
                    enterScoresGrid.revalidate();
                    enterScoresGrid.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                    welcomeLabel.setText("Error loading data.");
                }
            }
        };
        worker.execute();
    }

    // Load initial dashboard data
    private void loadDashboardData() {
        refreshHomePageData();
    }

    // Create box for each subject in home panel
    private JPanel createSubjectBoxHome(AssignedSection section) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setBackground(new Color(60, 60, 60, 200));
        box.setBorder(new CompoundBorder(
            new LineBorder(new Color(90, 90, 90, 150), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLabel = new JLabel("<html>" + section.getSubjectName() + "</html>");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20)); 
        nameLabel.setForeground(Color.WHITE);
        box.add(nameLabel, BorderLayout.NORTH);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        // Clickable options for each subject
        detailsPanel.add(createClickableLabel("Class Stats", Color.WHITE, () -> {
            classStatsPanel.loadSection(section);
            cardLayout.show(contentPanel, "CLASS_STATS");
            setActiveButton(navButtons.get(1)); // "MY SECTION"
        }));
        detailsPanel.add(createClickableLabel("Send Notification", new Color(150, 180, 255), () -> {
            handleSendSectionNotification(section);
        }));
        box.add(detailsPanel, BorderLayout.CENTER);
        return box;
    }

    // Send notification to all students in a section
    private void handleSendSectionNotification(AssignedSection section) {
        String message = JOptionPane.showInputDialog(
                this,
                "Enter message for students in " + section.getCourseCode() + ":",
                "Broadcast to Section",
                JOptionPane.PLAIN_MESSAGE
        );

        if (message != null && !message.trim().isEmpty()) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return instructorApi.sendSectionNotification(section.getSectionId(), message.trim());
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(InstructorDashboard.this,
                                    "Message sent to students in " + section.getCourseCode(),
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(InstructorDashboard.this,
                                    "Failed to send message. No students enrolled or database error.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) { /* Handle exception */ }
                }
            };
            worker.execute();
        }
    }

    // Create box for each subject in enter scores panel
    private JPanel createSubjectBoxEnterScores(AssignedSection section) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setBackground(new Color(60, 60, 60, 200));
        box.setBorder(new CompoundBorder(
            new LineBorder(new Color(90, 90, 90, 150), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLabel = new JLabel("<html>" + section.getSubjectName() + "</html>");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        box.add(nameLabel, BorderLayout.NORTH);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        detailsPanel.add(createClickableLabel("Open Gradebook", Color.WHITE, () -> {
            gradebookPanel.loadSection(section);
            cardLayout.show(contentPanel, "GRADEBOOK");
            setActiveButton(navButtons.get(2)); // "ENTER SCORES"
        }));
        
        box.add(detailsPanel, BorderLayout.CENTER);
        return box;
    }

    // Create clickable label with action
    private JLabel createClickableLabel(String text, Color color, Runnable action) {
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(color);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run(); // Run the provided action
            }
        });
        return label;
    }

    // Background panel with image
    private class BackgroundPanel extends JPanel {
        private BufferedImage image;
        public BackgroundPanel() {
            try {
                image =ImageIO.read(getClass().getClassLoader().getResourceAsStream("student_bg.jpg"));
            } catch (IOException e) {
                image = null;
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }
            g.setColor(new Color(0, 0, 0, 150)); // Dark overlay
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
}