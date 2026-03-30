package com.example.motovista_deep.models;

import com.google.gson.annotations.SerializedName;

public class DeleteRequestRequest {
    @SerializedName("request_id")
    private int request_id;

    public DeleteRequestRequest(int request_id) {
        this.request_id = request_id;
    }
}
