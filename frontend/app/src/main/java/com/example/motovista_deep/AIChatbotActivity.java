package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.motovista_deep.api.AiApiService;
import com.example.motovista_deep.api.AiRetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AIChatbotActivity extends AppCompatActivity {

    // Views
    private LinearLayout btnBack;
    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private CardView btnSend;
    private LinearLayout quickActionsLayout; // Now used for dynamic chips

    // Adapter and data
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    // Handler for typing simulation
    private boolean isTypingIndicatorShown = false;
    private int typingIndicatorPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chatbot);

        initializeViews();
        setupClickListeners();
        setupChatRecyclerView();

        // Restore History or Start New
        if (ChatRepository.getInstance().hasMessages()) {
            chatMessages.addAll(ChatRepository.getInstance().getMessages());
            chatAdapter.notifyDataSetChanged();
            rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        } else {
            // Start the AI Conversation ONLY if no history
            startConversation();
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        quickActionsLayout = findViewById(R.id.quickActionsLayout); // Container for Options
        
        // Hide unused buttons from previous layout if they exist via find, or just ignore
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage(etMessage.getText().toString().trim()));

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(etMessage.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void setupChatRecyclerView() {
        chatAdapter = new ChatAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void startConversation() {
        // Send hidden "START" message to init state
        showTypingIndicator();
        AiRetrofitClient.getApiService().chatWithAi(new AiChatRequest("user", "START"))
                .enqueue(new Callback<AiChatResponse>() {
                    @Override
                    public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {
                        removeTypingIndicator();
                        if (response.isSuccessful() && response.body() != null) {
                            handleAiResponse(response.body());
                        } else {
                            addBotMessage("⚠️ Connection Failed. Ensure Python server is running.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AiChatResponse> call, Throwable t) {
                        removeTypingIndicator();
                        addBotMessage("⚠️ Network Error. Is port 5000 open?");
                    }
                });
    }

    private void sendMessage(String message) {
        if (message.isEmpty()) return;

        // User Message
        addMessage(new ChatMessage(message, getCurrentTime(), true, ChatMessage.TYPE_TEXT, null));
        etMessage.setText("");
        
        // Clear Options while thinking
        quickActionsLayout.removeAllViews();

        // AI Request
        showTypingIndicator();
        AiRetrofitClient.getApiService().chatWithAi(new AiChatRequest("user", message))
                .enqueue(new Callback<AiChatResponse>() {
                    @Override
                    public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {
                        removeTypingIndicator();
                        if (response.isSuccessful() && response.body() != null) {
                            handleAiResponse(response.body());
                        } else {
                            addBotMessage("I'm having trouble thinking right now.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AiChatResponse> call, Throwable t) {
                        removeTypingIndicator();
                        addBotMessage("⚠️ Server unreachable.");
                    }
                });
    }

    private void handleAiResponse(AiChatResponse response) {
        // 1. Show Text Message
        if (response.getMessage() != null && !response.getMessage().isEmpty()) {
            addBotMessage(response.getMessage());
        }

        // 2. Show Options (Dynamic Chips)
        quickActionsLayout.removeAllViews();
        List<String> options = response.getOptions();
        if (options != null && !options.isEmpty()) {
            for (String opt : options) {
                addOptionChip(opt);
            }
        }

        // 3. Show Recommendations (if any)
        if ("recommendation".equals(response.getType()) && response.getData() != null) {
            // Pass data to adapter to render specific card
            addRecommendationMessage(response.getData());
        }
    }

    private void addOptionChip(String text) {
        // Create a simple styled button acting as a chip
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 16, 0);
        card.setLayoutParams(params);
        card.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
        card.setRadius(50f);
        card.setCardElevation(0f);

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(32, 16, 32, 16);
        tv.setTextColor(getResources().getColor(R.color.primary_color));
        tv.setTypeface(null, Typeface.BOLD);
        
        card.addView(tv);
        
        card.setOnClickListener(v -> sendMessage(text)); // Send option text as message
        
        quickActionsLayout.addView(card);
    }

    private void addBotMessage(String text) {
        addMessage(new ChatMessage(text, getCurrentTime(), false, ChatMessage.TYPE_TEXT, null));
    }

    private void addRecommendationMessage(List<AiChatResponse.RecommendationData> data) {
         addMessage(new ChatMessage("Here are the best matches:", getCurrentTime(), false, ChatMessage.TYPE_RECOMMENDATION, data));
    }

    private void addMessage(ChatMessage msg) {
        chatMessages.add(msg);
        
        // Save to Valid Repository logic
        // Only save real messages, not temporary typing indicators
        if (!msg.message.equals("AI is thinking...")) {
             ChatRepository.getInstance().addMessage(msg);
        }
        
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
    }

    // TYPING INDICATOR
    private void showTypingIndicator() {
        if (isTypingIndicatorShown) return;
        ChatMessage typing = new ChatMessage("AI is thinking...", getCurrentTime(), false, ChatMessage.TYPE_TEXT, null);
        chatMessages.add(typing);
        typingIndicatorPosition = chatMessages.size() - 1;
        chatAdapter.notifyItemInserted(typingIndicatorPosition);
        isTypingIndicatorShown = true;
        rvChatMessages.scrollToPosition(typingIndicatorPosition);
    }

    private void removeTypingIndicator() {
        if (isTypingIndicatorShown && typingIndicatorPosition >= 0 && typingIndicatorPosition < chatMessages.size()) {
            chatMessages.remove(typingIndicatorPosition);
            chatAdapter.notifyItemRemoved(typingIndicatorPosition);
            isTypingIndicatorShown = false;
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    // --- INNER CLASSES ---

    public static class ChatMessage {
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_RECOMMENDATION = 2;

        private String message;
        private String time;
        private boolean isUser;
        private int type;
        private List<AiChatResponse.RecommendationData> recData;

        public ChatMessage(String message, String time, boolean isUser, int type, List<AiChatResponse.RecommendationData> recData) {
            this.message = message;
            this.time = time;
            this.isUser = isUser;
            this.type = type;
            this.recData = recData;
        }

        public List<AiChatResponse.RecommendationData> getRecData() { return recData; }
    }

    public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ChatMessage> messages;

        public ChatAdapter(List<ChatMessage> messages) { this.messages = messages; }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).type == ChatMessage.TYPE_RECOMMENDATION ? 2 : 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == 2) return new RecViewHolder(inflater.inflate(R.layout.item_chat_message, parent, false));
            return new MsgViewHolder(inflater.inflate(R.layout.item_chat_message, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            if (holder instanceof RecViewHolder) ((RecViewHolder) holder).bind(msg);
            else ((MsgViewHolder) holder).bind(msg);
        }

        @Override
        public int getItemCount() { return messages.size(); }

        class MsgViewHolder extends RecyclerView.ViewHolder {
            LinearLayout botLayout, userLayout;
            TextView botTv, userTv, botTime, userTime;

            MsgViewHolder(View v) {
                super(v);
                botLayout = v.findViewById(R.id.botMessageLayout);
                userLayout = v.findViewById(R.id.userMessageLayout);
                botTv = v.findViewById(R.id.tvBotMessage);
                userTv = v.findViewById(R.id.tvUserMessage);
                botTime = v.findViewById(R.id.tvBotTime);
                userTime = v.findViewById(R.id.tvUserTime);
                v.findViewById(R.id.recommendationLayout).setVisibility(View.GONE);
            }

            void bind(ChatMessage msg) {
                if (msg.isUser) {
                    botLayout.setVisibility(View.GONE);
                    userLayout.setVisibility(View.VISIBLE);
                    userTv.setText(msg.message);
                    userTime.setText(msg.time);
                } else {
                    userLayout.setVisibility(View.GONE);
                    botLayout.setVisibility(View.VISIBLE);
                    botTv.setText(msg.message);
                    botTime.setText(msg.time);
                    if(msg.message.equals("AI is thinking...")) botTv.setTypeface(null, Typeface.ITALIC);
                    else botTv.setTypeface(null, Typeface.NORMAL);
                }
            }
        }

        class RecViewHolder extends RecyclerView.ViewHolder {
            LinearLayout recLayout;
            TextView title;
            TextView name1, desc1, price1;
            TextView name2, desc2, price2;
            CardView card1, card2;

            RecViewHolder(View v) {
                super(v);
                recLayout = v.findViewById(R.id.recommendationLayout);
                title = v.findViewById(R.id.tvRecommendationMessage);
                
                // Existing layout IDs might differ slightly, assume standard layout access
                // I need to bind data to cardBike1 and cardBike2 sub-views
                card1 = v.findViewById(R.id.cardBike1);
                card2 = v.findViewById(R.id.cardBike2);
                
                v.findViewById(R.id.botMessageLayout).setVisibility(View.GONE);
                v.findViewById(R.id.userMessageLayout).setVisibility(View.GONE);
            }

            void bind(ChatMessage msg) {
                recLayout.setVisibility(View.VISIBLE);
                title.setText(msg.message);
                
                List<AiChatResponse.RecommendationData> data = msg.getRecData();
                if(data != null) {
                    if (data.size() > 0) bindCard(card1, data.get(0), 1);
                    else card1.setVisibility(View.GONE);
                    
                    if (data.size() > 1) bindCard(card2, data.get(1), 2);
                    else card2.setVisibility(View.GONE);
                }
            }

            void bindCard(CardView card, AiChatResponse.RecommendationData d, int index) {
                card.setVisibility(View.VISIBLE);
                
                // Identify Views based on Index (1 or 2)
                TextView tvTitle = card.findViewById(index == 1 ? R.id.tvBike1Title : R.id.tvBike2Title);
                TextView tvPrice = card.findViewById(index == 1 ? R.id.tvBike1Price : R.id.tvBike2Price);
                Button btnDetails = card.findViewById(index == 1 ? R.id.btnViewDetails1 : R.id.btnViewDetails2);
                Button btnBuy = card.findViewById(index == 1 ? R.id.btnBuyNow1 : R.id.btnBuyNow2);
                ImageView ivBike = card.findViewById(index == 1 ? R.id.ivBike1 : R.id.ivBike2);
                
                // Bind Data
                if (tvTitle != null) tvTitle.setText(d.getName());
                if (tvPrice != null) tvPrice.setText("₹ " + d.getPrice());
                
                // Load Image
                if (ivBike != null && d.getImage() != null && !d.getImage().isEmpty()) {
                     Glide.with(itemView.getContext())
                          .load(d.getImage())
                          .apply(new RequestOptions().placeholder(R.color.gray_200).error(R.color.gray_400))
                          .into(ivBike);
                }
                
                // Click Listeners
                if (btnDetails != null) {
                    btnDetails.setOnClickListener(v -> 
                        Toast.makeText(itemView.getContext(), "Details: " + d.getName(), Toast.LENGTH_SHORT).show()
                    );
                }
                
                if (btnBuy != null) {
                    btnBuy.setOnClickListener(v -> 
                        Toast.makeText(itemView.getContext(), "Booking: " + d.getName(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        }
    }
}