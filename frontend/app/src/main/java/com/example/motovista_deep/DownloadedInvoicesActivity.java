package com.example.motovista_deep;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motovista_deep.adapters.DownloadedInvoiceAdapter;
import com.example.motovista_deep.models.DownloadedInvoice;

import java.util.ArrayList;
import java.util.List;

public class DownloadedInvoicesActivity extends AppCompatActivity {

    private static final String TAG = "DownloadedInvoices";
    private RecyclerView rvInvoices;
    private LinearLayout emptyState;
    private ImageView btnBack;
    private DownloadedInvoiceAdapter adapter;
    private List<DownloadedInvoice> invoiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_invoices);

        btnBack = findViewById(R.id.btnBack);
        rvInvoices = findViewById(R.id.rvInvoices);
        emptyState = findViewById(R.id.emptyState);

        btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadDownloadedInvoices();
    }

    private void setupRecyclerView() {
        invoiceList = new ArrayList<>();
        rvInvoices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DownloadedInvoiceAdapter(invoiceList, this::openInvoice);
        rvInvoices.setAdapter(adapter);
    }

    private void loadDownloadedInvoices() {
        invoiceList.clear();
        
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + " LIKE 'Invoice_%' AND " +
                          MediaStore.MediaColumns.MIME_TYPE + " = 'application/pdf'";
        
        String[] projection = new String[] {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.SIZE
        };

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }

        try (Cursor cursor = getContentResolver().query(
                collection,
                projection,
                selection,
                null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);

                do {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    long date = cursor.getLong(dateColumn) * 1000L; // Convert to millis
                    long size = cursor.getLong(sizeColumn);
                    
                    Uri contentUri = ContentUris.withAppendedId(collection, id);
                    
                    invoiceList.add(new DownloadedInvoice(name, contentUri, date, formatFileSize(size)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying MediaStore", e);
        }

        if (invoiceList.isEmpty()) {
            rvInvoices.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvInvoices.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void openInvoice(DownloadedInvoice invoice) {
        try {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(invoice.getFileUri(), "application/pdf");
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF", e);
            Toast.makeText(this, "No PDF viewer found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new java.text.DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optional: Add animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}