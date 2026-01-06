package com.example.motovista_deep.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.motovista_deep.R;
import com.example.motovista_deep.api.RetrofitClient;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class FullScreenImageAdapter extends RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private String baseUrl;

    public FullScreenImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.baseUrl = RetrofitClient.BASE_URL;
        
         if (this.baseUrl != null && !this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_full_screen_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        String finalUrl = cleanImageUrl(imageUrl);

        Glide.with(context)
                .asBitmap()
                .load(finalUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @androidx.annotation.Nullable Transition<? super Bitmap> transition) {
                         holder.photoView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@androidx.annotation.Nullable Drawable placeholder) {
                    }
                });
    }

     private String cleanImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        url = url.replace("\"", "").replace("\\", "").trim();

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        if (!url.contains("uploads/")) {
            if (url.startsWith("bikes/") || url.startsWith("second_hand_bikes/")) {
                url = "uploads/" + url;
            } else if (!url.startsWith("uploads/")) {
                url = "uploads/bikes/" + url;
            }
        }

        if (baseUrl != null && !baseUrl.isEmpty()) {
            String serverBase = baseUrl;
            if (serverBase.endsWith("api/")) {
                serverBase = serverBase.replace("api/", "");
            }
            
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.fullScreenImageView);
        }
    }
}
