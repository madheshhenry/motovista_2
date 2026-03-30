package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class AdminNotification {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("target_screen")
    private String targetScreen;

    @SerializedName("item_id")
    private String itemId;

    @SerializedName("is_read")
    private int isRead;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getTimestamp() { return timestamp; }
    public String getTargetScreen() { return targetScreen; }
    public String getItemId() { return itemId; }
    public boolean isRead() { return isRead == 1; }
}
