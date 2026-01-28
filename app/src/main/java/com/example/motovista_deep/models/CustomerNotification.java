package com.example.motovista_deep.models;

public class CustomerNotification {
    private String id;
    private String title;
    private String message;
    private String timestamp;
    private String type; // e.g., "order", "emi", "offer", "system"
    private boolean isRead;

    public CustomerNotification(String id, String title, String message, String timestamp, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.isRead = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
