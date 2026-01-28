package com.example.motovista_deep.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class EdgeToEdge {

    /**
     * Applies edge-to-edge display to the given activity.
     * This makes the status bar and navigation bar transparent and allows the layout to draw behind them.
     */
    public static void apply(Activity activity) {
        Window window = activity.getWindow();
        
        // 1. Set full screen / edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false);
        
        // 2. Set status and navigation bars to transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        // 3. Ensure light status bar icons for light background
        View decorView = window.getDecorView();
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, decorView);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }
        
        // 4. Handle specific older versions if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }
    }
}
