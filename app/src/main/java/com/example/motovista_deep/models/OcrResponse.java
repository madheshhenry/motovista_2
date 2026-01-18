package com.example.motovista_deep.models;

public class OcrResponse {
    private boolean success;
    private String text;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }

    public String getMessage() {
        return message;
    }
}
