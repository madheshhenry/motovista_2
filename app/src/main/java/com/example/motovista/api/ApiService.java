package com.example.motovista.api;

import com.example.motovista.models.ApiResponse;
import com.example.motovista.models.LoginRequest;
import com.example.motovista.models.SignupRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.POST;

public interface ApiService {

    // LOGIN
    @POST("login.php")
    Call<ApiResponse> login(@Body LoginRequest body);

    // SIGNUP
    @POST("signup.php")
    Call<ApiResponse> signup(@Body SignupRequest body);

    // ADMIN LOGIN
    @FormUrlEncoded
    @POST("admin_login.php")
    Call<ResponseBody> adminLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    // ⭐⭐⭐ PROFILE SETUP MULTIPART API (THIS WAS MISSING!)
    @Multipart
    @POST("complete_profile.php")
    Call<ApiResponse> completeProfile(
            @Part("user_id") RequestBody userId,
            @Part("dob") RequestBody dob,
            @Part("house_no") RequestBody houseNo,
            @Part("street_locality") RequestBody street,
            @Part("city") RequestBody city,
            @Part("state") RequestBody state,
            @Part("pincode") RequestBody pincode,
            @Part("pan_number") RequestBody pan,

            @Part MultipartBody.Part profile_photo,
            @Part MultipartBody.Part aadhar_front,
            @Part MultipartBody.Part aadhar_back
    );
}
