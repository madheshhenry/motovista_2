package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class GetMyRequestsRequest {
    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("user_id")
    private Integer userId;

    public GetMyRequestsRequest(String mobileNumber, Integer userId) {
        this.mobileNumber = mobileNumber;
        this.userId = userId;
    }
}
