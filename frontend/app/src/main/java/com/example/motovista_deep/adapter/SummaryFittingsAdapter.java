package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.R;

import java.util.Arrays;
import java.util.List;

public class SummaryFittingsAdapter extends RecyclerView.Adapter<SummaryFittingsAdapter.ViewHolder> {

    private final List<String> fittings;
    private final LayoutInflater inflater;

    public SummaryFittingsAdapter(Context context, String fittingsString) {
        this.inflater = LayoutInflater.from(context);
        this.fittings = Arrays.asList(fittingsString.split(",\\s*"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_fitting_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvFittingName.setText(fittings.get(position));
    }

    @Override
    public int getItemCount() {
        return fittings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFittingName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFittingName = itemView.findViewById(R.id.tvValue);
        }
    }
}
