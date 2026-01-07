package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motovista_deep.R;
import com.example.motovista_deep.api.RetrofitClient;

import java.util.List;

public class BikeImagesAdapter extends RecyclerView.Adapter<BikeImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> images;

    public BikeImagesAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bike_image_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = images.get(position);
        
        // Basic URL cleaning
        String baseUrl = RetrofitClient.BASE_URL;
         if (baseUrl != null && baseUrl.endsWith("api/")) {
             baseUrl = baseUrl.replace("api/", "");
         }
         
        if (imageUrl != null && !imageUrl.startsWith("http")) {
               if (!imageUrl.contains("uploads/")) {
                    if (imageUrl.startsWith("bikes/") || imageUrl.startsWith("second_hand_bikes/")) {
                        imageUrl = "uploads/" + imageUrl;
                    } else {
                        imageUrl = "uploads/bikes/" + imageUrl;
                    }
               }
               imageUrl = baseUrl + imageUrl;
         }

        Glide.with(context)
                .load(imageUrl)
                .override(800, 600) // Downsample high-res images
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .into(holder.ivBikeImage);
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBikeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBikeImage = itemView.findViewById(R.id.ivBikeImage);
        }
    }
}
