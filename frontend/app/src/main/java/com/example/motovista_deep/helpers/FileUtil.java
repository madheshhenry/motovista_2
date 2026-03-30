package com.example.motovista_deep.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtil {

    public static String getPath(Context context, Uri uri) {
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column);
                cursor.close();
                return path;
            }
        } catch (Exception ignored) {}

        return uri.getPath();
    }
}
