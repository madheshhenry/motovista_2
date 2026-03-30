package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetBikeRequestsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<BikeRequest> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public List<BikeRequest> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
