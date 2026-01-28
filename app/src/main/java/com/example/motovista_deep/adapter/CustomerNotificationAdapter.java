package com.example.motovista_deep.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.CustomerNotification;
import java.util.List;

public class CustomerNotificationAdapter extends RecyclerView.Adapter<CustomerNotificationAdapter.ViewHolder> {

    private Context context;
    private List<CustomerNotification> notifications;

    public CustomerNotificationAdapter(Context context, List<CustomerNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerNotification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(notification.getTimestamp());

        // Set icon based on type
        switch (notification.getType()) {
            case "order":
                holder.ivIcon.setImageResource(R.drawable.ic_receipt_long);
                break;
            case "emi":
                holder.ivIcon.setImageResource(R.drawable.ic_calculate);
                break;
            case "offer":
                holder.ivIcon.setImageResource(R.drawable.ic_confirmation_number); // Using ticket icon for offers
                break;
            default:
                holder.ivIcon.setImageResource(R.drawable.ic_notifications);
                break;
        }

        holder.unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvMessage, tvTime;
        View unreadIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }
    }
}
