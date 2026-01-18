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
import com.example.motovista_deep.utils.ImageUtils;

import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeViewHolder> {

    private Context context;
    private List<BikeModel> bikeList;
    private OnBikeClickListener listener;
    private int layoutResId = R.layout.item_bike_card;

    public interface OnBikeClickListener {
        void onBikeClick(BikeModel bike);
    }

    public BikeAdapter(Context context, List<BikeModel> bikeList, OnBikeClickListener listener) {
        this.context = context;
        this.bikeList = bikeList;
        this.listener = listener;
    }

    public BikeAdapter(Context context, List<BikeModel> bikeList, int layoutResId, OnBikeClickListener listener) {
        this.context = context;
        this.bikeList = bikeList;
        this.layoutResId = layoutResId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
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
        if (holder.tvBrand != null) {
            holder.tvBrand.setText(bike.getBrand() != null ? bike.getBrand() : "Brand");
        }
        if (holder.tvModel != null) {
            holder.tvModel.setText(bike.getModel() != null ? bike.getModel() : "Model");
        }

        // 2. SET TAG & INFO BASED ON BIKE TYPE
        if (holder.tvTag != null) {
            if ("NEW".equalsIgnoreCase(bike.getType())) {
                holder.tvTag.setText("NEW BIKE");
                holder.tvTag.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.tag_new_bike)
                );
                if (holder.tvInfo != null) holder.tvInfo.setVisibility(View.GONE);
            } else {
                holder.tvTag.setText("SH BIKES");
                holder.tvTag.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.tag_sh_bike)
                );
                if (holder.tvInfo != null) {
                    String condition = bike.getCondition();
                    if (condition == null || condition.isEmpty()) condition = "Good";
                    holder.tvInfo.setVisibility(View.VISIBLE);
                    holder.tvInfo.setText("Condition: " + condition);
                }
            }
        }

        // 3. LOAD BIKE IMAGE
        loadBikeImage(holder, bike, position, baseUrl);

        // 4. CLICK LISTENER
        View clickTarget = holder.cardBike != null ? holder.cardBike : holder.itemView;
        clickTarget.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBikeClick(bike);
            }
        });

        // 5. SET CHEVRON COLOR
        if (holder.ivChevron != null) {
            holder.ivChevron.setColorFilter(ContextCompat.getColor(context, R.color.primary_color));
        }

        // Extra: Featured price/text
        if (holder.tvPrice != null) {
            if (layoutResId == R.layout.item_home_featured) {
                holder.tvPrice.setText("Explore the performance");
            } else {
                String priceStr = bike.getOnRoadPrice();
                if (priceStr == null || priceStr.isEmpty()) priceStr = bike.getPrice();
                if (priceStr != null && !priceStr.isEmpty()) {
                    holder.tvPrice.setText("₹ " + priceStr);
                } else {
                    holder.tvPrice.setText("View Details");
                }
            }
        }
        
        // Featured background
        if (holder.ivBikeBg != null) {
            // holder.ivBikeBg is just a decoration, but we could put the bike image there too
        }
    }

    private void loadBikeImage(BikeViewHolder holder, BikeModel bike, int position, String baseUrl) {
        if (holder.ivBike == null) return;
        
        // Get the image URL from bike
        String imageUrl = bike.getImageUrl();

        // USER REQUEST: Fix images loading - "if variant also no problem any one image should come outer"
        // If no image URL, try to get from all images (which might contain variant images)
        if ((imageUrl == null || imageUrl.isEmpty() || "null".equalsIgnoreCase(imageUrl))) {
            List<String> allImages = bike.getAllImages();
            if (allImages != null && !allImages.isEmpty()) {
                for (String img : allImages) {
                    if (img != null && !img.isEmpty() && !"null".equalsIgnoreCase(img)) {
                        imageUrl = img;
                        break;
                    }
                }
            }
        }

        // Create request options
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .fitCenter()
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
        return ImageUtils.getFullImageUrl(url);
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
        
        // New views for featured/new arrival cards
        TextView tvPrice;
        ImageView ivBikeBg;

        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBike = itemView.findViewById(R.id.cardBike);
            tvTag = itemView.findViewById(R.id.tvTag);
            ivBike = itemView.findViewById(R.id.ivBike);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivBikeBg = itemView.findViewById(R.id.ivBikeBg);
        }
    }
}