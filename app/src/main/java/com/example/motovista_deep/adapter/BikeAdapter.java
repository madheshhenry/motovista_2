package com.example.motovista_deep.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.BikeModel;
import com.example.motovista_deep.api.RetrofitClient;

import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeViewHolder> {

    private Context context;
    private List<BikeModel> bikeList;
    private OnBikeClickListener listener;
    private String baseUrl;

    public interface OnBikeClickListener {
        void onBikeClick(BikeModel bike);
    }

    public BikeAdapter(Context context, List<BikeModel> bikeList, OnBikeClickListener listener) {
        this.context = context;
        this.bikeList = bikeList;
        this.listener = listener;
        this.baseUrl = RetrofitClient.BASE_URL;

        // Ensure base URL ends with /
        if (this.baseUrl != null && !this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bike_card, parent, false);
        return new BikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        BikeModel bike = bikeList.get(position);

        // Set base URL for bike model
        bike.setBaseUrl(baseUrl);

        // 1. SET TEXT CONTENT
        holder.tvBrand.setText(bike.getBrand() != null ? bike.getBrand() : "Brand");
        holder.tvModel.setText(bike.getModel() != null ? bike.getModel() : "Model");

        // 2. SET TAG & INFO BASED ON BIKE TYPE
        if ("NEW".equalsIgnoreCase(bike.getType())) {
            holder.tvTag.setText("NEW BIKE");
            holder.tvTag.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.tag_new_bike)
            );

            String price = bike.getPrice();
            if (price == null || price.isEmpty()) {
                price = bike.getOnRoadPrice();
            }
            holder.tvInfo.setText(price != null && !price.isEmpty() ? "₹" + price : "Price on request");
            holder.tvInfo.setTextColor(
                    ContextCompat.getColor(context, R.color.primary_color)
            );
        } else {
            holder.tvTag.setText("USED BIKE");
            holder.tvTag.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.tag_sh_bike)
            );

            String condition = bike.getCondition();
            if (condition == null || condition.isEmpty()) {
                condition = "Good";
            }
            holder.tvInfo.setText("Condition: " + condition);

            if ("Excellent".equalsIgnoreCase(condition)) {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_green));
            } else if ("Good".equalsIgnoreCase(condition)) {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_yellow));
            } else {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_red));
            }
        }

        // 3. LOAD BIKE IMAGE
        loadBikeImage(holder, bike, position);

        // 4. CLICK LISTENER
        holder.cardBike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBikeClick(bike);
            }
        });

        // 5. SET CHEVRON COLOR
        holder.ivChevron.setColorFilter(ContextCompat.getColor(context, R.color.primary_color));
    }

    private void loadBikeImage(BikeViewHolder holder, BikeModel bike, int position) {
        // Get the image URL from bike
        String imageUrl = bike.getImageUrl();

        // Debug log
        Log.d("BIKE_ADAPTER", "Position: " + position +
                ", Brand: " + bike.getBrand() +
                ", Model: " + bike.getModel() +
                ", Image URL: " + imageUrl);

        // If no image URL, try to get from all images
        if ((imageUrl == null || imageUrl.isEmpty()) && bike.getAllImages() != null && !bike.getAllImages().isEmpty()) {
            imageUrl = bike.getAllImages().get(0);
            Log.d("BIKE_ADAPTER", "Using image from getAllImages: " + imageUrl);
        }

        // Create request options
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        // ✅ FIX: Create a final variable for use in inner class
        final String finalImageUrl = imageUrl != null ? cleanImageUrl(imageUrl) : "";

        if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
            Log.d("BIKE_ADAPTER", "Cleaned Image URL: " + finalImageUrl);

            // Load with Glide
            Glide.with(context)
                    .load(finalImageUrl)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("BIKE_ADAPTER", "Failed to load image: " + finalImageUrl + ", Error: " + (e != null ? e.getMessage() : "Unknown"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("BIKE_ADAPTER", "Image loaded successfully: " + finalImageUrl);
                            return false;
                        }
                    })
                    .into(holder.ivBike);
        } else {
            Log.d("BIKE_ADAPTER", "No image URL, using placeholder");
            holder.ivBike.setImageResource(R.drawable.placeholder_bike);
        }
    }

    // Clean image URL
    private String cleanImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // Remove quotes and backslashes
        url = url.replace("\"", "").replace("\\", "");

        Log.d("BIKE_ADAPTER", "URL before cleaning: " + url);

        // If already a full URL, check if it has uploads/
        if (url.startsWith("http://") || url.startsWith("https://")) {
            // Check if uploads/ is missing
            if (url.contains(baseUrl)) {
                String relative = url.replace(baseUrl, "");
                if (!relative.contains("uploads/")) {
                    if (relative.startsWith("bikes/")) {
                        url = baseUrl + "uploads/" + relative;
                    } else if (relative.startsWith("second_hand_bikes/")) {
                        url = baseUrl + "uploads/" + relative;
                    }
                }
            }
            Log.d("BIKE_ADAPTER", "Cleaned URL: " + url);
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
            String base = baseUrl;
            if (!base.endsWith("/") && !url.startsWith("/")) {
                url = "/" + url;
            }
            if (base.endsWith("/") && url.startsWith("/")) {
                url = url.substring(1);
            }

            String finalUrl = base + url;
            Log.d("BIKE_ADAPTER", "Final URL: " + finalUrl);
            return finalUrl;
        }

        return url;
    }

    @Override
    public int getItemCount() {
        return bikeList != null ? bikeList.size() : 0;
    }

    public void updateList(List<BikeModel> newList) {
        if (newList != null) {
            this.bikeList = newList;
            notifyDataSetChanged();
        }
    }

    public void addItems(List<BikeModel> newItems) {
        if (newItems != null && !newItems.isEmpty()) {
            int startPosition = bikeList.size();
            bikeList.addAll(newItems);
            notifyItemRangeInserted(startPosition, newItems.size());
        }
    }

    public void clearItems() {
        if (bikeList != null) {
            int size = bikeList.size();
            bikeList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public void filterBikes(List<BikeModel> filteredList, String query) {
        bikeList = filteredList;
        notifyDataSetChanged();
    }

    public BikeModel getBikeAtPosition(int position) {
        if (position >= 0 && position < bikeList.size()) {
            return bikeList.get(position);
        }
        return null;
    }

    static class BikeViewHolder extends RecyclerView.ViewHolder {
        CardView cardBike;
        TextView tvTag;
        ImageView ivBike;
        TextView tvBrand;
        TextView tvModel;
        TextView tvInfo;
        ImageView ivChevron;

        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBike = itemView.findViewById(R.id.cardBike);
            tvTag = itemView.findViewById(R.id.tvTag);
            ivBike = itemView.findViewById(R.id.ivBike);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivChevron = itemView.findViewById(R.id.ivChevron);
        }
    }
}