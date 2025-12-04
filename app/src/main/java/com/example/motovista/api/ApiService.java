package com.example.motovista.api;

import com.example.motovista.models.ApiResponse;
import com.example.motovista.models.LoginRequest;
import com.example.motovista.models.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login.php")
    Call<ApiResponse> login(@Body LoginRequest body);

    @POST("signup.php")
    Call<ApiResponse> signup(@Body SignupRequest body);
}
