package com.example.motovista_deep.api;

import com.example.motovista_deep.models.LoginRequest;
import com.example.motovista_deep.models.LoginResponse;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.RegisterRequest;
import com.example.motovista_deep.models.RegisterResponse;
import com.example.motovista_deep.models.GetProfileResponse;
import com.example.motovista_deep.models.GetCustomersResponse;
import com.example.motovista_deep.models.GetCustomerDetailResponse;
import com.example.motovista_deep.models.AddBikeRequest;
import com.example.motovista_deep.models.UploadBikeImageResponse;
import com.example.motovista_deep.models.SecondHandBikeRequest;
import com.example.motovista_deep.models.GetBikesResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // ✅ CUSTOMER LOGIN
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register.php")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @Multipart
    @POST("profile_update.php")
    Call<GenericResponse> updateProfile(
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

    @GET("get_profile.php")
    Call<GetProfileResponse> getProfile(
            @Header("Authorization") String token
    );


    @POST("admin_login.php")
    Call<LoginResponse> adminLogin(@Body LoginRequest request);

    @GET("admin_get_customers.php")
    Call<GetCustomersResponse> getCustomers(
            @Header("Authorization") String token
    );

    @GET("admin_get_customer_detail.php")
    Call<GetCustomerDetailResponse> getCustomerDetail(
            @Header("Authorization") String token,
            @Query("customer_id") int customerId
    );

    @POST("add_bike.php")
    Call<GenericResponse> addBike(
            @Header("Authorization") String token,
            @Body AddBikeRequest request
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


    // Add these methods to your existing ApiService interface
    @GET("get_all_bikes.php")
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


}
