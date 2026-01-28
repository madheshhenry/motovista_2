package com.example.motovista_deep.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.R;
import com.example.motovista_deep.models.DownloadedInvoice;

import java.util.List;

public class DownloadedInvoiceAdapter extends RecyclerView.Adapter<DownloadedInvoiceAdapter.ViewHolder> {

    private final List<DownloadedInvoice> invoices;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DownloadedInvoice invoice);
    }

    public DownloadedInvoiceAdapter(List<DownloadedInvoice> invoices, OnItemClickListener listener) {
        this.invoices = invoices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downloaded_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadedInvoice invoice = invoices.get(position);
        holder.tvFileName.setText(invoice.getFileName());
        
        String date = DateFormat.format("dd MMM yyyy", invoice.getDateMillis()).toString();
        holder.tvFileDetails.setText(date + " • " + invoice.getFormattedSize());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(invoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvFileDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileDetails = itemView.findViewById(R.id.tvFileDetails);
        }
    }
}
