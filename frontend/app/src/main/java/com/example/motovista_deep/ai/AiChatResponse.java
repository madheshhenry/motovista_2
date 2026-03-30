package com.example.motovista_deep.ai;

import java.util.List;

public class AiChatResponse {

    private String message; // Python returns 'message'
    private List<String> options;
    private String type;
    private List<RecommendationData> data;

    public String getMessage() {
        return message;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getType() {
        return type;
    }

    public List<RecommendationData> getData() {
        return data;
    }

    // New Inner Class for Bike Data
    public static class RecommendationData {
        private String name;
        private String description;
        private String image;
        private Object price; // Handle potentially string or int

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public String getPrice() { return String.valueOf(price); }
    }
    
    // Legacy support if needed, but preferably remove
    public String getReply() { return message; }
    public List<RecommendationData> getRecommendations() { return data; }
}
