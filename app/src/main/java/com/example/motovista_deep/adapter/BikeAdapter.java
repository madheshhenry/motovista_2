package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.BikeModel;
import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeViewHolder> {

    private Context context;
    private List<BikeModel> bikeList;
    private OnBikeClickListener listener;

    public interface OnBikeClickListener {
        void onBikeClick(BikeModel bike);
    }

    public BikeAdapter(Context context, List<BikeModel> bikeList, OnBikeClickListener listener) {
        this.context = context;
        this.bikeList = bikeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bike_card, parent, false);
        return new BikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        BikeModel bike = bikeList.get(position);

        // Set brand and model
        holder.tvBrand.setText(bike.getBrand());
        holder.tvModel.setText(bike.getModel());

        // Set tag based on type
        if (bike.getType().equals("NEW")) {
            holder.tvTag.setText("NEW BIKE");
            holder.tvTag.setBackground(ContextCompat.getDrawable(context, R.drawable.tag_new_bike));

            // Set price for new bikes
            holder.tvInfo.setText("₹" + bike.getPrice());
            holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.primary_color));
        } else {
            holder.tvTag.setText("SH BIKES");
            holder.tvTag.setBackground(ContextCompat.getDrawable(context, R.drawable.tag_sh_bike));

            // Set condition for second-hand bikes
            String condition = bike.getCondition();
            holder.tvInfo.setText("Condition: " + condition);

            // Set color based on condition
            if (condition.equalsIgnoreCase("Excellent")) {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_green));
            } else if (condition.equalsIgnoreCase("Good")) {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_yellow));
            } else {
                holder.tvInfo.setTextColor(ContextCompat.getColor(context, R.color.icon_red));
            }
        }

        // Load image using Glide
        if (bike.getImageUrl() != null && !bike.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(bike.getImageUrl())
                    .placeholder(R.drawable.placeholder_bike)
                    .error(R.drawable.placeholder_bike)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.ivBike);
        } else {
            holder.ivBike.setImageResource(R.drawable.placeholder_bike);
        }

        // Set click listener
        holder.cardBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBikeClick(bike);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    public void updateList(List<BikeModel> newList) {
        bikeList = newList;
        notifyDataSetChanged();
    }

    static class BikeViewHolder extends RecyclerView.ViewHolder {
        CardView cardBike;
        TextView tvTag;
        ImageView ivBike;
        TextView tvBrand;
        TextView tvModel;
        TextView tvInfo;
        ImageView ivChevron;

        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBike = itemView.findViewById(R.id.cardBike);
            tvTag = itemView.findViewById(R.id.tvTag);
            ivBike = itemView.findViewById(R.id.ivBike);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivChevron = itemView.findViewById(R.id.ivChevron);
        }
    }
}