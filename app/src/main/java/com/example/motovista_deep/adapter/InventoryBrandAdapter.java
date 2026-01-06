package com.example.motovista_deep.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.BrandBikesActivity;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.InventoryBrand;

import java.io.Serializable;
import java.util.List;

public class InventoryBrandAdapter extends RecyclerView.Adapter<InventoryBrandAdapter.ViewHolder> {

    private Context context;
    private List<InventoryBrand> brandList;

    public InventoryBrandAdapter(Context context, List<InventoryBrand> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory_brand_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryBrand brand = brandList.get(position);

        holder.tvBrandName.setText(brand.getBrand());
        holder.tvBikeCount.setText(brand.getCount() + " Bikes");

        // Initial Logic
        if (brand.getBrand() != null && !brand.getBrand().isEmpty()) {
            holder.tvBrandInitial.setText(String.valueOf(brand.getBrand().charAt(0)).toUpperCase());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BrandBikesActivity.class);
            intent.putExtra("BRAND_NAME", brand.getBrand());
            // Pass the list of bikes
            // Note: InventoryBike must implement Serializable
            intent.putExtra("BIKE_LIST", (Serializable) brand.getBikes());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return brandList != null ? brandList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandInitial, tvBrandName, tvBikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandInitial = itemView.findViewById(R.id.tvBrandInitial);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvBikeCount = itemView.findViewById(R.id.tvBikeCount);
        }
    }
}
