package com.example.motovista_deep.models;

public class BaseResponse {
    private String status;
    private String message;
    private int code;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}