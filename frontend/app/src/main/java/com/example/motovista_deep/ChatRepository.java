package com.example.motovista_deep;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    private static ChatRepository instance;
    private List<AIChatbotActivity.ChatMessage> messages;

    private ChatRepository() {
        messages = new ArrayList<>();
    }

    public static synchronized ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    public List<AIChatbotActivity.ChatMessage> getMessages() {
        return messages;
    }
    
    public void addMessage(AIChatbotActivity.ChatMessage message) {
        messages.add(message);
    }

    // Call this if we want to force clear (not requested, but good to have)
    public void clear() {
        messages.clear();
    }
    
    public boolean hasMessages() {
        return !messages.isEmpty();
    }
}
