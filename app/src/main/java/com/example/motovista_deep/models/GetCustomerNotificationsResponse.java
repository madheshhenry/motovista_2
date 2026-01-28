package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetCustomerNotificationsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<CustomerNotification> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return success; }
    public List<CustomerNotification> getData() { return data; }
    public String getMessage() { return message; }
}
