package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.BikeVariantModel;
import java.util.List;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    private Context context;
    private List<BikeVariantModel> variantList;
    private int selectedPosition = 0;
    private OnVariantClickListener listener;

    public interface OnVariantClickListener {
        void onVariantClick(BikeVariantModel variant, int position);
    }

    public VariantAdapter(Context context, List<BikeVariantModel> variantList, OnVariantClickListener listener) {
        this.context = context;
        this.variantList = variantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_variant_chip, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        BikeVariantModel variant = variantList.get(position);
        holder.tvVariantName.setText(variant.variantName != null ? variant.variantName : "Variant " + (position + 1));

        if (selectedPosition == position) {
            // Selected Style
            holder.container.setBackgroundResource(R.drawable.bg_card_dark);
            holder.tvVariantName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            // Unselected Style
            holder.container.setBackgroundResource(R.drawable.bg_input_new);
            holder.tvVariantName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onVariantClick(variant, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variantList != null ? variantList.size() : 0;
    }
    
    // Method to update selection programmatically if needed
    public void setSelectedPosition(int position) {
        int previous = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previous);
        notifyItemChanged(selectedPosition);
    }

    public static class VariantViewHolder extends RecyclerView.ViewHolder {
        TextView tvVariantName;
        LinearLayout container;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVariantName = itemView.findViewById(R.id.tvVariantName);
            container = itemView.findViewById(R.id.cardVariant);
        }
    }
}
