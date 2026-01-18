package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AdminNotificationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("counts")
    private Map<String, Integer> counts;

    public boolean isSuccess() { return success; }
    public Map<String, Integer> getCounts() { return counts; }
    
    public int getEmiVerifications() {
        return counts != null && counts.containsKey("emi_verifications") ? counts.get("emi_verifications") : 0;
    }
}
