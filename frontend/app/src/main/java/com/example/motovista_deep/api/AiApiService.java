package com.example.motovista_deep.api;

import com.example.motovista_deep.ai.AiChatRequest;
import com.example.motovista_deep.ai.AiChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiApiService {
    @POST("chat")
    Call<AiChatResponse> chatWithAi(@Body AiChatRequest request);
}
