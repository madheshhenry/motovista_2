package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class CustomerNotification {
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    @SerializedName("type")
    private String type; // e.g., "order", "emi", "offer", "system"
    
    @SerializedName("target_screen")
    private String target_screen;
    
    @SerializedName("item_id")
    private String item_id;
    
    @SerializedName("is_read")
    private boolean isRead;

    public CustomerNotification(String id, String title, String message, String timestamp, String type, String target_screen, String item_id) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.target_screen = target_screen;
        this.item_id = item_id;
        this.isRead = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public String getTarget_screen() { return target_screen; }
    public String getItem_id() { return item_id; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
