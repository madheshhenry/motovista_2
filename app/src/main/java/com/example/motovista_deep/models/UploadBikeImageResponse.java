package com.example.motovista_deep.models;

import java.util.List;

public class UploadBikeImageResponse {
    private String status;
    private String message;
    private List<String> data; // Changed to List<String>

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getData() {
        return data;
    }

    // Helper method to get first image path
    public String getFirstImagePath() {
        if (data != null && !data.isEmpty()) {
            return data.get(0);
        }
        return "";
    }

    // Helper method to get all paths as JSON string
    public String getAllPathsAsJson() {
        if (data != null) {
            return data.toString(); // Returns as JSON array string
        }
        return "[]";
    }
}