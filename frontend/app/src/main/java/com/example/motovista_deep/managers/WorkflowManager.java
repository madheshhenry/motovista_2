package com.example.motovista_deep.managers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.motovista_deep.AdminDashboardActivity;
import com.example.motovista_deep.DocumentsActivity;
import com.example.motovista_deep.OrderCompletedActivity;
import com.example.motovista_deep.PaymentConfirmedActivity;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.helpers.SharedPrefManager;
import com.example.motovista_deep.models.GenericResponse;
import com.example.motovista_deep.models.UpdateWorkflowRequest;
import com.example.motovista_deep.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkflowManager {

    public interface WorkflowCallback {
        void onSuccess();
        void onError(String message);
    }

    public static void updateStage(Context context, String stage, Integer orderId, WorkflowCallback callback) {
        String token = SharedPrefManager.getInstance(context).getToken();
        if (token == null) {
            callback.onError("Token missing");
            return;
        }
        
        // Ensure "Bearer " prefix
        if (!token.startsWith("Bearer ")) token = "Bearer " + token;

        UpdateWorkflowRequest request = new UpdateWorkflowRequest(stage, orderId);
        
        RetrofitClient.getApiService().updateAdminWorkflow(token, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Update Local User Object
                    User user = SharedPrefManager.getInstance(context).getUser();
                    if (user != null) {
                        user.setWorkflow_stage(stage);
                        user.setActive_order_id(orderId);
                        SharedPrefManager.getInstance(context).updateUser(user);
                    }
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to update workflow");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Returns true if redirection was handled (activity started)
    public static boolean checkAndRedirect(Context context) {
        User user = SharedPrefManager.getInstance(context).getUser();
        if (user == null) return false;

        String stage = user.getWorkflow_stage();
        Integer orderId = user.getActive_order_id();
        
        if (stage == null || stage.isEmpty() || stage.equals("NULL")) return false;

        Intent intent = null;
        
        // Map stages to Activities
        if (stage.equals("PAYMENT_CONFIRMED")) {
            intent = new Intent(context, PaymentConfirmedActivity.class);
        } else if (stage.equals("DOCUMENTS")) {
            intent = new Intent(context, DocumentsActivity.class);
        } else if (stage.equals("ORDER_COMPLETED")) {
            intent = new Intent(context, OrderCompletedActivity.class);
        }
        
        if (intent != null) {
            if (context instanceof android.app.Activity) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
                context.startActivity(intent);
                ((android.app.Activity) context).finish();
                return true;
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return true;
            }
        }
        
        return false;
    }
}
