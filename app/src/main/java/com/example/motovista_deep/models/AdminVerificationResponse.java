package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminVerificationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<AdminVerification> data;

    public boolean isSuccess() { return success; }
    public List<AdminVerification> getData() { return data; }
}
