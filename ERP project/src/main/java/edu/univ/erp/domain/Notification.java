package edu.univ.erp.domain;

// Stores notification messages for users like alerts or updates
public class Notification {
    private final int id;
    private final String message;
    private final boolean isRead;
    private final String timestamp;

    // Creates a notification with message and timing info
    public Notification(int id, String message, boolean isRead, String timestamp) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    // Get notification's unique number
    public int getId() { return id; }
    // Get the actual message text
    public String getMessage() { return message; }
    // Check if user has seen this notification
    public boolean isRead() { return isRead; }
    // Get when notification was sent
    public String getTimestamp() { return timestamp; }
}