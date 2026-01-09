package com.example.motovista_deep.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BikeListResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<InventoryBike> data;

    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public List<InventoryBike> getData() { return data; }
}
