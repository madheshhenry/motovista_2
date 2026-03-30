package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyBikesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<MyBikeModel> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return success; }
    public List<MyBikeModel> getData() { return data; }
    public String getMessage() { return message; }
}
