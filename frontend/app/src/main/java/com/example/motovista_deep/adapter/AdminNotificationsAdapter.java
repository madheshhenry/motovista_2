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
    private java.util.Set<Integer> selectedIds = new java.util.HashSet<>();
    private boolean selectionMode = false;
    private OnSelectionListener selectionListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(AdminNotification notification);
    }

    public interface OnSelectionListener {
        void onSelectionChanged(int count);
    }

    public void setSelectionListener(OnSelectionListener listener) {
        this.selectionListener = listener;
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

        // --- SELECTION LOGIC ---
        boolean isSelected = selectedIds.contains(item.getId());
        holder.itemView.setBackgroundColor(isSelected ? 
                holder.itemView.getContext().getResources().getColor(R.color.gray_100) : 
                android.graphics.Color.TRANSPARENT);

        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(item.getId());
            } else if (listener != null) {
                listener.onNotificationClick(item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!selectionMode) {
                selectionMode = true;
                toggleSelection(item.getId());
                return true;
            }
            return false;
        });
    }

    private void toggleSelection(int id) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(id);
        } else {
            selectedIds.add(id);
        }
        
        if (selectedIds.isEmpty()) {
            selectionMode = false;
        }

        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedIds.size());
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedIds.clear();
        selectionMode = false;
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(0);
    }

    public java.util.Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public List<AdminNotification> getItems() {
        return items;
    }

    public AdminNotification getItemAt(int position) {
        if (items != null && position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void removeItems(java.util.Set<Integer> idsToRemove) {
        if (items == null) return;
        java.util.Iterator<AdminNotification> it = items.iterator();
        while (it.hasNext()) {
            if (idsToRemove.contains(it.next().getId())) {
                it.remove();
            }
        }
        notifyDataSetChanged();
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
