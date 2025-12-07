package com.example.motovista;

// FileUtils.java (use one of many available implementations — this is a simple content->file copy fallback)
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.io.*;

public class FileUtils {
    public static String getPath(Context context, Uri uri) {
        // If file scheme is file://, return path directly
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        // Otherwise copy content to temp file
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            String fileName = getFileName(context, uri);
            if (fileName == null) fileName = "temp_image_" + System.currentTimeMillis();
            File tempFile = new File(context.getCacheDir(), fileName);
            tempFile.createNewFile();
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[8192];
            int len;
            while ((len = input.read(buf)) > 0) out.write(buf, 0, len);
            out.close();
            input.close();
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String res = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) res = cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return res;
    }
}
