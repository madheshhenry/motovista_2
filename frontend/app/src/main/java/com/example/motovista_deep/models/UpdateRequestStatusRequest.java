package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class UpdateRequestStatusRequest {
    @SerializedName("request_id")
    private int request_id;

    @SerializedName("status")
    private String status;

    public UpdateRequestStatusRequest(int request_id, String status) {
        this.request_id = request_id;
        this.status = status;
    }
}
