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

import com.bumptech.glide.Glide;
import com.example.motovista_deep.BikeDetailsViewActivity;
import com.example.motovista_deep.R;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.MyBikeModel;
import com.example.motovista_deep.utils.ImageUtils;

import java.util.List;

public class MyBikesAdapter extends RecyclerView.Adapter<MyBikesAdapter.BikeViewHolder> {

    private Context context;
    private List<MyBikeModel> bikeList;

    public MyBikesAdapter(Context context, List<MyBikeModel> bikeList) {
        this.context = context;
        this.bikeList = bikeList;
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_bike, parent, false);
        return new BikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        MyBikeModel bike = bikeList.get(position);

        holder.tvBikeName.setText(bike.getBikeName());
        holder.tvBikeColor.setText(bike.getBikeColorName());
        holder.tvOrderId.setText("Order #" + bike.getRequestId());
        holder.tvPurchaseDate.setText(bike.getPurchaseDate());

        // Set color indicator (Preserving circular shape)
        if (bike.getBikeColorHex() != null && !bike.getBikeColorHex().isEmpty()) {
            try {
                android.graphics.drawable.Drawable background = holder.viewColorIndicator.getBackground();
                if (background instanceof android.graphics.drawable.GradientDrawable) {
                    ((android.graphics.drawable.GradientDrawable) background).setColor(Color.parseColor(bike.getBikeColorHex()));
                } else {
                    holder.viewColorIndicator.setBackgroundColor(Color.parseColor(bike.getBikeColorHex()));
                }
            } catch (Exception e) {
                holder.viewColorIndicator.setBackgroundColor(Color.GRAY);
            }
        }

        // Load image logic using centralized ImageUtils
        String bikeImage = bike.getBikeImage();
        String finalImageUrl = ImageUtils.getFullImageUrl(bikeImage);
 
        Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.placeholder_bike)
                .error(R.drawable.placeholder_bike)
                .into(holder.ivBikeImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BikeDetailsViewActivity.class);
            intent.putExtra("BIKE_NAME", bike.getBikeName());
            intent.putExtra("REG_NUMBER", "Pending..."); 
            intent.putExtra("PURCHASE_DATE", bike.getPurchaseDate());
            intent.putExtra("COLOR", bike.getBikeColorName());
            intent.putExtra("COLOR_HEX", bike.getBikeColorHex());
            intent.putExtra("VARIANT", bike.getBikeVariant());
            intent.putExtra("ENGINE_NUMBER", bike.getEngineNumber());
            intent.putExtra("CHASSIS_NUMBER", bike.getChassisNumber());
            intent.putExtra("BIKE_IMAGE", bike.getBikeImage()); // Pass the relative/raw path
            
            // Insurance data
            intent.putExtra("INSURER", bike.getPolicyNumber() != null ? "Policy: " + bike.getPolicyNumber() : "Calculating...");
            intent.putExtra("POLICY_NUMBER", bike.getPolicyNumber() != null ? bike.getPolicyNumber() : "N/A");
            intent.putExtra("INSURANCE_TYPE", "Comprehensive"); 
            intent.putExtra("INSURANCE_START", bike.getPurchaseDate());
            intent.putExtra("INSURANCE_END", bike.getInsuranceExpiry() != null ? bike.getInsuranceExpiry() : "TBD");
            
            // EMI data
            intent.putExtra("EMI_TOTAL", bike.getEmiTotalAmount());
            intent.putExtra("EMI_PAID", bike.getEmiPaidAmount());
            intent.putExtra("EMI_MONTHLY", bike.getEmiMonthlyAmount());
            intent.putExtra("EMI_DURATION", bike.getEmiDurationMonths());
            intent.putExtra("EMI_STATUS", bike.getEmiStatus());
            intent.putExtra("EMI_REMAINING", bike.getEmiRemainingAmount());
            intent.putExtra("LEDGER_ID", bike.getEmiLedgerId());
            intent.putExtra("REQUEST_ID", bike.getRequestId());
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    public static class BikeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBikeImage;
        TextView tvBikeName, tvBikeColor, tvOrderId, tvPurchaseDate;
        View viewColorIndicator;

        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBikeImage = itemView.findViewById(R.id.ivBikeImage);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvBikeColor = itemView.findViewById(R.id.tvBikeColor);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            viewColorIndicator = itemView.findViewById(R.id.viewColorIndicator);
        }
    }
}
