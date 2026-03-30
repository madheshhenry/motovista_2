package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class GetBikeByIdResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private BikeModel data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public BikeModel getData() { return data; }
}