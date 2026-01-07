package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetCustomerRequestsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<CustomerRequest> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public List<CustomerRequest> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
