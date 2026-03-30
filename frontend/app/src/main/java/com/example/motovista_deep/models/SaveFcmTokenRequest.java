package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class SaveFcmTokenRequest {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("fcm_token")
    private String fcmToken;

    @SerializedName("device_name")
    private String deviceName;

    @SerializedName("user_type")
    private String userType;

    public SaveFcmTokenRequest(int userId, String fcmToken, String deviceName, String userType) {
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceName = deviceName;
        this.userType = userType;
    }
}
