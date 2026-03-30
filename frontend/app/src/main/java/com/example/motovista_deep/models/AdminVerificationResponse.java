package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminVerificationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("status")
    private Object status;

    @SerializedName("data")
    private List<AdminVerification> data;

    public boolean isSuccess() {
        if (success) return true;
        if (status instanceof Boolean) return (Boolean) status;
        if (status instanceof String) {
            return "true".equalsIgnoreCase((String) status) || "success".equalsIgnoreCase((String) status);
        }
        return false;
    }
    public List<AdminVerification> getData() { return data; }
}
