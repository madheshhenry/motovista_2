package com.example.motovista_deep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.InsuranceModel;
import java.util.List;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.InsuranceViewHolder> {

    private List<InsuranceModel> insuranceList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(InsuranceModel item);
    }

    public InsuranceAdapter(List<InsuranceModel> insuranceList, OnItemClickListener listener) {
        this.insuranceList = insuranceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InsuranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insurance_card, parent, false);
        return new InsuranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsuranceViewHolder holder, int position) {
        InsuranceModel item = insuranceList.get(position);
        holder.tvName.setText(item.getCustomerName());
        holder.tvBike.setText(item.getBikeName());
        holder.tvPolicy.setText("Policy #" + item.getPolicyNumber());
        holder.tvFullExp.setText(item.getFullInsuranceExpiry());
        holder.tvTPExp.setText(item.getThirdPartyExpiry());
        holder.tvStatus.setText(item.getStatus());

        // Status styling
        if ("Expired".equalsIgnoreCase(item.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.rounded_corner_red);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#dc2626"));
        } else if ("Expiring Soon".equalsIgnoreCase(item.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.rounded_corner_yellow);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#d97706"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.rounded_corner_green);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#16a34a"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    public void updateList(List<InsuranceModel> newList) {
        this.insuranceList = newList;
        notifyDataSetChanged();
    }

    public static class InsuranceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBike, tvPolicy, tvFullExp, tvTPExp, tvStatus;

        public InsuranceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvBike = itemView.findViewById(R.id.tvBikeModel);
            tvPolicy = itemView.findViewById(R.id.tvPolicyNumber);
            tvFullExp = itemView.findViewById(R.id.tvFullExpiry);
            tvTPExp = itemView.findViewById(R.id.tvThirdPartyExpiry);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
