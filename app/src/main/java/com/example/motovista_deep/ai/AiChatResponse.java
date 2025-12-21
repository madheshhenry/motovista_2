package com.example.motovista_deep.ai;

import java.util.List;

public class AiChatResponse {

    private String reply;
    private List<Recommendation> recommendations;

    public String getReply() {
        return reply;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public static class Recommendation {
        private String bike;       // ✅ MATCHES BACKEND
        private double confidence;

        public String getBike() {
            return bike;
        }

        public double getConfidence() {
            return confidence;
        }
    }
}
