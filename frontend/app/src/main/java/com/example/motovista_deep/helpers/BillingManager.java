package com.example.motovista_deep.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.example.motovista_deep.R;

public class BillingManager {
    private static final String PREF_NAME = "MotoVistaBilling";
    private static final String KEY_IS_SUBSCRIBED = "is_subscribed_premium";

    private Context context;

    public BillingManager(Context context) {
        this.context = context;
    }

    public boolean isSubscribed() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_SUBSCRIBED, false);
    }

    public void setSubscribed(boolean isSubscribed) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_SUBSCRIBED, isSubscribed).apply();
    }

    public void simulatePurchase(Activity activity, Runnable onSuccess) {
        // Show a simulated payment processing dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_payment_processing, null); // We will need to create this simple layout or just use a standard dialog
        builder.setView(dialogView);
        builder.setCancelable(false);
        
        AlertDialog dialog = builder.create();
        dialog.show();

        // Simulate network delay
        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            setSubscribed(true);
            showSuccessDialog(activity, onSuccess);
        }, 2000);
    }

    private void showSuccessDialog(Activity activity, Runnable onSuccess) {
        new AlertDialog.Builder(activity)
            .setTitle("Payment Successful")
            .setMessage("Welcome to MotoVista Prime! You can now place orders.")
            .setPositiveButton("Continue", (dialog, which) -> {
                if (onSuccess != null) {
                    onSuccess.run();
                }
            })
            .setCancelable(false)
            .show();
    }
}
