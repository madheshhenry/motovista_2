package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motovista_deep.ai.AiChatRequest;
import com.example.motovista_deep.ai.AiChatResponse;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIChatbotActivity extends AppCompatActivity {

    // Views
    private LinearLayout btnBack;
    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private CardView btnSend;
    private CardView btnShowMore;
    private CardView btnRefineSearch;
    private LinearLayout quickActionsLayout;

    // Adapter and data
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    // IMPROVEMENT 1: AI State Management
    private enum AiState {
        ASKING,
        WAITING_FOR_INPUT,
        RECOMMENDING
    }

    private AiState currentState = AiState.ASKING;

    // Handler for typing simulation
    private Handler handler = new Handler();
    private boolean isTypingIndicatorShown = false;
    private int typingIndicatorPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chatbot);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup chat recycler view
        setupChatRecyclerView();

        // Set initial state
        setAiState(AiState.ASKING);

        // Add initial bot message
        addInitialMessages();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnShowMore = findViewById(R.id.btnShowMore);
        btnRefineSearch = findViewById(R.id.btnRefineSearch);
        quickActionsLayout = findViewById(R.id.quickActionsLayout);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Send button
        btnSend.setOnClickListener(v -> {
            sendMessage();
        });

        // Send on enter
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Show more options
        btnShowMore.setOnClickListener(v -> {
            if (currentState == AiState.RECOMMENDING) {
                addBotMessage("Here are more options for you...");
                // You can add more bike recommendations here
            }
        });

        // Refine search
        btnRefineSearch.setOnClickListener(v -> {
            if (currentState == AiState.RECOMMENDING) {
                setAiState(AiState.ASKING);
                addBotMessage("Let's refine your search. What specific features are you looking for?");
            }
        });

        // Enable/disable send button based on input
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSend.setEnabled(s.toString().trim().length() > 0);
                btnSend.setAlpha(s.toString().trim().length() > 0 ? 1f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChatRecyclerView() {
        chatAdapter = new ChatAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void addInitialMessages() {
        // IMPROVEMENT 5: Better welcome message
        ChatMessage welcomeMessage = new ChatMessage(
                "Hi 👋 I'm your AI bike assistant.\n\nTell me your budget or press Start to begin.",
                getCurrentTime(),
                false,
                ChatMessage.TYPE_TEXT
        );
        chatMessages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();
    }

    // Helper method to get AI user ID
    private String getAiUserId() {
        try {
            SharedPrefManager sp = SharedPrefManager.getInstance(this);

            String userJson = getSharedPreferences(
                    "motovista_prefs", MODE_PRIVATE
            ).getString("user", null);

            if (userJson != null) {
                JSONObject obj = new JSONObject(userJson);
                return obj.getString("id"); // reads private field safely
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "guest";
    }


    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        // Remove typing indicator if shown
        removeTypingIndicator();

        // Add user message
        ChatMessage userMessage = new ChatMessage(
                message,
                getCurrentTime(),
                true,
                ChatMessage.TYPE_TEXT
        );
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);

        // Clear input
        etMessage.setText("");

        // Scroll to bottom
        scrollToBottom();

        // Change state to waiting for AI response
        setAiState(AiState.WAITING_FOR_INPUT);

        // Show typing indicator
        showTypingIndicator();

        // REAL BACKEND CALL - Replace the simulated AI delay
        String userId = getAiUserId();

        AiChatRequest request = new AiChatRequest(userId, message);

        RetrofitClient.getApiService()
                .chatWithAi(request)
                .enqueue(new Callback<AiChatResponse>() {
                    @Override
                    public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {

                        removeTypingIndicator();

                        if (response.isSuccessful() && response.body() != null) {
                            // Handle the AI response
                            handleAiResponse(response.body());
                        } else {
                            addBotMessage("Sorry 😕 I couldn’t understand that. Please try again.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AiChatResponse> call, Throwable t) {
                        removeTypingIndicator();
                        addBotMessage("⚠️ AI service is currently unavailable.");
                    }
                });
    }

    private void handleAiResponse(AiChatResponse aiResponse) {

        String aiReply = aiResponse.getReply();
        addBotMessage(aiReply);

        // 🔥 SHOW RECOMMENDATIONS IF PRESENT
        if (aiResponse.getRecommendations() != null &&
                !aiResponse.getRecommendations().isEmpty()) {

            setAiState(AiState.RECOMMENDING);

            for (AiChatResponse.Recommendation rec : aiResponse.getRecommendations()) {

                String bikeMsg =
                        "• " + rec.getBike() +
                                "\nConfidence: " + rec.getConfidence();

                addBotMessage(bikeMsg);
            }

        } else {
            setAiState(AiState.ASKING);
        }
    }


    private void handleUserInput(String input) {
        // This method is now only for fallback if backend fails
        // Simulate AI responses based on user input
        if (input.contains("start") || input.contains("begin") || input.contains("hello") || input.contains("hi")) {
            setAiState(AiState.ASKING);
            addBotMessage("Great! Let's find your dream bike. Are you looking for new bikes?");
        }
        else if (input.contains("new bike") || input.contains("new bikes") || input.contains("yes")) {
            setAiState(AiState.ASKING);
            addBotMessage("Great choice! To narrow it down, do you have a specific budget range in mind?");
        }
        else if ((input.contains("1.5") && input.contains("2.5")) ||
                input.contains("1.5l") || input.contains("2.5l") ||
                input.contains("1.5 l") || input.contains("2.5 l")) {
            setAiState(AiState.RECOMMENDING);
            showBikeRecommendations();
        }
        else if (input.contains("budget") || input.contains("price")) {
            setAiState(AiState.ASKING);
            addBotMessage("We have bikes in various price ranges. Could you please specify your budget?\n\nFor example: ₹1.5L - ₹2.5L");
        }
        else if (input.contains("help") || input.contains("assist")) {
            setAiState(AiState.ASKING);
            addBotMessage("I can help you with:\n• Finding new bikes\n• Budget recommendations\n• Bike comparisons\n• Booking test rides\n• Service information\n\nWhat would you like to know?");
        }
        else {
            setAiState(AiState.ASKING);
            addBotMessage("I understand you're looking for: " + input + "\n\nCould you tell me more about what you're looking for? For example, you can ask about:\n• New bikes\n• Budget range\n• Bike types\n• Features");
        }
    }

    private void addBotMessage(String message) {
        ChatMessage botMessage = new ChatMessage(
                message,
                getCurrentTime(),
                false,
                ChatMessage.TYPE_TEXT
        );
        chatMessages.add(botMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();
    }

    private void showBikeRecommendations() {
        // This will show the bike cards in the chat
        ChatMessage recommendationMessage = new ChatMessage(
                "Perfect. Based on your budget, I highly recommend these 2 models for you:",
                getCurrentTime(),
                false,
                ChatMessage.TYPE_RECOMMENDATION
        );
        chatMessages.add(recommendationMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();
    }

    // IMPROVEMENT 1: AI State Management
    private void setAiState(AiState state) {
        this.currentState = state;
        updateQuickActions();

        // Clear input if AI is recommending
        if (state == AiState.RECOMMENDING) {
            etMessage.setText("");
        }
    }

    // IMPROVEMENT 2: Update Quick Actions based on state
    private void updateQuickActions() {
        if (currentState == AiState.RECOMMENDING) {
            quickActionsLayout.setVisibility(View.VISIBLE);
        } else {
            quickActionsLayout.setVisibility(View.GONE);
        }
    }

    // IMPROVEMENT 3: Typing Indicator
    private void showTypingIndicator() {
        if (isTypingIndicatorShown) return;

        ChatMessage typing = new ChatMessage(
                "AI is typing...",
                getCurrentTime(),
                false,
                ChatMessage.TYPE_TEXT
        );
        chatMessages.add(typing);
        typingIndicatorPosition = chatMessages.size() - 1;
        chatAdapter.notifyItemInserted(typingIndicatorPosition);
        isTypingIndicatorShown = true;
        scrollToBottom();
    }

    private void removeTypingIndicator() {
        if (isTypingIndicatorShown && typingIndicatorPosition >= 0) {
            chatMessages.remove(typingIndicatorPosition);
            chatAdapter.notifyItemRemoved(typingIndicatorPosition);
            isTypingIndicatorShown = false;
            typingIndicatorPosition = -1;
        }
    }

    private void scrollToBottom() {
        if (chatMessages.size() > 0) {
            rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Chat Message Model Class
    public static class ChatMessage {
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_RECOMMENDATION = 2;

        private String message;
        private String time;
        private boolean isUser;
        private int type;

        public ChatMessage(String message, String time, boolean isUser, int type) {
            this.message = message;
            this.time = time;
            this.isUser = isUser;
            this.type = type;
        }

        public String getMessage() { return message; }
        public String getTime() { return time; }
        public boolean isUser() { return isUser; }
        public int getType() { return type; }

        // Helper method to check if it's a question
        public boolean isQuestion() {
            return !isUser && message.trim().endsWith("?");
        }
    }

    // Chat Adapter Class
    public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_USER = 1;
        private static final int TYPE_BOT = 2;
        private static final int TYPE_RECOMMENDATION = 3;

        private List<ChatMessage> messages;

        public ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage message = messages.get(position);
            if (message.isUser()) {
                return TYPE_USER;
            } else if (message.getType() == ChatMessage.TYPE_RECOMMENDATION) {
                return TYPE_RECOMMENDATION;
            } else {
                return TYPE_BOT;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_RECOMMENDATION) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_message, parent, false);
                return new RecommendationViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_message, parent, false);
                return new MessageViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatMessage message = messages.get(position);

            if (holder.getItemViewType() == TYPE_RECOMMENDATION) {
                RecommendationViewHolder rvh = (RecommendationViewHolder) holder;
                rvh.bind(message);
            } else {
                MessageViewHolder mvh = (MessageViewHolder) holder;
                mvh.bind(message);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        // Message ViewHolder for regular text messages
        public class MessageViewHolder extends RecyclerView.ViewHolder {
            LinearLayout botMessageLayout, userMessageLayout;
            TextView tvBotMessage, tvBotTime, tvUserMessage, tvUserTime;

            public MessageViewHolder(View itemView) {
                super(itemView);
                botMessageLayout = itemView.findViewById(R.id.botMessageLayout);
                userMessageLayout = itemView.findViewById(R.id.userMessageLayout);
                tvBotMessage = itemView.findViewById(R.id.tvBotMessage);
                tvBotTime = itemView.findViewById(R.id.tvBotTime);
                tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
                tvUserTime = itemView.findViewById(R.id.tvUserTime);
            }

            public void bind(ChatMessage message) {
                if (message.isUser()) {
                    botMessageLayout.setVisibility(View.GONE);
                    userMessageLayout.setVisibility(View.VISIBLE);
                    tvUserMessage.setText(message.getMessage());
                    tvUserTime.setText(message.getTime());
                } else {
                    userMessageLayout.setVisibility(View.GONE);
                    botMessageLayout.setVisibility(View.VISIBLE);
                    tvBotMessage.setText(message.getMessage());
                    tvBotTime.setText(message.getTime());

                    // IMPROVEMENT 4: Question Styling - Only text styling
                    if (message.isQuestion()) {
                        // Make question text bold
                        tvBotMessage.setTypeface(null, Typeface.BOLD);
                    } else {
                        tvBotMessage.setTypeface(null, Typeface.NORMAL);
                    }

                    // Special styling for typing indicator
                    if (message.getMessage().equals("AI is typing...")) {
                        tvBotMessage.setTypeface(null, Typeface.ITALIC);
                        tvBotMessage.setTextColor(
                                getResources().getColor(R.color.gray_500)
                        );
                    } else {
                        tvBotMessage.setTextColor(
                                getResources().getColor(R.color.text_dark)
                        );
                    }
                }
            }
        }

        // Recommendation ViewHolder for bike recommendations
        public class RecommendationViewHolder extends RecyclerView.ViewHolder {
            LinearLayout recommendationLayout;
            TextView tvRecommendationMessage, tvRecommendationTime;
            androidx.cardview.widget.CardView cardBike1, cardBike2;
            Button btnViewDetails1, btnBuyNow1, btnViewDetails2, btnBuyNow2;

            public RecommendationViewHolder(View itemView) {
                super(itemView);
                recommendationLayout = itemView.findViewById(R.id.recommendationLayout);
                tvRecommendationMessage = itemView.findViewById(R.id.tvRecommendationMessage);
                tvRecommendationTime = itemView.findViewById(R.id.tvRecommendationTime);

                cardBike1 = itemView.findViewById(R.id.cardBike1);
                cardBike2 = itemView.findViewById(R.id.cardBike2);

                btnViewDetails1 = itemView.findViewById(R.id.btnViewDetails1);
                btnBuyNow1 = itemView.findViewById(R.id.btnBuyNow1);
                btnViewDetails2 = itemView.findViewById(R.id.btnViewDetails2);
                btnBuyNow2 = itemView.findViewById(R.id.btnBuyNow2);

                setupClickListeners();
            }

            private void setupClickListeners() {
                // Bike 1 buttons
                btnViewDetails1.setOnClickListener(v -> {
                    Toast.makeText(AIChatbotActivity.this, "View Adventure Tourer Pro Details", Toast.LENGTH_SHORT).show();
                });

                btnBuyNow1.setOnClickListener(v -> {
                    Toast.makeText(AIChatbotActivity.this, "Buy Adventure Tourer Pro", Toast.LENGTH_SHORT).show();
                });

                // Bike 2 buttons
                btnViewDetails2.setOnClickListener(v -> {
                    Toast.makeText(AIChatbotActivity.this, "View Street Fighter 200 Details", Toast.LENGTH_SHORT).show();
                });

                btnBuyNow2.setOnClickListener(v -> {
                    Toast.makeText(AIChatbotActivity.this, "Buy Street Fighter 200", Toast.LENGTH_SHORT).show();
                });
            }

            public void bind(ChatMessage message) {
                recommendationLayout.setVisibility(View.VISIBLE);
                tvRecommendationMessage.setText(message.getMessage());
                tvRecommendationTime.setText(message.getTime());

                // IMPROVEMENT 4: Make recommendation message bold
                tvRecommendationMessage.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}