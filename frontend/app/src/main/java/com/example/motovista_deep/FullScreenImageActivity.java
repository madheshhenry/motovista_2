package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.motovista_deep.adapter.FullScreenImageAdapter;

import java.util.ArrayList;

public class FullScreenImageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageButton btnClose;
    private ArrayList<String> imageUrls;
    private int startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        viewPager = findViewById(R.id.viewPager);
        btnClose = findViewById(R.id.btnClose);

        // Get data
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("IMAGE_URLS")) {
                imageUrls = intent.getStringArrayListExtra("IMAGE_URLS");
                startPosition = intent.getIntExtra("START_POSITION", 0);
            } else if (intent.hasExtra("image_url")) {
                // Handle single image request
                imageUrls = new ArrayList<>();
                String singleUrl = intent.getStringExtra("image_url");
                if (singleUrl != null) {
                    imageUrls.add(singleUrl);
                }
                startPosition = 0;
            }
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            FullScreenImageAdapter adapter = new FullScreenImageAdapter(this, imageUrls);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(startPosition, false);
        } else {
            // Fallback for debugging
            // Toast.makeText(this, "No image to display", Toast.LENGTH_SHORT).show();
        }

        btnClose.setOnClickListener(v -> finish());
    }
}