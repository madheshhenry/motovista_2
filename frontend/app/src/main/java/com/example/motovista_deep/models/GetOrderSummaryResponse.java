package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class GetOrderSummaryResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OrderSummaryData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public OrderSummaryData getData() {
        return data;
    }
}
