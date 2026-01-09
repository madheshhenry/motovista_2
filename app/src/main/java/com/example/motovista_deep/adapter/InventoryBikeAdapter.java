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

import java.util.List;

public class InventoryBikeAdapter extends RecyclerView.Adapter<InventoryBikeAdapter.ViewHolder> {

    private Context context;
    private List<InventoryBike> bikeList;
    private String baseUrl;

    public InventoryBikeAdapter(Context context, List<InventoryBike> bikeList) {
        this.context = context;
        this.bikeList = bikeList;
        this.baseUrl = RetrofitClient.BASE_URL;
        
        if (this.baseUrl != null && !this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }
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
            holder.tvStatusBadge.setText("DELIVERED");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#15803d")); // Green 700
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_green_bg);
            
            // Customer Info
            holder.layoutCustomer.setVisibility(View.VISIBLE);
            holder.tvCustomerName.setText(bike.getCustomerName() != null ? bike.getCustomerName() : "Unknown");
            holder.tvDateLabel.setText("DELIVERED ON");
            holder.tvDeliveryDate.setText(bike.getDeliveryDate() != null ? bike.getDeliveryDate() : "-");
            
        } else {
            holder.tvStatusBadge.setText("IN STOCK");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#1d4ed8")); // Blue 700
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_blue_bg);
            
            // Hide Customer Info
            holder.layoutCustomer.setVisibility(View.GONE);
        }

        // Color Logic
        // Assuming colors string is like "Metallic Black|#000000" or just name? 
        // Or JSON [{"name":"Black", "hex":"#000"}]
        // Let's safe handle.
        String colorName = "Unknown";
        int colorHex = android.graphics.Color.GRAY;
        
        try {
            // Simple robust check if it's a JSON or pipe separated
            String rawColors = bike.getColors();
            if (rawColors != null && !rawColors.isEmpty()) {
                 if (rawColors.contains("|")) {
                     String[] parts = rawColors.split("\\|");
                     colorName = parts[0];
                     if (parts.length > 1) colorHex = android.graphics.Color.parseColor(parts[1]);
                 } else if (rawColors.startsWith("[")) {
                     // Parse JSON if implemented, fallback to name extract
                     // Just use Model/Variant default if complex
                     colorName = "Standard"; 
                 } else {
                     colorName = rawColors;
                 }
            }
        } catch (Exception e) {
            // Fallback
        }
        
        holder.tvColorName.setText(colorName);
        holder.viewColorDot.getBackground().setTint(colorHex);
    }
    
    // cleanImageUrl unused in this layout? 
    // New layout does not show bike image thumbnail, only color dot.
    // So distinct from previous.

    private String cleanImageUrl(String url) {
        // ... (keep if might be used later or remove)
        return url; 
    }

    @Override
    public int getItemCount() {
        return bikeList != null ? bikeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvVariant, tvEngineNo, tvChassisNo, tvInwardDate, tvStatusBadge;
        TextView tvColorName, tvCustomerName, tvDateLabel, tvDeliveryDate;
        View viewColorDot, layoutCustomer;

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
        }
    }
}
