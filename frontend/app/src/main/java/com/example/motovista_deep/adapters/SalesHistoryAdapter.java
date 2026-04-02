package com.example.motovista_deep.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.SalesHistoryItem;
import java.util.List;

public class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.ViewHolder> {

    private List<SalesHistoryItem> salesList;
    private OnSaleClickListener listener;

    public interface OnSaleClickListener {
        void onSaleClick(SalesHistoryItem item);
    }

    public SalesHistoryAdapter(List<SalesHistoryItem> salesList, OnSaleClickListener listener) {
        this.salesList = salesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalesHistoryItem item = salesList.get(position);
        holder.tvBikeName.setText(item.getBrand() + " " + item.getModel());
        holder.tvCustomerName.setText("Customer: " + item.getCustomerName());
        holder.tvSaleDate.setText("Date: " + item.getFormattedDate());
        String price = item.getTotalValue();
        if (price != null) {
            price = price.replaceAll("[^0-9.]", "").trim();
            holder.tvPrice.setText("₹" + price);
        } else {
            holder.tvPrice.setText("₹0.00");
        }
        holder.tvPaymentType.setText(item.getPaymentType());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaleClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salesList != null ? salesList.size() : 0;
    }

    public void updateList(List<SalesHistoryItem> newList) {
        this.salesList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBikeName, tvCustomerName, tvSaleDate, tvPrice, tvPaymentType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvSaleDate = itemView.findViewById(R.id.tvSaleDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPaymentType = itemView.findViewById(R.id.tvPaymentType);
        }
    }
}
