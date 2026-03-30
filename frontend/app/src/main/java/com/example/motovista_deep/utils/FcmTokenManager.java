package com.example.motovista_deep.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.SaveFcmTokenRequest;
import com.example.motovista_deep.models.User;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmTokenManager {
    private static final String TAG = "FcmTokenManager";

    public static void init(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    sendTokenToServer(context, token);
                });
    }

    public static void sendTokenToServer(Context context, String token) {
        User user = SharedPrefManager.getInstance(context).getUser();
        if (user == null) {
            Log.d(TAG, "User not logged in, skipping token sync");
            return;
        }

        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        String userType = SharedPrefManager.getInstance(context).getRole();
        if (userType == null || userType.isEmpty()) userType = "customer";

        SaveFcmTokenRequest request = new SaveFcmTokenRequest(user.getId(), token, deviceName, userType);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.saveFcmToken(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token synced with server");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.e(TAG, "Failed to sync token: " + t.getMessage());
            }
        });
    }
}
