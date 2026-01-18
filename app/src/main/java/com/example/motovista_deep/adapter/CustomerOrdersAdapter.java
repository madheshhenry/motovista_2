package com.example.motovista_deep.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.OrderStatusActivity;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.CustomerRequest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.ViewHolder> {

    private Context context;
    private List<CustomerRequest> orderList;
    private OnLongClickListener longClickListener;

    public interface OnLongClickListener {
        void onLongClick(CustomerRequest order);
    }

    public CustomerOrdersAdapter(Context context, List<CustomerRequest> orderList) {
        this(context, orderList, null);
    }

    public CustomerOrdersAdapter(Context context, List<CustomerRequest> orderList, OnLongClickListener longClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerRequest order = orderList.get(position);

        holder.tvBikeName.setText(order.getBike_name());
        holder.tvBikeVariant.setText(order.getBike_variant());
        holder.tvOrderId.setText("#ORD" + order.getId());

        // Date Formatting
        String date = order.getCreated_at();
        if (date != null) {
            try {
                SimpleDateFormat inputElem = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputElem = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                date = outputElem.format(inputElem.parse(date));
            } catch (Exception e) {
                // keep original
            }
        } else {
            date = "Unknown Date";
        }
        holder.tvOrderDate.setText(date);

        // Status Logic
        String status = order.getStatus();
        if (status == null) status = "Pending";
        String displayStatus = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        
        holder.tvStatusBadge.setText(displayStatus);

        if (status.equalsIgnoreCase("approved") || status.equalsIgnoreCase("accepted")) {
            holder.tvStatusBadge.setTextColor(Color.parseColor("#15803d")); // Green
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_green_bg);
        } else if (status.equalsIgnoreCase("rejected")) {
            holder.tvStatusBadge.setTextColor(Color.parseColor("#b91c1c")); // Red
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_red_bg); // Assuming you have red bg or reuse logic
            // If no red bg, fallback to grey or programmatically set
        } else if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("delivered")) {
             holder.tvStatusBadge.setTextColor(Color.parseColor("#15803d")); // Green
             holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_green_bg);
             holder.tvStatusBadge.setText("Completed");
        } else {
            // Pending
            holder.tvStatusBadge.setTextColor(Color.parseColor("#d97706")); // Yellow
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_yellow_bg);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderStatusActivity.class);
            intent.putExtra("ORDER_ID", String.valueOf(order.getId()));
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(order);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBikeName, tvBikeVariant, tvOrderDate, tvStatusBadge, tvOrderId;
        ImageView ivBikeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvBikeVariant = itemView.findViewById(R.id.tvBikeVariant);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            ivBikeImage = itemView.findViewById(R.id.ivBikeImage);
        }
    }
}
