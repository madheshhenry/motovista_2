package com.example.motovista_deep.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.R;
import com.example.motovista_deep.models.BikeRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BikeRequestAdapter extends RecyclerView.Adapter<BikeRequestAdapter.ViewHolder> {

    private Context context;
    private List<BikeRequest> requestList;
    private OnActionListener listener;
    private boolean isAdmin = true; // Default to true for backward compatibility

    public interface OnActionListener {
        void onAccept(BikeRequest request);
        void onReject(BikeRequest request);
    }

    public interface OnLongClickListener {
        void onLongClick(BikeRequest request);
    }

    private OnLongClickListener longClickListener;

    public BikeRequestAdapter(Context context, List<BikeRequest> requestList, OnActionListener listener) {
        this(context, requestList, listener, null);
    }

    public BikeRequestAdapter(Context context, List<BikeRequest> requestList, OnActionListener listener, OnLongClickListener longClickListener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
        this.longClickListener = longClickListener;
        this.isAdmin = (listener != null);
    }
    
    // Constructor for ReadOnly mode (Customer side)
    public BikeRequestAdapter(Context context, List<BikeRequest> requestList) {
        this(context, requestList, (OnLongClickListener) null);
    }

    public BikeRequestAdapter(Context context, List<BikeRequest> requestList, OnLongClickListener longClickListener) {
        this.context = context;
        this.requestList = requestList;
        this.longClickListener = longClickListener;
        this.isAdmin = false;
        this.listener = null;
    }

    public void updateList(List<BikeRequest> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bike_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BikeRequest request = requestList.get(position);

        holder.tvCustomerName.setText(request.getFullName());
        
        // Date Formatting
        String dateStr = request.getCreatedAt();
        if (dateStr != null) {
             try {
                 SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                 Date date = inputFormat.parse(dateStr);
                 SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                 holder.tvDate.setText(outputFormat.format(date));
             } catch (ParseException e) {
                 holder.tvDate.setText(dateStr);
             }
        }

        holder.tvBikeModel.setText(request.getBrand() + " " + request.getModel());
        
        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            holder.tvFeatures.setText("Note: " + request.getFeatures());
            holder.tvFeatures.setVisibility(View.VISIBLE);
        } else {
            holder.tvFeatures.setVisibility(View.GONE);
        }

        holder.tvMobile.setText(request.getMobileNumber());
        
        // Status Handling
        String status = request.getStatus() != null ? request.getStatus().toLowerCase() : "pending";
        holder.tvStatus.setText(status.toUpperCase());
        
        if (status.equals("accepted") || status.equals("approved")) {
            holder.tvStatus.setTextColor(Color.parseColor("#047857")); // Green Text
            holder.tvStatus.setBackgroundResource(R.drawable.pill_blue_light); 
            holder.layoutActions.setVisibility(View.GONE); 
        } else if (status.equals("rejected")) {
            holder.tvStatus.setTextColor(Color.parseColor("#b91c1c")); // Red Text
            holder.layoutActions.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#d97706")); // Amber/Orange
            // Only show actions if Admin AND status is pending
            if (isAdmin) {
                holder.layoutActions.setVisibility(View.VISIBLE);
            } else {
                holder.layoutActions.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(request);
                return true;
            }
            return false;
        });

        // Listeners (only if admin)
        if (isAdmin && listener != null) {
            holder.btnAccept.setOnClickListener(v -> listener.onAccept(request));
            holder.btnReject.setOnClickListener(v -> listener.onReject(request));
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvDate, tvStatus, tvBikeModel, tvFeatures, tvMobile;
        LinearLayout layoutActions;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBikeModel = itemView.findViewById(R.id.tvBikeModel);
            tvFeatures = itemView.findViewById(R.id.tvFeatures);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
