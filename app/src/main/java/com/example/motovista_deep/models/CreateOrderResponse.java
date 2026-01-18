package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class CreateOrderResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CreateOrderData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public CreateOrderData getData() { return data; }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}
