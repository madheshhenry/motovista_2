package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class RequestResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("order_id")
    private String order_id;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
    
    public String getOrderId() {
        return order_id;
    }
}
