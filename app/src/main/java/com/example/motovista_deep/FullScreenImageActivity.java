package com.example.motovista_deep;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class FullScreenImageActivity extends AppCompatActivity {

    private PhotoView fullScreenImageView;
    private TextView tvTitle;
    private ImageView btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Initialize views
        fullScreenImageView = findViewById(R.id.fullScreenImageView);
        tvTitle = findViewById(R.id.tvTitle);
        btnClose = findViewById(R.id.btnClose);

        // Get data from intent
        String imageUrl = getIntent().getStringExtra("image_url");
        String title = getIntent().getStringExtra("title");

        // Set title
        if (title != null) {
            tvTitle.setText(title);
        }

        // Load image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(fullScreenImageView);
        }

        // Close button click
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Click on image to close
        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}