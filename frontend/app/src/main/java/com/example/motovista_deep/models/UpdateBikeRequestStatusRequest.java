package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class UpdateBikeRequestStatusRequest {
    @SerializedName("request_id")
    private int requestId;

    @SerializedName("status")
    private String status;

    public UpdateBikeRequestStatusRequest(int requestId, String status) {
        this.requestId = requestId;
        this.status = status;
    }
    
    public int getRequestId() { return requestId; }
    public String getStatus() { return status; }
}
