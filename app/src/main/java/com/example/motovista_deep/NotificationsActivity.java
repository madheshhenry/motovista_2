package com.example.motovista_deep;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle, tvEmptyTitle, tvEmptyDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyDescription = findViewById(R.id.tvEmptyDescription);

        // Set initial text
        tvTitle.setText("Notifications");
        tvEmptyTitle.setText("No notifications yet");
        tvEmptyDescription.setText("You have no new notifications. We'll let you know when updates arrive.");
    }

    private void setupClickListeners() {
        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Admin Dashboard
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}