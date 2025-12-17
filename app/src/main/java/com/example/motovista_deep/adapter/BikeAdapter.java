package com.example.motovista_deep.adapter;

import android.content.Context;
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

        // 1. FIRST SET TEXT (FAST)
        holder.tvBrand.setText(bike.getBrand() != null ? bike.getBrand() : "");
        holder.tvModel.setText(bike.getModel() != null ? bike.getModel() : "");

        // 2. SET TAG & INFO
        if ("NEW".equalsIgnoreCase(bike.getType())) {
            holder.tvTag.setText("NEW BIKE");
            holder.tvTag.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.tag_new_bike)
            );
            holder.tvInfo.setText("₹" + bike.getPrice());
            holder.tvInfo.setTextColor(
                    ContextCompat.getColor(context, R.color.primary_color)
            );
        } else {
            holder.tvTag.setText("SH BIKES");
            holder.tvTag.setBackground(
                    ContextCompat.getDrawable(context, R.drawable.tag_sh_bike)
            );
            String condition = bike.getCondition();
            holder.tvInfo.setText("Condition: " + (condition != null ? condition : ""));

            if ("Excellent".equalsIgnoreCase(condition)) {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_green));
            } else if ("Good".equalsIgnoreCase(condition)) {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_yellow));
            } else {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_red));
            }
        }

        // 3. ✅ OPTIMIZED IMAGE LOADING
        String imageUrl = bike.getImageUrl();

        // Show placeholder first
        holder.ivBike.setImageResource(R.drawable.placeholder_bike);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            // ✅ FIX: Use proper caching
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.placeholder_bike)
                    .error(R.drawable.placeholder_bike)
                    .centerCrop();

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .apply(requestOptions)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // ✅ CACHE EVERYTHING
                    .skipMemoryCache(false)                   // ✅ USE MEMORY CACHE
                    .transition(DrawableTransitionOptions.withCrossFade(300)) // Smooth fade
                    .thumbnail(0.1f) // Load thumbnail first
                    .into(holder.ivBike);
        } else {
            // If no image, keep placeholder
            holder.ivBike.setImageResource(R.drawable.placeholder_bike);
        }

        // 4. CLICK LISTENER
        holder.cardBike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBikeClick(bike);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeList != null ? bikeList.size() : 0;
    }

    public void updateList(List<BikeModel> newList) {
        this.bikeList = newList;
        notifyDataSetChanged();
    }

    // ✅ ADD THIS METHOD for better performance
    public void addItems(List<BikeModel> newItems) {
        int startPosition = bikeList.size();
        bikeList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
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