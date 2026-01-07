package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.api.RetrofitClient;

import java.util.List;

public class CustomerBikeAdapter extends RecyclerView.Adapter<CustomerBikeAdapter.ViewHolder> {

    private Context context;
    private List<BikeModel> bikeList;
    private OnBikeClickListener listener;

    public interface OnBikeClickListener {
        void onBikeClick(BikeModel bike);
    }

    public CustomerBikeAdapter(Context context, List<BikeModel> bikeList, OnBikeClickListener listener) {
        this.context = context;
        this.bikeList = bikeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_bike_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BikeModel bike = bikeList.get(position);

        // Text Data
        holder.tvBikeName.setText(bike.getBrand() + " " + bike.getModel());
        
        String price = bike.getOnRoadPrice();
        if (price == null || price.isEmpty()) price = bike.getPrice();
        holder.tvBikePrice.setText(price != null ? "₹ " + price : "Price on request");

        holder.tvEngineCC.setText(bike.getEngineCC() != null ? bike.getEngineCC() : "N/A");
        holder.tvMileage.setText(bike.getMileage() != null ? bike.getMileage() : "N/A");

        // Image Loading Logic (Cleaned)
        String baseUrl = RetrofitClient.BASE_URL;
        if (baseUrl != null && baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.replace("api/", "");
        }
        if (baseUrl != null && !baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        bike.setBaseUrl(baseUrl); // Helper for model if used elsewhere

        loadBikeImage(holder.ivBikeImage, bike, baseUrl);

        // Click Listener
        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) listener.onBikeClick(bike);
        });
        
        // Also card click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBikeClick(bike);
        });
    }

    private void loadBikeImage(ImageView imageView, BikeModel bike, String baseUrl) {
        String imageUrl = bike.getImageUrl();
        if ((imageUrl == null || imageUrl.isEmpty()) && bike.getAllImages() != null && !bike.getAllImages().isEmpty()) {
            imageUrl = bike.getAllImages().get(0);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
             // Robust Cleaning Logic repeated
             imageUrl = imageUrl.replace("\"", "").replace("\\", "").trim();
             if (imageUrl.startsWith("[") || imageUrl.startsWith("]")) {
                imageUrl = imageUrl.replaceAll("[\\[\\]\"]", "");
             }
             
             if (!imageUrl.startsWith("http")) {
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
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_bike);
        }
    }

    @Override
    public int getItemCount() {
        return bikeList != null ? bikeList.size() : 0;
    }
    
    public void updateList(List<BikeModel> newList) {
        this.bikeList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBikeImage;
        TextView tvBikeName, tvBikePrice;
        TextView tvEngineCC, tvMileage;
        TextView btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBikeImage = itemView.findViewById(R.id.ivBikeImage);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvBikePrice = itemView.findViewById(R.id.tvBikePrice);
            tvEngineCC = itemView.findViewById(R.id.tvEngineCC);
            tvMileage = itemView.findViewById(R.id.tvMileage);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
