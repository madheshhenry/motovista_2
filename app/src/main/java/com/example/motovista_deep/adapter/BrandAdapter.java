package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.Brand;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private Context context;
    private List<Brand> brandList;
    private OnBrandClickListener listener;
    private OnBrandLongClickListener longClickListener;

    public interface OnBrandClickListener {
        void onBrandClick(int position, Brand brand);
    }

    public interface OnBrandLongClickListener {
        void onBrandLongClick(int position, Brand brand, View view);
    }

    public BrandAdapter(Context context, List<Brand> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    public void setOnBrandClickListener(OnBrandClickListener listener) {
        this.listener = listener;
    }

    public void setOnBrandLongClickListener(OnBrandLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brand_card, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);

        // Set brand name
        holder.brandName.setText(brand.getName());

        // Set icon
        holder.brandIcon.setImageResource(brand.getIconResId());
        holder.brandIcon.setColorFilter(context.getResources().getColor(brand.getIconTintColor()));

        // Set icon background
        holder.iconContainer.setBackgroundResource(brand.getIconBgResId());

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBrandClick(position, brand);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onBrandLongClick(position, brand, v);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    // Method to remove brand
    public void removeBrand(int position) {
        if (position >= 0 && position < brandList.size()) {
            brandList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, brandList.size());
        }
    }

    // Method to get brand at position
    public Brand getBrandAt(int position) {
        return brandList.get(position);
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        CardView brandCard;
        LinearLayout iconContainer;
        ImageView brandIcon;
        TextView brandName;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandCard = itemView.findViewById(R.id.card_brand);
            iconContainer = itemView.findViewById(R.id.icon_container);
            brandIcon = itemView.findViewById(R.id.iv_brand_icon);
            brandName = itemView.findViewById(R.id.tv_brand_name);
        }
    }
}