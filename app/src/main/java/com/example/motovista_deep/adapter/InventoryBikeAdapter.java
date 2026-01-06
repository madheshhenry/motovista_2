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
        holder.tvEngineNo.setText(bike.getEngineNumber() != null ? bike.getEngineNumber() : "N/A");

        // Load Thumbnail
        String imageUrl = bike.getThumbnail();
        String finalUrl = cleanImageUrl(imageUrl);

        Glide.with(context)
                .load(finalUrl)
                .placeholder(R.drawable.ic_two_wheeler)
                .error(R.drawable.ic_two_wheeler)
                .centerCrop()
                .into(holder.ivThumbnail);
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
             // Basic heuristic, adjust as needed based on actual backend paths
             if (url.startsWith("bikes/")) {
                 url = "uploads/" + url;
             } else {
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
        return bikeList != null ? bikeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvVariant, tvEngineNo;
        ImageView ivThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModel = itemView.findViewById(R.id.tvBikeModel);
            tvVariant = itemView.findViewById(R.id.tvBikeVariant);
            tvEngineNo = itemView.findViewById(R.id.tvEngineNo);
            ivThumbnail = itemView.findViewById(R.id.ivBikeThumbnail);
        }
    }
}
