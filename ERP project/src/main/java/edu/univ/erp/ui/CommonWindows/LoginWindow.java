package edu.univ.erp.ui.CommonWindows;

import edu.univ.erp.auth.AuthApi;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.ui.AdminWindows.AdminDashboard;
import edu.univ.erp.ui.InstructorWindows.InstructorDashboard;
import edu.univ.erp.ui.StudentWindows.StudentDashboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// The main entry point for the application. Handles user authentication.
public class LoginWindow extends JFrame {

    // Background image for the styling
    private BufferedImage background;

    // UI Components for user input
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JToggleButton showPasswordButton;
    private JButton loginButton;

    // Handle backend authentication
    private AuthApi authApi;

    // Constructor: Sets up the window and UI
    public LoginWindow() {
        this.authApi = new AuthApi();

        // Load the background image from resources
        try {
            background = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Rnd back.jpg"));
        } catch (IOException e) {
            System.err.println("Could not read background image 'Rnd back.jpg'");
            background = null;
        }

        // Basic window settings
        setTitle("Student ERP Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 700));
        setLocationRelativeTo(null); // Center on screen

        // Custom panel to paint the background
        BackgroundPanel mainPanel = new BackgroundPanel(background);
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible centering
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Color palette and Fonts for the dark theme
        Color foregroundColor = Color.WHITE;
        Color darkTransparent = new Color(50, 50, 50, 180);
        Color lightBlue = new Color(150, 180, 255);
        Color separatorColor = new Color(100, 100, 100);

        Font titleFont = new Font("SansSerif", Font.BOLD, 42);
        Font subtitleFont = new Font("Serif", Font.BOLD, 24);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font smallFont = new Font("SansSerif", Font.PLAIN, 12);
        Font mediumFont = new Font("SansSerif", Font.PLAIN, 14);

        // Styling for input fields
        Border fieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 5, 5, 5)
        );

        // Layout configuration
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title and Subtitle section
        JLabel titleLabel = new JLabel("STUDENT ERP PORTAL");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(foregroundColor);
        gbc.insets = new Insets(10, 5, 15, 5);
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("LOGIN");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(foregroundColor);
        gbc.insets = new Insets(0, 5, 5, 5);
        mainPanel.add(subtitleLabel, gbc);

        // Decorative line
        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setForeground(separatorColor);
        line.setBackground(separatorColor);
        gbc.insets = new Insets(0, 5, 20, 5);
        mainPanel.add(line, gbc);

        // Username Input Field
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(labelFont);
        userLabel.setForeground(foregroundColor);
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(labelFont);
        usernameField.setBackground(darkTransparent);
        usernameField.setForeground(foregroundColor);
        usernameField.setCaretColor(foregroundColor);
        usernameField.setBorder(fieldBorder);
        mainPanel.add(usernameField, gbc);

        // Password Input Field with "Show Password" toggle
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(labelFont);
        passLabel.setForeground(foregroundColor);
        mainPanel.add(passLabel, gbc);

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(darkTransparent);
        passPanel.setBorder(fieldBorder);
        passPanel.setOpaque(true);

        passwordField = new JPasswordField(20);
        passwordField.setFont(labelFont);
        passwordField.setBackground(darkTransparent);
        passwordField.setForeground(foregroundColor);
        passwordField.setCaretColor(foregroundColor);
        passwordField.setBorder(null);
        passPanel.add(passwordField, BorderLayout.CENTER);

        // Eye icon button
        showPasswordButton = new JToggleButton("\uD83D\uDC41");
        showPasswordButton.setFont(labelFont);
        showPasswordButton.setForeground(lightBlue);
        showPasswordButton.setOpaque(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setBorder(new EmptyBorder(0, 5, 0, 5));
        showPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Logic to toggle password visibility (dots vs text)
        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show text
            } else {
                passwordField.setEchoChar('•'); // Show dots
            }
        });

        passPanel.add(showPasswordButton, BorderLayout.EAST);
        mainPanel.add(passPanel, gbc);

        // Forgot password link (placeholder)
        JLabel forgotLabel = createClickableLabel("<html><u>Forgot Password?</u></html>", smallFont, lightBlue);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 5, 15, 5);
        mainPanel.add(forgotLabel, gbc);
        addNotImplementedListener(forgotLabel, "Forgot Password functionality");

        // Main Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(labelFont);
        loginButton.setOpaque(true);
        loginButton.setBackground(new Color(80, 80, 80));
        loginButton.setForeground(foregroundColor);
        mainPanel.add(loginButton, gbc);

        // Separator for external options
        JSeparator line2 = new JSeparator(SwingConstants.HORIZONTAL);
        line2.setForeground(separatorColor);
        line2.setBackground(separatorColor);
        gbc.insets = new Insets(20, 5, 10, 5);
        mainPanel.add(line2, gbc);

        // Extra links (Google Sign-in, Registration) - currently placeholders
        String googleText = "\uD83C\uDF10  Sign in with Google";
        JLabel googleLabel = createClickableLabel(googleText, mediumFont, foregroundColor);
        gbc.insets = new Insets(10, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(googleLabel, gbc);
        addNotImplementedListener(googleLabel, "Google Sign-In");

        JLabel registerLabel = createClickableLabel("<html><u>New? Register</u></html>", mediumFont, foregroundColor);
        gbc.insets = new Insets(5, 5, 10, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(registerLabel, gbc);
        addNotImplementedListener(registerLabel, "User Registration");

        setContentPane(mainPanel);
        pack();
        setVisible(true);

        addLoginButtonListener();
    }

    // Handles the click event for the Login button
    private void addLoginButtonListener() {
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Call the API to verify credentials
            UserSession session = authApi.login(username, password);
            
            if (session == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Incorrect username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            } else if (session.getRole().startsWith("LOCKED:")) {
                // Handle locked account scenario
                String minutes = session.getRole().split(":")[1];

                JOptionPane.showMessageDialog(
                        this,
                        "Your account is locked due to too many failed attempts.\n" +
                                "Please try again in " + minutes + " minutes.",
                        "Account Locked",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                // Login successful - Redirect based on Role
                if (session.isStudent()) {
                    new StudentDashboard(session);
                } else if (session.isInstructor()) {
                    new InstructorDashboard(session);
                } else if (session.isAdmin()) {
                    new AdminDashboard(session);
                }
                this.dispose(); // Close login window
            }
        });
    }

    // Helper to create styled, clickable labels (like links)
    private JLabel createClickableLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    // Placeholder listener for features not yet implemented
    private void addNotImplementedListener(Component component, String featureName) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        LoginWindow.this,
                        featureName + " is not implemented in this project.",
                        "Feature Not Implemented",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    // Custom panel to paint the background image with a dark overlay
    private class BackgroundPanel extends JPanel {
        private BufferedImage image;
        public BackgroundPanel(BufferedImage image) { this.image = image; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }
            // Add a transparent dark layer for better text readability
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
    
    // Application entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}