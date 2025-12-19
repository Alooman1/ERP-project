package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

// This panel lets admins send messages to everyone or specific users
public class AdminSendNotificationPanel extends JPanel {

    private final AdminApi adminApi;

    // Radio buttons to choose between sending to everyone or one person
    private JRadioButton broadcastRadio, individualRadio;
    private JComboBox<String> recipientBox;
    private JTextField individualUserField;
    private JTextArea messageArea;
    private JPanel recipientSwapPanel;
    private JLabel dynamicLabel;

    private static final int FORM_WIDTH = 500;

    public AdminSendNotificationPanel(UserSession session) {
        this.adminApi = new AdminApi();

        // Setup main panel
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Big title at top
        JLabel titleLabel = new JLabel("SEND NOTIFICATIONS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Main content area with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Radio buttons to choose message type
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        togglePanel.setOpaque(false);
        togglePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        togglePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        broadcastRadio = new JRadioButton("Broadcast by Role");
        individualRadio = new JRadioButton("Individual User");

        // Style the radio buttons
        for (JRadioButton rb : new JRadioButton[]{broadcastRadio, individualRadio}) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(new Font("SansSerif", Font.BOLD, 14));
            rb.setFocusPainted(false);
        }
        individualRadio.setBorder(new EmptyBorder(0, 20, 0, 0));

        // Group them so only one can be selected
        ButtonGroup group = new ButtonGroup();
        group.add(broadcastRadio);
        group.add(individualRadio);
        broadcastRadio.setSelected(true);

        togglePanel.add(broadcastRadio);
        togglePanel.add(individualRadio);
        mainPanel.add(togglePanel);

        // Label that changes based on radio selection
        dynamicLabel = createLabel("Select Target Role:");
        mainPanel.add(dynamicLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        // Dropdown for roles or text field for username
        String[] roles = {"Student", "Instructor", "Admin"};
        recipientBox = createComboBox(roles);
        individualUserField = createTextField();

        // Panel that switches between dropdown and text field
        recipientSwapPanel = new JPanel(new CardLayout());
        recipientSwapPanel.setOpaque(false);
        recipientSwapPanel.setMaximumSize(new Dimension(FORM_WIDTH, 35));
        recipientSwapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        recipientSwapPanel.add(recipientBox, "BROADCAST");
        recipientSwapPanel.add(individualUserField, "INDIVIDUAL");

        mainPanel.add(recipientSwapPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Message input area
        JLabel msgLabel = createLabel("Message:");
        mainPanel.add(msgLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        messageArea = createTextArea();
        JScrollPane scroll = new JScrollPane(messageArea);
        scroll.setBorder(new LineBorder(Color.GRAY));
        scroll.setMaximumSize(new Dimension(FORM_WIDTH, 150));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(scroll);
        mainPanel.add(Box.createVerticalStrut(20));

        // Send button at bottom
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton sendButton = createSendButton("Send Notification");
        btnPanel.add(sendButton);
        mainPanel.add(btnPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Setup what happens when radio buttons are clicked
        setupToggleListeners();
        sendButton.addActionListener(e -> handleSend());

        // Start with broadcast mode visible
        ((CardLayout)recipientSwapPanel.getLayout()).show(recipientSwapPanel, "BROADCAST");
    }

    // Creates nice-looking labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(new Color(200, 200, 200));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(FORM_WIDTH, 25));
        return label;
    }

    // Creates text fields for username input
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    // Creates dropdown with custom arrow styling
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setBackground(Color.WHITE);
        box.setForeground(Color.BLACK);
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Custom dropdown arrow styling
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new BasicArrowButton(
                        BasicArrowButton.SOUTH,
                        Color.WHITE, Color.WHITE, Color.DARK_GRAY, Color.WHITE
                ) {
                    @Override
                    public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.DARK_GRAY);
                        super.paintTriangle(g2, x, y, size, direction, isEnabled);
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });

        return box;
    }

    // Creates the big text area for typing messages
    private JTextArea createTextArea() {
        JTextArea area = new JTextArea(5, 30);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBackground(new Color(60, 60, 60));
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setBorder(new EmptyBorder(5, 5, 5, 5));
        return area;
    }

    // Creates the green send button
    private JButton createSendButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(new Color(80, 140, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 40, 10, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Switches between broadcast and individual mode when radio buttons clicked
    private void setupToggleListeners() {
        CardLayout cl = (CardLayout) (recipientSwapPanel.getLayout());

        broadcastRadio.addActionListener(e -> {
            dynamicLabel.setText("Select Target Role:");
            cl.show(recipientSwapPanel, "BROADCAST");
            recipientSwapPanel.revalidate();
        });

        individualRadio.addActionListener(e -> {
            dynamicLabel.setText("Enter Target User (Username):");
            cl.show(recipientSwapPanel, "INDIVIDUAL");
            recipientSwapPanel.revalidate();
        });
    }

    // Handles when send button is clicked
    private void handleSend() {
        String message = messageArea.getText().trim();

        // Check if message is empty
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Send to everyone or just one person based on selection
        if (broadcastRadio.isSelected()) {
            String role = (String) recipientBox.getSelectedItem();
            runBroadcastWorker(role, message);
        } else if (individualRadio.isSelected()) {
            String username = individualUserField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            runIndividualWorker(username, message);
        }
    }

    // Sends message to all users of a certain type (students, teachers, etc.)
    private void runBroadcastWorker(String role, String message) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminApi.sendBroadcastNotification(role, message);
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdminSendNotificationPanel.this, "Notification sent to all " + role + "s!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        messageArea.setText("");
                    } else {
                        JOptionPane.showMessageDialog(AdminSendNotificationPanel.this, "Failed to send.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminSendNotificationPanel.this, "Error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Sends message to just one specific user
    private void runIndividualWorker(String username, String message) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                boolean success = adminApi.sendIndividualNotification(username, message);
                return success ? "Success" : "Failed";
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    if ("Success".equals(result)) {
                        JOptionPane.showMessageDialog(AdminSendNotificationPanel.this, "Notification sent to " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                        messageArea.setText("");
                        individualUserField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(AdminSendNotificationPanel.this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminSendNotificationPanel.this, "Error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}