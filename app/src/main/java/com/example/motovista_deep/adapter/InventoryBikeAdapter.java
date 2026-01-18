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
import com.example.motovista_deep.R;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.InventoryBike;
import com.example.motovista_deep.utils.ImageUtils;

import java.util.List;

public class InventoryBikeAdapter extends RecyclerView.Adapter<InventoryBikeAdapter.ViewHolder> {

    private Context context;
    private List<InventoryBike> bikeList;
    private String baseUrl;

    private OnBikeLongClickListener longClickListener;

    public interface OnBikeLongClickListener {
        void onBikeLongClick(InventoryBike bike, int position);
    }

    public InventoryBikeAdapter(Context context, List<InventoryBike> bikeList, OnBikeLongClickListener longClickListener) {
        this.context = context;
        this.bikeList = bikeList;
        this.longClickListener = longClickListener;
        this.baseUrl = RetrofitClient.BASE_URL;
        
        if (this.baseUrl != null && !this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }
    }
    
    // BACKWARD COMPATIBILITY CONSTRUCTOR
    public InventoryBikeAdapter(Context context, List<InventoryBike> bikeList) {
        this(context, bikeList, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory_bike, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryBike bike = bikeList.get(position);

        holder.tvModel.setText(bike.getModel());
        holder.tvVariant.setText(bike.getVariant() != null ? bike.getVariant() : "");
        
        // Engine & Chassis
        holder.tvEngineNo.setText(bike.getEngineNumber() != null ? bike.getEngineNumber() : "N/A");
        holder.tvChassisNo.setText(bike.getChassisNumber() != null ? bike.getChassisNumber() : "N/A");
        holder.tvInwardDate.setText(bike.getStockDate() != null ? bike.getStockDate() : "-");

        // Status Logic
        String status = bike.getStatus();
        if ("Delivered".equalsIgnoreCase(status) || "Sold".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("SOLD");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#15803d")); // Green 700
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_green_bg);
            
            // Customer Info
            holder.layoutCustomer.setVisibility(View.VISIBLE);
            holder.tvCustomerName.setText(bike.getCustomerName() != null ? bike.getCustomerName() : "Unknown");
            holder.tvDateLabel.setText("SOLD ON");
            holder.tvDeliveryDate.setText(bike.getSoldDate() != null ? bike.getSoldDate() : "-");
            
        } else {
            holder.tvStatusBadge.setText("IN STOCK");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#1d4ed8")); // Blue 700
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_blue_bg);
            
            // Hide Customer Info
            holder.layoutCustomer.setVisibility(View.GONE);
        }

        // Color Logic
        String colorName = "Unknown";
        int colorHex = android.graphics.Color.GRAY;
        
        try {
            String rawColors = bike.getColors();
            if (rawColors != null && !rawColors.isEmpty()) {
                String colorString = rawColors;
                
                if (rawColors.trim().startsWith("[")) {
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(rawColors);
                        if (jsonArray.length() > 0) {
                            colorString = jsonArray.getString(0); 
                        }
                    } catch (Exception ex) { }
                }

                if (colorString.contains("|")) {
                     String[] parts = colorString.split("\\|");
                     colorName = parts[0];
                     if (parts.length > 1 && !parts[1].isEmpty()) {
                         try {
                             colorHex = android.graphics.Color.parseColor(parts[1]);
                         } catch (IllegalArgumentException e) { }
                     }
                 } else {
                     colorName = colorString;
                 }
            }
        } catch (Exception e) { }
        
        holder.tvColorName.setText(colorName);
        holder.viewColorDot.getBackground().setTint(colorHex);

        // Load Image using centralized ImageUtils
        String imageUrl = ImageUtils.getFullImageUrl(bike.getThumbnail());
        if (!imageUrl.isEmpty()) {
             Glide.with(context)
                 .load(imageUrl)
                 .placeholder(R.drawable.placeholder_bike)
                 .error(R.drawable.placeholder_bike)
                 .centerCrop()
                 .into(holder.ivBikeImage);
        } else {
             holder.ivBikeImage.setImageResource(R.drawable.placeholder_bike);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    longClickListener.onBikeLongClick(bike, pos);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return bikeList != null ? bikeList.size() : 0;
    }

    public void removeAt(int position) {
        if (bikeList != null && position >= 0 && position < bikeList.size()) {
            bikeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bikeList.size());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvVariant, tvEngineNo, tvChassisNo, tvInwardDate, tvStatusBadge;
        TextView tvColorName, tvCustomerName, tvDateLabel, tvDeliveryDate;
        View viewColorDot, layoutCustomer;
        ImageView ivBikeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModel = itemView.findViewById(R.id.tvBikeModel);
            tvVariant = itemView.findViewById(R.id.tvBikeVariant);
            tvEngineNo = itemView.findViewById(R.id.tvEngineNo);
            tvChassisNo = itemView.findViewById(R.id.tvChassisNo);
            tvInwardDate = itemView.findViewById(R.id.tvInwardDate);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            
            tvColorName = itemView.findViewById(R.id.tvColorName);
            viewColorDot = itemView.findViewById(R.id.viewColorDot);
            
            layoutCustomer = itemView.findViewById(R.id.layoutCustomer);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDateLabel = itemView.findViewById(R.id.tvDateLabel);
            tvDeliveryDate = itemView.findViewById(R.id.tvDeliveryDate);
            
            ivBikeImage = itemView.findViewById(R.id.ivBikeImage);
        }
    }
}
