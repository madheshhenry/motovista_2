package com.example.motovista_deep.models;

import java.util.List;

public class GetBikesResponse {
    private String status;
    private String message;
    private List<BikeModel> data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<BikeModel> getData() {
        return data;
    }
}