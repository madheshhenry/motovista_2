package com.example.motovista_deep.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class UIUtils {

    /**
     * Setups edge-to-edge display for an activity and applies padding to a target view 
     * to avoid overlap with status and navigation bars.
     */
    public static void setupEdgeToEdge(Activity activity, View targetView) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        ViewCompat.setOnApplyWindowInsetsListener(targetView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Apply padding to avoid overlap with system bars
            v.setPadding(v.getPaddingLeft(), insets.top, v.getPaddingRight(), insets.bottom);
            
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Applies top padding to avoid status bar overlap.
     */
    public static void applyStatusBarPadding(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), insets.top, v.getPaddingRight(), v.getPaddingBottom());
            return windowInsets;
        });
    }

    /**
     * Applies top padding to avoid navigation bar overlap.
     */
    public static void applyNavigationBarPadding(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.bottom);
            return windowInsets;
        });
    }

    /**
     * Premium Edge-to-Edge: Root gets Top Padding (Status Bar), ScrollView gets Bottom Padding (Nav Bar) & clipToPadding=false.
     */
    public static void setupEdgeToEdgeScroll(Activity activity, View rootView, View scrollView) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        // Terminate any previous listener to avoid conflict
        ViewCompat.setOnApplyWindowInsetsListener(rootView, null);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, null);

        // Apply Top Inset to Root (Header)
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), insets.top, v.getPaddingRight(), v.getPaddingBottom());
            return windowInsets; // Do not consume, pass down
        });

        // Apply Bottom Inset to ScrollView
        if (scrollView instanceof android.view.ViewGroup) {
            ((android.view.ViewGroup) scrollView).setClipToPadding(false);
        }
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
