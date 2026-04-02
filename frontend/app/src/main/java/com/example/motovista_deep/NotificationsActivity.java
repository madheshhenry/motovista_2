package com.example.motovista_deep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motovista_deep.adapter.AdminNotificationsAdapter;
import com.example.motovista_deep.api.ApiService;
import com.example.motovista_deep.api.RetrofitClient;
import com.example.motovista_deep.models.AdminNotification;
import com.example.motovista_deep.models.AdminNotificationListResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements AdminNotificationsAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private ImageView btnBack, btnDelete;
    private android.widget.TextView tvHeaderTitle;
    private AdminNotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> {
            if (adapter != null && adapter.isSelectionMode()) {
                adapter.clearSelection();
            } else {
                onBackPressed();
            }
        });

        btnDelete.setOnClickListener(v -> showMultiDeleteConfirmation());

        fetchNotifications();
        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback callback = 
            new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, 
                androidx.recyclerview.widget.ItemTouchHelper.LEFT) {
            
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                AdminNotification notification = adapter.getItemAt(position);
                showDeleteConfirmation(notification, position);
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Custom drawing for Swipe-to-Delete background and icon
                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getBottom() - itemView.getTop();
                
                // 1. Draw Red Background
                android.graphics.drawable.ColorDrawable background = new android.graphics.drawable.ColorDrawable(
                        androidx.core.content.ContextCompat.getColor(NotificationsActivity.this, R.color.red_500));
                background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                
                // 2. Draw Trash Icon
                android.graphics.drawable.Drawable icon = androidx.core.content.ContextCompat.getDrawable(NotificationsActivity.this, R.drawable.ic_delete);
                if (icon != null) {
                    int iconMargin = (itemHeight - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();
                    
                    icon.setTint(android.graphics.Color.WHITE);
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    
                    // Only draw icon if swiped enough
                    if (dX < -100) icon.draw(c);
                }
                
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new androidx.recyclerview.widget.ItemTouchHelper(callback).attachToRecyclerView(rvNotifications);
    }

    private void showDeleteConfirmation(AdminNotification notification, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> deleteSingleNotification(notification, position))
                .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    private void showMultiDeleteConfirmation() {
        int count = adapter.getSelectedIds().size();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Multiple")
                .setMessage("Delete " + count + " selected notifications?")
                .setPositiveButton("Delete", (dialog, which) -> performBatchDelete())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteSingleNotification(AdminNotification notification, int position) {
        java.util.List<com.example.motovista_deep.models.DeleteNotificationsRequest.NotificationItem> items = new java.util.ArrayList<>();
        items.add(new com.example.motovista_deep.models.DeleteNotificationsRequest.NotificationItem(notification.getId(), notification.getType()));
        
        com.example.motovista_deep.models.DeleteNotificationsRequest request = new com.example.motovista_deep.models.DeleteNotificationsRequest(items);
        
        String token = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getToken();
        RetrofitClient.getApiService().deleteNotifications("Bearer " + token, request).enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    java.util.Set<Integer> singleton = new java.util.HashSet<>();
                    singleton.add(notification.getId());
                    adapter.removeItems(singleton);
                    Toast.makeText(NotificationsActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(NotificationsActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                adapter.notifyItemChanged(position);
                Toast.makeText(NotificationsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performBatchDelete() {
        java.util.List<com.example.motovista_deep.models.DeleteNotificationsRequest.NotificationItem> itemsToDelete = new java.util.ArrayList<>();
        java.util.Set<Integer> selectedIds = adapter.getSelectedIds();

        // Need to iterate through full items to get types
        // Using a more efficient way if adapter provided a way, 
        // but for batch delete we need to collect all selected items
        for (AdminNotification n : adapter.getItems()) {
            if (selectedIds.contains(n.getId())) {
                itemsToDelete.add(new com.example.motovista_deep.models.DeleteNotificationsRequest.NotificationItem(n.getId(), n.getType()));
            }
        }

        com.example.motovista_deep.models.DeleteNotificationsRequest request = new com.example.motovista_deep.models.DeleteNotificationsRequest(itemsToDelete);
        String token = com.example.motovista_deep.helpers.SharedPrefManager.getInstance(this).getToken();

        RetrofitClient.getApiService().deleteNotifications("Bearer " + token, request).enqueue(new Callback<com.example.motovista_deep.models.GenericResponse>() {
            @Override
            public void onResponse(Call<com.example.motovista_deep.models.GenericResponse> call, Response<com.example.motovista_deep.models.GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.removeItems(selectedIds);
                    adapter.clearSelection();
                    Toast.makeText(NotificationsActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.motovista_deep.models.GenericResponse> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNotifications() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAdminNotificationList().enqueue(new Callback<AdminNotificationListResponse>() {
            @Override
            public void onResponse(Call<AdminNotificationListResponse> call, Response<AdminNotificationListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    setupAdapter(response.body().getData());
                } else {
                    Toast.makeText(NotificationsActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminNotificationListResponse> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<AdminNotification> notifications) {
        adapter = new AdminNotificationsAdapter(notifications, this);
        adapter.setSelectionListener(count -> {
            if (count > 0) {
                btnDelete.setVisibility(View.VISIBLE);
                tvHeaderTitle.setText(count + " Selected");
                btnBack.setImageResource(R.drawable.ic_close); // Change arrow to X
            } else {
                btnDelete.setVisibility(View.GONE);
                tvHeaderTitle.setText("Notifications");
                btnBack.setImageResource(R.drawable.ic_arrow_back_ios);
            }
        });
        rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onNotificationClick(AdminNotification notification) {
        String screen = notification.getTargetScreen();
        String id = notification.getItemId();

        if (screen != null) {
            Intent intent = null;
            if ("EmiDetailsActivity".equals(screen)) {
                intent = new Intent(this, EmiDetailsActivity.class);
                intent.putExtra("LEDGER_ID", id != null ? Integer.parseInt(id) : -1);
                intent.putExtra("IS_CUSTOMER_VIEW", false);
            } else if ("OrderSummaryActivity".equals(screen)) {
                intent = new Intent(this, OrderSummaryActivity.class);
                if (id != null) intent.putExtra("request_id", Integer.parseInt(id));
            } else if ("CustomerDetailsActivity".equals(screen)) {
                intent = new Intent(this, CustomerDetailsActivity.class);
                if (id != null) intent.putExtra("customer_id", Integer.parseInt(id));
            }

            if (intent != null) {
                startActivity(intent);
                return;
            }
        }

        // Fallback or general notification
        Toast.makeText(this, notification.getTitle(), Toast.LENGTH_SHORT).show();
    }
}