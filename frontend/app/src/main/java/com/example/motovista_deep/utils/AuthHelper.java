package com.example.motovista_deep.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.motovista_deep.RoleSelectionActivity;
import com.example.motovista_deep.helpers.SharedPrefManager;

public class AuthHelper {

    /**
     * Handles authentication failures by checking if the session is expired or unauthorized.
     *
     * @param context The context from which to start the login activity.
     * @param statusCode The HTTP status code from the API response (e.g., 401).
     * @param message The error message from the API.
     * @return true if the session was handled (expired/unauthorized), false otherwise.
     */
    public static boolean handleAuthFailure(Context context, int statusCode, String message) {
        boolean isUnauthorized = (statusCode == 401) || 
                                (message != null && (message.equalsIgnoreCase("Unauthorized access") || 
                                                     message.equalsIgnoreCase("Authorization token missing")));
        
        if (isUnauthorized) {
            // Clear all user data
            SharedPrefManager.getInstance(context).clear();

            // Notify user
            Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_LONG).show();

            // Redirect to Login (Selection screen)
            Intent intent = new Intent(context, RoleSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            
            return true;
        }
        return false;
    }

    // Keep the old signature for backward compatibility if needed, but updated to call new one
    public static boolean handleAuthFailure(Context context, String message) {
        return handleAuthFailure(context, -1, message);
    }
}
