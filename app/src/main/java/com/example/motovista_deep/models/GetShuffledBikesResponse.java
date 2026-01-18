package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetShuffledBikesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<BikeModel> data;

    public boolean isSuccess() {
        return success;
    }

    public List<BikeModel> getData() {
        return data;
    }
}
