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
import com.example.motovista_deep.models.AdminNotification;
import java.util.List;

public class AdminNotificationsAdapter extends RecyclerView.Adapter<AdminNotificationsAdapter.ViewHolder> {

    private List<AdminNotification> items;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(AdminNotification notification);
    }

    public AdminNotificationsAdapter(List<AdminNotification> items, OnNotificationClickListener listener) {
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
        AdminNotification item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getMessage());
        holder.tvTime.setText(item.getTimestamp());
 
        // Set Icon based on type
        if ("emi".equals(item.getType())) {
            holder.ivBike.setImageResource(R.drawable.ic_calculate);
        } else if ("order".equals(item.getType())) {
            holder.ivBike.setImageResource(R.drawable.ic_receipt_long);
        } else {
            holder.ivBike.setImageResource(R.drawable.ic_notifications);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificationClick(item);
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
