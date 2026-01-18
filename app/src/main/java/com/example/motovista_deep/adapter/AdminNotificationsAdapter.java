package com.example.motovista_deep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.AdminVerification;
import com.example.motovista_deep.utils.ImageUtils;
import java.util.List;

public class AdminNotificationsAdapter extends RecyclerView.Adapter<AdminNotificationsAdapter.ViewHolder> {

    private List<AdminVerification> items;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onVerificationClick(AdminVerification verification);
    }

    public AdminNotificationsAdapter(List<AdminVerification> items, OnNotificationClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminVerification item = items.get(position);
        holder.tvTitle.setText("EMI Verification: " + item.getBikeName());
        holder.tvDesc.setText("Customer: " + item.getCustomerName() + " - ₹" + item.getAmountPaid());
        holder.tvTime.setText(item.getCreatedAt());
 
        // Load image using centralized ImageUtils
        String imageUrl = ImageUtils.getFullImageUrl(item.getBikeImage());
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .into(holder.ivBike);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onVerificationClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvTime;
        ImageView ivBike;
 
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvDesc = itemView.findViewById(R.id.tvNotificationDesc);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            ivBike = itemView.findViewById(R.id.ivBikeThumbnail);
        }
    }
}
