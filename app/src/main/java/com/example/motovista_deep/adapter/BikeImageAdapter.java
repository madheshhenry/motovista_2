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

public class BikeImageAdapter extends RecyclerView.Adapter<BikeImageAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imagePaths;

    public BikeImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    public void updateImages(List<String> newPaths) {
        this.imagePaths = newPaths;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bike_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        if (imagePath != null && !imagePath.isEmpty()) {
            String finalPath = imagePath.replace("\\", "");
            
            // Construct full URL if it's a relative path
            if (!finalPath.startsWith("http")) {
                String baseUrl = RetrofitClient.BASE_URL;
                if (baseUrl.endsWith("api/")) baseUrl = baseUrl.replace("api/", "");
                if (!baseUrl.endsWith("/")) baseUrl += "/";
                if (finalPath.startsWith("/")) finalPath = finalPath.substring(1);
                finalPath = baseUrl + finalPath;
            }

            Glide.with(context)
                    .load(finalPath)
                    .placeholder(R.drawable.placeholder_image) 
                    .into(holder.imageView);
        } else {
             holder.imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return imagePaths != null ? imagePaths.size() : 0;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivBikeImageSlider);
        }
    }
}
