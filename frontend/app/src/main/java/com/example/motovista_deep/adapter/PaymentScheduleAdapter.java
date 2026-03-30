package com.example.motovista_deep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.R;
import com.example.motovista_deep.models.PaymentScheduleItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentScheduleAdapter extends RecyclerView.Adapter<PaymentScheduleAdapter.ViewHolder> {

    private List<PaymentScheduleItem> items;
    private Context context;
    private OnPaymentClickListener listener;
    private boolean isAdmin;
    private boolean isEmiCompleted;

    public interface OnPaymentClickListener {
        void onNotifyPaid(PaymentScheduleItem item);
        void onAdminMarkAsPaid(PaymentScheduleItem item);
    }

    public PaymentScheduleAdapter(List<PaymentScheduleItem> items, boolean isAdmin, boolean isEmiCompleted, OnPaymentClickListener listener) {
        this.items = items;
        this.isAdmin = isAdmin;
        this.isEmiCompleted = isEmiCompleted;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_payment_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentScheduleItem item = items.get(position);
        
        holder.tvDueDate.setText(item.getStatus().equalsIgnoreCase("paid") 
                ? "Paid on " + formatDate(item.getPaymentDate()) 
                : (item.getStatus().equalsIgnoreCase("overdue") ? "Overdue by " + calculateDays(item.getDueDate()) : "Due on " + formatDate(item.getDueDate())));
        
        String amountText = "₹" + item.getAmount();
        if (item.getFine() > 0) amountText += " + Fine";
        holder.tvInstallmentDetail.setText("Installment #" + item.getInstallmentNo() + " • " + amountText);
        
        holder.tvStatusBadge.setText(item.getStatus());
        
        // Reset styles based on status
        int iconBg, iconColor, badgeBg, badgeTextColor, iconRes;
        holder.mainContainer.setAlpha(1.0f);
        holder.tvDueDate.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
        
        switch (item.getStatus().toLowerCase()) {
            case "paid":
                iconBg = R.color.emi_accent_green;
                iconColor = R.color.emi_success_green;
                badgeBg = R.drawable.bg_badge_green;
                badgeTextColor = R.color.green_800;
                iconRes = R.drawable.ic_check_circle;
                holder.layoutAction.setVisibility(View.GONE);
                break;
            case "reviewing":
                iconBg = R.color.emi_accent_blue;
                iconColor = R.color.emi_primary;
                badgeBg = R.drawable.bg_badge_active; // Use the green/blue rounded badge
                badgeTextColor = R.color.emi_racing_blue;
                iconRes = R.drawable.ic_info;
                holder.layoutAction.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                holder.tvActionHint.setText("Customer notified but payment not verified");
                break;
            case "overdue":
                iconBg = R.color.emi_accent_red;
                iconColor = R.color.icon_red;
                badgeBg = R.drawable.bg_badge_red;
                badgeTextColor = R.color.red_800;
                iconRes = R.drawable.ic_warning;
                holder.layoutAction.setVisibility(isAdmin ? View.VISIBLE : View.VISIBLE);
                holder.tvActionHint.setText(isAdmin ? "Mark this overdue EMI as paid" : "Notify admin of your late payment");
                holder.tvDueDate.setTextColor(ContextCompat.getColor(context, R.color.red_600));
                break;
            case "pending":
                iconBg = R.color.emi_accent_yellow;
                iconColor = R.color.amber_600;
                badgeBg = R.drawable.bg_badge_yellow;
                badgeTextColor = R.color.amber_800;
                iconRes = R.drawable.ic_schedule;
                holder.layoutAction.setVisibility(View.VISIBLE);
                holder.tvActionHint.setText(isAdmin ? "Mark this pending EMI as paid" : "Press after paying at showroom / UPI");
                break;
            default: // upcoming
                iconBg = R.color.gray_100;
                iconColor = R.color.gray_400;
                badgeBg = R.drawable.bg_badge_gray;
                badgeTextColor = R.color.gray_600;
                iconRes = R.drawable.ic_event;
                holder.layoutAction.setVisibility(View.GONE);
                holder.mainContainer.setAlpha(0.6f);
                break;
        }

        // Final override: if EMI is overall completed, hide all action buttons for admin too
        if (isEmiCompleted) {
            holder.layoutAction.setVisibility(View.GONE);
        }

        holder.cvIcon.setCardBackgroundColor(ContextCompat.getColor(context, iconBg));
        holder.ivStatusIcon.setImageResource(iconRes);
        holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, iconColor));
        holder.tvStatusBadge.setBackgroundResource(badgeBg);
        holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, badgeTextColor));
        
        holder.btnPaidNotify.setOnClickListener(v -> {
            if (listener != null) {
                if (isAdmin) {
                    listener.onAdminMarkAsPaid(item);
                } else {
                    listener.onNotifyPaid(item);
                }
            }
        });

        if (isAdmin) {
            if (item.getStatus().equalsIgnoreCase("reviewing")) {
                holder.btnPaidNotify.setText("APPROVE PAYMENT");
            } else {
                holder.btnPaidNotify.setText("MARK AS PAID");
            }
            holder.btnPaidNotify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "N/A";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = in.parse(dateStr);
            return out.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String calculateDays(String dueDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date due = sdf.parse(dueDate);
            long diff = new Date().getTime() - due.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            return days + " days";
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvIcon;
        ImageView ivStatusIcon;
        TextView tvDueDate, tvInstallmentDetail, tvStatusBadge, tvActionHint;
        LinearLayout layoutAction, mainContainer;
        Button btnPaidNotify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvIcon = itemView.findViewById(R.id.cvIcon);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvInstallmentDetail = itemView.findViewById(R.id.tvInstallmentDetail);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            layoutAction = itemView.findViewById(R.id.layoutAction);
            mainContainer = itemView.findViewById(R.id.mainContainer);
            btnPaidNotify = itemView.findViewById(R.id.btnPaidNotify);
            tvActionHint = itemView.findViewById(R.id.tvActionHint);
        }
    }
}
