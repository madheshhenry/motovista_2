package com.example.motovista_deep.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static final int REQUEST_STORAGE_PERMISSION = 100;
    public static final int REQUEST_MANAGE_STORAGE = 101;

    public static boolean checkStoragePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            return Environment.isExternalStorageManager();
        } else {
            // Android 10 and below
            int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            return writePermission == PackageManager.PERMISSION_GRANTED &&
                    readPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void requestStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
                activity.startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
            }
        } else {
            // Android 10 and below
            List<String> permissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!permissions.isEmpty()) {
                ActivityCompat.requestPermissions(activity,
                        permissions.toArray(new String[0]),
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use FileProvider for Android 7.0+
            return FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider",
                    file);
        } else {
            // For older versions
            return Uri.fromFile(file);
        }
    }

    public static void sharePdfFile(Context context, File pdfFile) {
        try {
            Uri pdfUri = getFileUri(context, pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vehicle Document");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Please find attached the document.");

            // Grant read permission to the receiving app
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Share PDF via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openPdfFile(Context context, File pdfFile) {
        try {
            Uri pdfUri = getFileUri(context, pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(Intent.createChooser(intent, "Open PDF with"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}