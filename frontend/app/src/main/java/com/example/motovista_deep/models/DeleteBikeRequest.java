package com.example.motovista_deep.models;

public class DeleteBikeRequest {
    private int bike_id;

    public DeleteBikeRequest(int bike_id) {
        this.bike_id = bike_id;
    }

    public int getBike_id() { return bike_id; }
    public void setBike_id(int bike_id) { this.bike_id = bike_id; }
}