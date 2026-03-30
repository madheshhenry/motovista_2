package com.example.motovista_deep.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.R;
import com.example.motovista_deep.RegistrationRecordActivity;
import com.example.motovista_deep.models.RegistrationLedgerItem;

import java.util.ArrayList;
import java.util.List;

public class RegistrationLedgerAdapter extends RecyclerView.Adapter<RegistrationLedgerAdapter.ViewHolder> {

    private Context context;
    private List<RegistrationLedgerItem> items;
    private List<RegistrationLedgerItem> itemsFull;

    public RegistrationLedgerAdapter(Context context, List<RegistrationLedgerItem> items) {
        this.context = context;
        this.items = items;
        this.itemsFull = new ArrayList<>(items);
    }

    public void updateList(List<RegistrationLedgerItem> newList) {
        this.items = newList;
        this.itemsFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query, String statusFilter) {
        List<RegistrationLedgerItem> filteredList = new ArrayList<>();
        for (RegistrationLedgerItem item : itemsFull) {
            boolean matchesQuery = true;
            if (query != null && !query.isEmpty()) {
                String q = query.toLowerCase();
                matchesQuery = item.getCustomerName().toLowerCase().contains(q) || 
                               item.getBikeName().toLowerCase().contains(q) ||
                               (item.getEngineNumber() != null && item.getEngineNumber().toLowerCase().contains(q));
            }

            boolean matchesStatus = true;
            if (statusFilter != null && !statusFilter.equals("All")) {
                if (statusFilter.equals("Pending")) {
                    matchesStatus = item.getProgressPercentage() < 100;
                } else if (statusFilter.equals("Completed")) {
                    matchesStatus = item.getProgressPercentage() == 100;
                } else if (statusFilter.equals("Verification")) {
                    matchesStatus = !"completed".equalsIgnoreCase(item.getStep1Status());
                }
            }

            if (matchesQuery && matchesStatus) {
                filteredList.add(item);
            }
        }
        items = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_registration_ledger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistrationLedgerItem item = items.get(position);

        holder.tvCustomerName.setText(item.getCustomerName());
        holder.tvBikeName.setText(item.getBikeName() + (item.getVariant() != null ? " • " + item.getVariant() : ""));
        holder.tvPhoneNumber.setText(item.getPhone() != null ? item.getPhone() : "N/A");
        holder.tvEngineNumber.setText(item.getEngineNumber() != null ? item.getEngineNumber() : "N/A");
        
        // Format date
        String date = item.getCreatedAt();
        if(date != null && date.contains(" ")) {
            date = date.split(" ")[0];
        }
        holder.tvDate.setText(date);

        // Status Logic for the 4 icons
        setStatusIcon(holder.ivStatus1, null, item.getStep1Status());
        setStatusIcon(holder.ivStatus2, null, item.getStep2Status());
        setStatusIcon(holder.ivStatus3, holder.tvStatus3Text, item.getStep3Status());
        setStatusIcon(holder.ivStatus4, null, item.getStep4Status());

        // Accent line color based on overall progress
        int progress = item.getProgressPercentage();
        if (progress == 100) {
            holder.statusAccent.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (progress > 0) {
            holder.statusAccent.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.statusAccent.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        holder.itemView.setOnClickListener(v -> {
             Intent intent = new Intent(context, RegistrationRecordActivity.class);
             intent.putExtra("ledger_id", item.getId());
             intent.putExtra("customer_name", item.getCustomerName());
             intent.putExtra("bike_name", item.getBikeName());
             intent.putExtra("phone", item.getPhone());
             intent.putExtra("engine_number", item.getEngineNumber());
             context.startActivity(intent);
        });
    }

    private void setStatusIcon(android.widget.ImageView imageView, TextView statusText, String status) {
        if ("completed".equalsIgnoreCase(status)) {
            imageView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#f0fdf4")));
            imageView.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#16a34a")));
            if (statusText != null) {
                statusText.setText("Done");
                statusText.setTextColor(android.graphics.Color.parseColor("#16a34a"));
            }
        } else if ("pending".equalsIgnoreCase(status)) {
            imageView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#fff7ed")));
            imageView.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#ea580c")));
            if (statusText != null) {
                statusText.setText("Pending");
                statusText.setTextColor(android.graphics.Color.parseColor("#ea580c"));
            }
        } else { // locked
            imageView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#f3f4f6")));
            imageView.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9ca3af")));
            if (statusText != null) {
                statusText.setText("Locked");
                statusText.setTextColor(android.graphics.Color.parseColor("#9ca3af"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvBikeName, tvDate, tvPhoneNumber, tvEngineNumber, tvStatus3Text;
        android.view.View statusAccent;
        android.widget.ImageView ivStatus1, ivStatus2, ivStatus3, ivStatus4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvEngineNumber = itemView.findViewById(R.id.tvEngineNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus3Text = itemView.findViewById(R.id.tvStatus3Text);
            statusAccent = itemView.findViewById(R.id.statusAccent);
            ivStatus1 = itemView.findViewById(R.id.ivStatus1);
            ivStatus2 = itemView.findViewById(R.id.ivStatus2);
            ivStatus3 = itemView.findViewById(R.id.ivStatus3);
            ivStatus4 = itemView.findViewById(R.id.ivStatus4);
        }
    }
}
