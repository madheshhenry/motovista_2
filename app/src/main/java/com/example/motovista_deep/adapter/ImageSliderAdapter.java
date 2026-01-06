package com.example.motovista_deep.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.motovista_deep.R;
import com.example.motovista_deep.api.RetrofitClient;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private String baseUrl;

    public ImageSliderAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.baseUrl = RetrofitClient.BASE_URL;

        // Ensure base URL ends with /
        if (this.baseUrl != null && !this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }

        Log.d("IMAGE_SLIDER", "Adapter created with " + imageUrls.size() + " images");
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slide, parent, false);
        return new ImageSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        Log.d("IMAGE_SLIDER", "Binding position " + position + ", URL: " + imageUrl);

        // Clean and prepare the URL
        String finalUrl = cleanImageUrl(imageUrl);

        Log.d("IMAGE_SLIDER", "Final URL: " + finalUrl);

        // Create request options
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_bike_placeholder)
                .error(R.drawable.ic_bike_placeholder)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        // Load image with Glide
        Glide.with(context)
                .load(finalUrl)
                .apply(requestOptions)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.motovista_deep.FullScreenImageActivity.class);
            intent.putStringArrayListExtra("IMAGE_URLS", new java.util.ArrayList<>(imageUrls));
            intent.putExtra("START_POSITION", position);
            context.startActivity(intent);
        });
    }

    private String cleanImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // Remove quotes, backslashes and whitespace
        url = url.replace("\"", "").replace("\\", "").trim();

        // If already a full URL, check if uploads/ is missing
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // If it's a relative path
        if (!url.contains("uploads/")) {
            if (url.startsWith("bikes/") || url.startsWith("second_hand_bikes/")) {
                url = "uploads/" + url;
            } else if (!url.startsWith("uploads/")) {
                url = "uploads/bikes/" + url;
            }
        }

        // Add base URL
        if (baseUrl != null && !baseUrl.isEmpty()) {
            // IMPORTANT: The uploads folder is OUTSIDE the api folder.
            // BASE_URL is .../motovista_backend/api/
            // Images are at .../motovista_backend/uploads/
            // So we need to remove "api/" from the base URL
            
            String serverBase = baseUrl;
            if (serverBase.endsWith("api/")) {
                serverBase = serverBase.replace("api/", "");
            }
            
            // Handle slashes
            if (!serverBase.endsWith("/") && !url.startsWith("/")) {
                url = "/" + url;
            }
            if (serverBase.endsWith("/") && url.startsWith("/")) {
                url = url.substring(1);
            }
            return serverBase + url;
        }

        return url;
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    public void updateImages(List<String> newImages) {
        this.imageUrls = newImages;
        notifyDataSetChanged();
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSliderImage);
        }
    }
}