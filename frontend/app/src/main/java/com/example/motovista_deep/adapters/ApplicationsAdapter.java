package com.example.motovista_deep.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.R;
import com.example.motovista_deep.models.CustomerRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ViewHolder> {

    private Context context;
    private List<CustomerRequest> requests;
    private OnItemClickListener listener;
    private OnLongItemClickListener longListener;

    public interface OnItemClickListener {
        void onItemClick(CustomerRequest request);
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(CustomerRequest request);
    }

    public ApplicationsAdapter(Context context, List<CustomerRequest> requests, OnItemClickListener listener) {
        this(context, requests, listener, null);
    }

    public ApplicationsAdapter(Context context, List<CustomerRequest> requests, OnItemClickListener listener, OnLongItemClickListener longListener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
        this.longListener = longListener;
    }
    
    public void updateList(List<CustomerRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerRequest req = requests.get(position);
        
        // Name
        holder.tvCustomerName.setText(req.getCustomer_name());
        
        // Date (Format "Today, 09:41 AM" / "Oct 21, 2023")
        String dateStr = req.getCreated_at();
        if (dateStr != null) {
             try {
                 SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                 Date date = inputFormat.parse(dateStr);
                 
                 SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                 holder.tvRequestDate.setText(outputFormat.format(date));
             } catch (ParseException e) {
                 holder.tvRequestDate.setText(dateStr);
             }
        } else {
             holder.tvRequestDate.setText("Just Now");
        }

        // Status Badge Logic
        String status = req.getStatus() != null ? req.getStatus().toLowerCase() : "pending";
        setupStatusBadge(holder, status);

        // Bike Details
        holder.tvBikeName.setText(req.getBike_name());
        
        if (req.getBike_variant() != null && !req.getBike_variant().isEmpty()) {
            holder.tvBikeVariant.setText(req.getBike_variant());
        } else {
            holder.tvBikeVariant.setText("Standard Variant");
        }

        // Color Badge Logic
        setupColorBadge(holder, req.getBike_color());

        // Click Listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(req));
        holder.itemView.setOnLongClickListener(v -> {
            if (longListener != null) {
                longListener.onLongItemClick(req);
                return true;
            }
            return false;
        });
        holder.btnViewDetails.setOnClickListener(v -> listener.onItemClick(req));
    }

    private void setupStatusBadge(ViewHolder holder, String status) {
        int bgColor, textColor, dotColor;
        String label;

        if (status.equals("completed")) {
            bgColor = Color.parseColor("#ecfdf5"); // Green-50
            textColor = Color.parseColor("#047857"); // Green-700
            dotColor = Color.parseColor("#10b981"); // Green-500
            label = "COMPLETED";
        } else if (status.equals("approved") || status.equals("accepted")) {
            bgColor = Color.parseColor("#ecfdf5"); // Green-50
            textColor = Color.parseColor("#047857"); // Green-700
            dotColor = Color.parseColor("#10b981"); // Green-500
            label = "ACCEPTED";
        } else if (status.equals("rejected")) {
            bgColor = Color.parseColor("#fef2f2"); // Red-50
            textColor = Color.parseColor("#b91c1c"); // Red-700
            dotColor = Color.parseColor("#ef4444"); // Red-500
            label = "REJECTED";
        } else {
            // New / Pending
            bgColor = Color.parseColor("#eff6ff"); // Blue-50
            textColor = Color.parseColor("#1d4ed8"); // Blue-700
            dotColor = Color.parseColor("#3b82f6"); // Blue-500
            label = "NEW";
        }

        // Apply Status Colors
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(100f);
        bg.setColor(bgColor);
        holder.statusBadgeContainer.setBackground(bg);

        holder.tvStatus.setText(label);
        holder.tvStatus.setTextColor(textColor);

        GradientDrawable dot = new GradientDrawable();
        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(dotColor);
        holder.statusDot.setBackground(dot);
    }

    private void setupColorBadge(ViewHolder holder, String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            holder.layoutBikeColor.setVisibility(View.GONE);
            return;
        }

        String displayColorName = colorStr;
        int colorHex = Color.GRAY; // Default

        // Try parsing "Name|Hex" format
        if (colorStr.contains("|")) {
            String[] parts = colorStr.split("\\|");
            if (parts.length >= 2) {
                displayColorName = parts[0];
                try {
                    colorHex = Color.parseColor(parts[1]);
                } catch (IllegalArgumentException e) {
                    // Fallback
                }
            }
        }

        holder.layoutBikeColor.setVisibility(View.VISIBLE);
        holder.tvBikeColor.setText(displayColorName);

        // Apply Color Pill Styling
        // Background: Light tint (10% opacity) of the color
        int bgTint = androidx.core.graphics.ColorUtils.setAlphaComponent(colorHex, 30);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(12f); // Rounded rect for pill
        bg.setColor(bgTint);
        bg.setStroke(2, androidx.core.graphics.ColorUtils.setAlphaComponent(colorHex, 50)); // Subtle border
        holder.layoutBikeColor.setBackground(bg);

        // Dot Color
        GradientDrawable dot = new GradientDrawable();
        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(colorHex);
        holder.ivColorDot.setBackground(dot);
        
        // Text Color (Darker version of the color for readability) -> or just dark gray
        // Ideally we pick a readable text color, but standard Gray-700 usually works well on light tints
        holder.tvBikeColor.setTextColor(Color.parseColor("#374151")); 
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvRequestDate, tvStatus, tvBikeName, tvBikeVariant, tvBikeColor;
        LinearLayout statusBadgeContainer, layoutBikeColor, btnViewDetails;
        View statusDot, ivColorDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvBikeVariant = itemView.findViewById(R.id.tvBikeVariant);
            tvBikeColor = itemView.findViewById(R.id.tvBikeColor);
            
            statusBadgeContainer = itemView.findViewById(R.id.statusBadgeContainer);
            statusDot = itemView.findViewById(R.id.statusDot);
            
            layoutBikeColor = itemView.findViewById(R.id.layoutBikeColor);
            ivColorDot = itemView.findViewById(R.id.ivColorDot);
            
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
