package com.example.motovista_deep.api;

import com.example.motovista_deep.models.CompleteProfileRequest;
import com.example.motovista_deep.models.CustomerRequest;
import com.example.motovista_deep.models.DeleteBikeRequest;
import com.example.motovista_deep.models.GetBikeByIdResponse;
import com.example.motovista_deep.models.GetCustomerRequestsResponse;
import com.example.motovista_deep.models.GetSecondHandBikeByIdResponse;
import com.example.motovista_deep.models.InventoryResponse;
import com.example.motovista_deep.models.LoginRequest;
import com.example.motovista_deep.models.LoginResponse;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.ProfileUpdateRequest;
import com.example.motovista_deep.models.ProfileUpdateResponse;
import com.example.motovista_deep.models.RegisterRequest;
import com.example.motovista_deep.models.RegisterResponse;
import com.example.motovista_deep.models.GetProfileResponse;
import com.example.motovista_deep.models.GetCustomersResponse;
import com.example.motovista_deep.models.GetCustomerDetailResponse;
import com.example.motovista_deep.models.AddBikeRequest;
import com.example.motovista_deep.models.RequestResponse;
import com.example.motovista_deep.models.UpdateBikeRequest;
import com.example.motovista_deep.models.UpdateProfileRequest;
import com.example.motovista_deep.models.UpdateSecondHandBikeRequest;
import com.example.motovista_deep.models.UploadBikeImageResponse;
import com.example.motovista_deep.models.SecondHandBikeRequest;
import com.example.motovista_deep.models.GetBikesResponse;
import com.example.motovista_deep.ai.AiChatRequest;
import com.example.motovista_deep.ai.AiChatResponse;
import com.example.motovista_deep.models.OtpRequest;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface ApiService {

    // ✅ CUSTOMER LOGIN (PHP BACKEND)


    // ✅ CUSTOMER REGISTRATION (PHP BACKEND)


    // ✅ GET PROFILE (PHP BACKEND) - CHANGED FROM get_profile.php

    // ✅ UPDATE PROFILE (PHP BACKEND) - CHANGED FROM profile_update.php
    @POST("update_profile.php")
    Call<GenericResponse> updateProfile(
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

    // ✅ KEEP OLD UPDATE PROFILE (for backward compatibility)
    @Multipart
    @POST("profile_update.php")
    Call<GenericResponse> updateProfileMultipart(
            @Header("Authorization") String token,
            @Part MultipartBody.Part profile_image,
            @Part MultipartBody.Part aadhar_front,
            @Part MultipartBody.Part aadhar_back,
            @Part("dob") RequestBody dob,
            @Part("house_no") RequestBody house_no,
            @Part("street") RequestBody street,
            @Part("city") RequestBody city,
            @Part("state") RequestBody state,
            @Part("pincode") RequestBody pincode,
            @Part("pan") RequestBody pan
    );

    // ✅ VERIFY EMAIL (PHP BACKEND)
    @POST("verify_email_link.php")
    Call<GenericResponse> verifyEmail(@Body VerifyEmailRequest request);

    // ✅ RESEND VERIFICATION EMAIL (PHP BACKEND)
    @POST("resend_verification_email.php")
    Call<GenericResponse> resendVerification(@Body ResendVerificationRequest request);

    // ✅ ADMIN LOGIN (YOUR OLD BACKEND)
    @POST("admin_send_otp.php")
    Call<GenericResponse> adminSendOtp(@Body LoginRequest request); // We reuse LoginRequest (email, password)

    @POST("admin_verify_otp.php")
    Call<LoginResponse> adminVerifyOtp(@Body OtpRequest request); // Need a new (or reused) OtpRequest


    @GET("admin_get_customer_detail.php")
    Call<GetCustomerDetailResponse> getCustomerDetail(
            @Header("Authorization") String token,
            @Query("customer_id") int customerId
    );


    @POST("add_second_hand_bike.php")
    Call<GenericResponse> addSecondHandBike(
            @Header("Authorization") String token,
            @Body SecondHandBikeRequest request
    );

    @Multipart
    @POST("upload_bike_image.php")
    Call<UploadBikeImageResponse> uploadBikeImages(
            @Header("Authorization") String token,
            @Part("bike_type") RequestBody bikeType,
            @Part List<MultipartBody.Part> bike_images
    );

    @GET("get_bikes.php")
    Call<GetBikesResponse> getAllBikes(
            @Header("Authorization") String token
    );

    @GET("get_new_bikes.php")
    Call<GetBikesResponse> getNewBikes(
            @Header("Authorization") String token
    );

    @GET("get_second_hand_bikes.php")
    Call<GetBikesResponse> getSecondHandBikes(
            @Header("Authorization") String token
    );

    @POST("ai_chat.php")
    Call<AiChatResponse> chatWithAi(@Body AiChatRequest request);

    @POST("delete_bike.php")
    Call<GenericResponse> deleteBike(
            @Header("Authorization") String token,
            @Body DeleteBikeRequest request
    );

    @POST("delete_second_hand_bike.php")
    Call<GenericResponse> deleteSecondHandBike(
            @Header("Authorization") String token,
            @Body DeleteBikeRequest request
    );

    @POST("update_bike.php")
    Call<GenericResponse> updateBike(
            @Header("Authorization") String token,
            @Body UpdateBikeRequest request
    );

    @POST("update_second_hand_bike.php")
    Call<GenericResponse> updateSecondHandBike(
            @Header("Authorization") String token,
            @Body UpdateSecondHandBikeRequest request
    );

    @GET("get_bike_details.php")
    Call<GetBikeByIdResponse> getBikeById(
            @Header("Authorization") String token,
            @Query("id") int bikeId
    );

    @GET("get_second_hand_bike_by_id.php")
    Call<GetSecondHandBikeByIdResponse> getSecondHandBikeById(
            @Header("Authorization") String token,
            @Query("bike_id") int bikeId
    );

    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register.php")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // This matches our update_profile.php script
    @Multipart
    @POST("update_profile.php")
    Call<ProfileUpdateResponse> updateProfile(
            @Header("Authorization") String token,
            @Part MultipartBody.Part profile_image,
            @Part MultipartBody.Part aadhar_front,
            @Part MultipartBody.Part aadhar_back,
            @Part("full_name") RequestBody full_name,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("dob") RequestBody dob,
            @Part("house_no") RequestBody house_no,
            @Part("street") RequestBody street,
            @Part("city") RequestBody city,
            @Part("state") RequestBody state,
            @Part("pincode") RequestBody pincode,
            @Part("pan_no") RequestBody pan_no
    );
    @GET("get_all_customers.php")
    Call<GetCustomersResponse> getCustomers(@Header("Authorization") String token);
    @POST("update_profile_complete.php")
    @Headers("Content-Type: application/json")
    Call<ProfileUpdateResponse> completeProfile(
            @Header("Authorization") String token,
            @Body ProfileUpdateRequest request
    );
    @POST("add_bike.php")
    Call<GenericResponse> addBike(@Header("Authorization") String token, @Body AddBikeRequest request);
    @GET("profile.php")
    Call<GetProfileResponse> getProfile(@Header("Authorization") String token);

    @GET("get_inventory.php")
    Call<InventoryResponse> getInventory(@Header("Authorization") String token);

    // ✅ MULTIPART PROFILE UPDATE (WITH IMAGES)
    @Multipart
    @POST("upload_profile_images.php")
    Call<GenericResponse> uploadProfileWithImages(
            @Header("Authorization") String token,
            @Part MultipartBody.Part profile_image,
            @Part MultipartBody.Part aadhar_front,
            @Part MultipartBody.Part aadhar_back,
            @Part("dob") RequestBody dob,
            @Part("house_no") RequestBody house_no,
            @Part("street") RequestBody street,
            @Part("city") RequestBody city,
            @Part("state") RequestBody state,
            @Part("pincode") RequestBody pincode,
            @Part("pan") RequestBody pan
    );

    // Inner classes for verification requests
    class VerifyEmailRequest {
        private String email;
        private String verification_code;

        public VerifyEmailRequest(String email, String verification_code) {
            this.email = email;
            this.verification_code = verification_code;
        }

        public String getEmail() { return email; }
        public String getVerification_code() { return verification_code; }
    }

    class ResendVerificationRequest {
        private String email;

        public ResendVerificationRequest(String email) {
            this.email = email;
        }

        public String getEmail() { return email; }
    }

    
    @POST("add_customer_request.php")
    Call<RequestResponse> addCustomerRequest(@Body CustomerRequest request);

    @GET("get_customer_requests.php")
    Call<GetCustomerRequestsResponse> getCustomerRequests();

    @POST("update_request_status.php")
    Call<GenericResponse> updateRequestStatus(@Body com.example.motovista_deep.models.UpdateRequestStatusRequest request);

    @POST("complete_order.php")
    Call<GenericResponse> completeOrder(@Body com.example.motovista_deep.models.CompleteOrderRequest request);

    @POST("delete_customer_request.php")
    Call<GenericResponse> deleteCustomerRequest(@Body com.example.motovista_deep.models.DeleteRequestRequest request);

    @GET("get_order_summary.php")
    Call<com.example.motovista_deep.models.GetOrderSummaryResponse> getOrderSummary(@Query("request_id") int requestId);

    @Multipart
    @POST("add_brand.php")
    Call<GenericResponse> addBrand(
            @Header("Authorization") String token,
            @Part("brand_name") RequestBody brandName,
            @Part MultipartBody.Part brandLogo
    );

    @GET("get_brands.php")
    Call<com.example.motovista_deep.models.InventoryResponse> getBrands(@Header("Authorization") String token);

    @GET("get_brand_bikes.php")
    Call<com.example.motovista_deep.models.BikeListResponse> getBikesByBrand(
            @Header("Authorization") String token,
            @Query("brand") String brand
    );

    @FormUrlEncoded
    @POST("add_stock_bike.php")
    Call<GenericResponse> addStockBike(
            @Header("Authorization") String token,
            @Field("brand") String brand,
            @Field("model") String model,
            @Field("variant") String variant,
            @Field("colors") String colors,
            @Field("engine_number") String engineNumber,
            @Field("chassis_number") String chassisNumber,
            @Field("date") String date
    );
}