package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class GetEmiDetailsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private EmiDetailsData data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return success; }
    public EmiDetailsData getData() { return data; }
    public String getMessage() { return message; }
}
