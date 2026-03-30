package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetShuffledBikesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<BikeModel> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<BikeModel> getData() {
        return data;
    }
}
