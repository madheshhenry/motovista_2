package com.example.motovista_deep.models;

public class CompleteOrderRequest {
    private int request_id;

    public CompleteOrderRequest(int request_id) {
        this.request_id = request_id;
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }
}
