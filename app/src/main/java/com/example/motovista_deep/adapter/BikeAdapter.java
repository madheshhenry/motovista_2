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

    public interface OnBikeClickListener {
        void onBikeClick(BikeModel bike);
    }

    public BikeAdapter(Context context, List<BikeModel> bikeList, OnBikeClickListener listener) {
        this.context = context;
        this.bikeList = bikeList;
        this.listener = listener;
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

        // Get base URL from RetrofitClient
        // Get base URL from RetrofitClient
        String baseUrl = RetrofitClient.BASE_URL;
        
        // CLEAN THE BASE URL HERE
        // RetrofitClient.BASE_URL includes "api/", but images are in "uploads/" (sibling folder)
        if (baseUrl != null && baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.replace("api/", "");
        }
        
        // Ensure base URL ends with /
        if (baseUrl != null && !baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        
        // Set CLEAN base URL for bike model
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

            String price = bike.getOnRoadPrice();
            if (price == null || price.isEmpty()) {
                price = bike.getPrice();
            }
            // For NEW bikes, show Price. Do NOT show Condition.
            holder.tvInfo.setText(price != null && !price.isEmpty() ? "₹" + price : "Price on request");
            holder.tvInfo.setTextColor(
                    ContextCompat.getColor(context, R.color.primary_color)
            );
        } else {
            // SECOND HAND
            holder.tvTag.setText("SH BIKES");
            holder.tvTag.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.tag_sh_bike)
            );

            // For USED bikes, show Condition.
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
        loadBikeImage(holder, bike, position, baseUrl);

        // 4. CLICK LISTENER
        holder.cardBike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBikeClick(bike);
            }
        });

        // 5. SET CHEVRON COLOR
        holder.ivChevron.setColorFilter(ContextCompat.getColor(context, R.color.primary_color));
    }

    private void loadBikeImage(BikeViewHolder holder, BikeModel bike, int position, String baseUrl) {
        // Get the image URL from bike
        String imageUrl = bike.getImageUrl();

        // If no image URL, try to get from all images
        if ((imageUrl == null || imageUrl.isEmpty()) && bike.getAllImages() != null && !bike.getAllImages().isEmpty()) {
            imageUrl = bike.getAllImages().get(0);
        }

        // Create request options
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        // Clean the image URL
        final String finalImageUrl = cleanImageUrl(imageUrl, baseUrl);

        if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
            // Load with Glide
            Glide.with(context)
                    .load(finalImageUrl)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(holder.ivBike);
        } else {
            holder.ivBike.setImageResource(R.drawable.placeholder_bike);
        }
    }

    // Clean image URL
    private String cleanImageUrl(String url, String baseUrl) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // Remove quotes, backslashes and whitespace
        url = url.replace("\"", "").replace("\\", "").trim();

        // If already a full URL, return as is
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // Handle paths that might come as "bikes/..." or "uploads/bikes/..."
        // If it starts with [ or ", it might be a JSON failure string, try to clean it
        if (url.startsWith("[") || url.startsWith("]")) {
            url = url.replaceAll("[\\[\\]\"]", "");
        }
        
        // Construct partial path
        if (!url.contains("uploads/")) {
             if (url.startsWith("bikes/") || url.startsWith("second_hand_bikes/")) {
                 url = "uploads/" + url;
             } else if (!url.startsWith("uploads/")) {
                 // Default to uploads/bikes/ if pure filename
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
            
            return serverBase + url;
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