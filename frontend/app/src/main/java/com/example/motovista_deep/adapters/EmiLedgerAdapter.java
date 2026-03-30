package com.example.motovista_deep.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.R;
import com.example.motovista_deep.models.EmiLedgerItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmiLedgerAdapter extends RecyclerView.Adapter<EmiLedgerAdapter.ViewHolder> {

    private Context context;
    private List<EmiLedgerItem> emiList;
    private List<EmiLedgerItem> originalList; // For filtering if needed
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EmiLedgerItem item);
    }

    public EmiLedgerAdapter(Context context, List<EmiLedgerItem> emiList, OnItemClickListener listener) {
        this.context = context;
        this.emiList = emiList != null ? emiList : new ArrayList<>();
        this.originalList = new ArrayList<>(this.emiList);
        this.listener = listener;
    }

    public void updateData(List<EmiLedgerItem> newList) {
        this.emiList = newList != null ? newList : new ArrayList<>();
        this.originalList = new ArrayList<>(this.emiList);
        notifyDataSetChanged();
    }
    
    public void filterList(String status) {
        if (status.equals("All")) {
            emiList = new ArrayList<>(originalList);
        } else {
            List<EmiLedgerItem> filtered = new ArrayList<>();
            for (EmiLedgerItem item : originalList) {
                if (item.getStatus() != null && item.getStatus().equalsIgnoreCase(status)) {
                    filtered.add(item);
                }
            }
            emiList = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emi_ledger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmiLedgerItem item = emiList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return emiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvBikeModel, tvStatus, tvMonthlyEmi, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvBikeModel = itemView.findViewById(R.id.tvBikeModel);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMonthlyEmi = itemView.findViewById(R.id.tvMonthlyEmi);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }

        public void bind(final EmiLedgerItem item, final OnItemClickListener listener) {
            tvCustomerName.setText(item.getCustomerName() != null ? item.getCustomerName() : "Unknown");
            tvBikeModel.setText(item.getVehicleName() != null ? item.getVehicleName() : "Unknown Vehicle");

            // Format Amount
            try {
                double amount = Double.parseDouble(item.getEmiMonthlyAmount());
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                String formatted = format.format(amount).replace(".00", "");
                tvMonthlyEmi.setText(formatted + "/mo");
            } catch (Exception e) {
                tvMonthlyEmi.setText("₹" + item.getEmiMonthlyAmount() + "/mo");
            }

            tvDuration.setText(item.getDurationMonths() + " months");

            // Status Styling
            String status = item.getStatus() != null ? item.getStatus() : "Active";
            tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));

            if (status.equalsIgnoreCase("completed")) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.icon_green));
                tvStatus.setBackgroundResource(R.drawable.badge_bg_green);
            } else if (status.equalsIgnoreCase("defaulted")) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.icon_red));
                tvStatus.setBackgroundResource(R.drawable.badge_bg_red); // Assume this exists or fallback
            } else {
                 // Active/Running
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.primary));
                tvStatus.setBackgroundResource(R.drawable.badge_bg_blue); // Assume simple styling
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
