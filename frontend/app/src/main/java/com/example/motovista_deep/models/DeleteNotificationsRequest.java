package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DeleteNotificationsRequest {
    @SerializedName("items")
    private List<NotificationItem> items;

    public DeleteNotificationsRequest(List<NotificationItem> items) {
        this.items = items;
    }

    public static class NotificationItem {
        @SerializedName("id")
        private int id;
        @SerializedName("type")
        private String type;

        public NotificationItem(int id, String type) {
            this.id = id;
            this.type = type;
        }
    }
}
