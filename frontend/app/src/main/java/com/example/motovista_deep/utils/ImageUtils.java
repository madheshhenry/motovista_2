package com.example.motovista_deep.utils;

import com.example.motovista_deep.api.RetrofitClient;

public class ImageUtils {

    public static final String PATH_BIKES = "uploads/bikes/";
    public static final String PATH_SECOND_HAND_BIKES = "uploads/second_hand_bikes/";
    public static final String PATH_BRANDS = "uploads/brands/";
    public static final String PATH_PROFILE_PICS = "uploads/profile_pics/";

    /**
     * Constructs a full, valid image URL from a partial path or filename.
     */
    public static String getFullImageUrl(String path) {
        return getFullImageUrl(path, PATH_BIKES);
    }

    /**
     * Constructs a full, valid image URL from a partial path or filename with a specific default directory.
     */
    public static String getFullImageUrl(String path, String defaultDir) {
        if (path == null || path.trim().isEmpty() || "null".equalsIgnoreCase(path) || "[]".equals(path)) {
            return "";
        }

        // 1. Clean the path (remove quotes, backslashes, and leading/trailing whitespace)
        String cleanPath = path.replace("\"", "").replace("\\", "").trim();

        // 2. If it's already a full URL or resource path, return it as is
        if (cleanPath.startsWith("http://") || cleanPath.startsWith("https://") || 
            cleanPath.startsWith("android.resource://") || cleanPath.startsWith("content://") || 
            cleanPath.startsWith("file://")) {
            return cleanPath;
        }

        // 3. Remove leading slash if present to avoid double slashes
        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        // 4. Ensure the path contains 'uploads/'
        if (!cleanPath.contains("uploads/")) {
            if (cleanPath.startsWith("bikes/") || cleanPath.startsWith("second_hand_bikes/") || 
                cleanPath.startsWith("brands/") || cleanPath.startsWith("profile_pics/")) {
                cleanPath = "uploads/" + cleanPath;
            } else {
                // Default to the provided defaultDir (ensure it ends with /)
                String dir = defaultDir;
                if (!dir.endsWith("/")) dir += "/";
                
                // If the path already starts with what the defaultDir ends with, skip prepending
                // e.g. if defaultDir is uploads/bikes/ and path is bikes/image.jpg
                if (cleanPath.startsWith("bikes/") && dir.endsWith("bikes/")) {
                     cleanPath = "uploads/" + cleanPath;
                } else if (cleanPath.startsWith("profile_pics/") && dir.endsWith("profile_pics/")) {
                     cleanPath = "uploads/" + cleanPath;
                } else {
                    cleanPath = dir + cleanPath;
                }
            }
        }

        // 5. Build Base Server URL (Sanitize RetrofitClient.BASE_URL by removing 'api/')
        String apiBase = RetrofitClient.BASE_URL;
        String serverBase = apiBase;
        if (serverBase.endsWith("api/")) {
            serverBase = serverBase.substring(0, serverBase.length() - 4);
        } else if (serverBase.endsWith("api")) {
            serverBase = serverBase.substring(0, serverBase.length() - 3);
        }
        
        // Ensure serverBase ends with a slash
        if (!serverBase.endsWith("/")) {
            serverBase += "/";
        }

        return serverBase + cleanPath;
    }
}
