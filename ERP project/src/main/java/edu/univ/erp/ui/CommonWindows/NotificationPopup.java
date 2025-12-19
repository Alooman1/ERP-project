package edu.univ.erp.ui.CommonWindows;

import edu.univ.erp.domain.Notification;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.NotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NotificationPopup extends JWindow {

    private final NotificationService service;
    private final UserSession session;
    private final Runnable onCloseCallback;
    private final JPanel content;

    public NotificationPopup(Window owner, UserSession session, Component invoker, Runnable onRefreshNeeded) {
        super(owner);
        this.session = session;
        this.service = new NotificationService();
        this.onCloseCallback = onRefreshNeeded;

        // Create main content panel for notifications
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(40, 40, 40)); // Dark background
        content.setBorder(new LineBorder(new Color(90, 90, 90), 1)); // Border around popup

        refreshContent(); // Load and display notifications
        add(content);
        pack(); // Size window to fit content

        // Position popup below the notification bell icon
        Point loc = invoker.getLocationOnScreen();
        setLocation(loc.x - getWidth() + invoker.getWidth(), loc.y + invoker.getHeight());
        setVisible(true);
    }

    // Reload and refresh all notification content
    private void refreshContent() {
        content.removeAll(); // Clear existing content

        // Create header with title and clear button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Notifications");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Button to clear all notifications
        JButton clearAllBtn = new JButton("Clear all");
        clearAllBtn.setFocusPainted(false);
        clearAllBtn.setMargin(new Insets(2, 8, 2, 8));
        clearAllBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearAllBtn.addActionListener(e -> handleClearAll());

        header.add(clearAllBtn, BorderLayout.EAST);

        content.add(header);
        content.add(new JSeparator()); // Horizontal line

        // Get user's notifications from database
        List<Notification> notifs = service.getUserNotifications(session.getUserId());

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        if (notifs.isEmpty()) {
            // Show message when no notifications
            clearAllBtn.setEnabled(false);
            JLabel empty = new JLabel("You're all caught up ✨");
            empty.setForeground(Color.LIGHT_GRAY);
            empty.setBorder(new EmptyBorder(15, 15, 15, 15));
            listPanel.add(empty);
        } else {
            // Add each notification as an item
            clearAllBtn.setEnabled(true);
            for (Notification n : notifs) {
                listPanel.add(createItem(n));
            }
        }

        // Make notification list scrollable
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setPreferredSize(new Dimension(360, 260)); // Fixed size for popup
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        content.add(scrollPane);

        // Footer with close button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 8, 8, 8));

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> closePopup());
        footer.add(closeBtn);

        content.add(footer);

        // Refresh display
        content.revalidate();
        content.repaint();
        pack();

        if (onCloseCallback != null) onCloseCallback.run();
    }

    // Close the notification popup
    private void closePopup() {
        setVisible(false);
        dispose();
    }

    // Create individual notification item
    private JPanel createItem(Notification n) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Text area for notification message and time
        JPanel textWrapper = new JPanel();
        textWrapper.setOpaque(false);
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));

        JTextArea text = new JTextArea(n.getMessage());
        text.setLineWrap(true); // Wrap long text
        text.setWrapStyleWord(true); // Wrap at word boundaries
        text.setOpaque(false);
        text.setEditable(false);
        text.setForeground(n.isRead() ? Color.GRAY : Color.WHITE); // Gray if read, white if unread
        text.setFont(new Font("SansSerif", n.isRead() ? Font.PLAIN : Font.BOLD, 13)); // Bold if unread
        text.setBorder(null);
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Show when notification was created
        JLabel timeLabel = new JLabel(n.getTimestamp());
        timeLabel.setForeground(new Color(180, 180, 180)); // Light gray
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textWrapper.add(text);
        textWrapper.add(Box.createVerticalStrut(3)); // Small space between text and time
        textWrapper.add(timeLabel);

        // Action buttons (delete) on the right
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);

        // Red X button to delete notification
        JLabel deleteIcon = new JLabel("✕");
        deleteIcon.setForeground(new Color(230, 80, 80)); // Red color
        deleteIcon.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteIcon.setToolTipText("Delete notification");
        deleteIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDelete(n.getId());
            }
        });

        actionPanel.add(deleteIcon);

        item.add(textWrapper, BorderLayout.CENTER);
        item.add(actionPanel, BorderLayout.EAST);

        // Make unread notifications clickable to mark as read
        if (!n.isRead()) {
            text.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            text.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    service.markRead(n.getId()); // Mark as read in database
                    refreshContent(); // Refresh to show updated state
                }
            });
        }

        return item;
    }

    // Handle clearing all notifications
    private void handleClearAll() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Clear all notifications? This cannot be undone.",
                "Clear all",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Clear notifications in background thread
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return service.clearAllForUser(session.getUserId());
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            refreshContent(); // Refresh if successful
                        } else {
                                JOptionPane.showMessageDialog(NotificationPopup.this,
                                        "Failed to clear notifications.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(NotificationPopup.this,
                                "Error while clearing notifications.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
        }
    }

    // Handle deleting single notification
    private void handleDelete(int notificationId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete this notification?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Delete notification in background thread
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return service.deleteNotification(notificationId);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            refreshContent(); // Refresh if successful
                        } else {
                            JOptionPane.showMessageDialog(NotificationPopup.this,
                                    "Failed to delete notification.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(NotificationPopup.this,
                                "Error during deletion.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
        }
    }
}