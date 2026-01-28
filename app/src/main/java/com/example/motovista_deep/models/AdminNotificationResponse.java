package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AdminNotificationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("status")
    private Object status;

    @SerializedName("counts")
    private Map<String, Integer> counts;

    public boolean isSuccess() {
        if (success) return true;
        if (status instanceof Boolean) return (Boolean) status;
        if (status instanceof String) {
            return "true".equalsIgnoreCase((String) status) || "success".equalsIgnoreCase((String) status);
        }
        return false;
    }
    public Map<String, Integer> getCounts() { return counts; }
    
    public int getEmiVerifications() {
        return counts != null && counts.containsKey("emi_verifications") ? counts.get("emi_verifications") : 0;
    }
}
