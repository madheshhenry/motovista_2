package com.example.motovista_deep.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utility to handle system UI insets (status bar, navigation bar) dynamically.
 * Replaces fitsSystemWindows and hardcoded padding.
 */
public class SystemUIHelper {

    /**
     * Sets up edge-to-edge display and applies dynamic WindowInsets to header and footer views.
     * 
     * @param activity   The hosting activity
     * @param rootView   Root layout (gets background behind system bars)
     * @param headerView The top header/toolbar that needs status bar padding (nullable)
     * @param footerView The bottom bar/button container that needs navigation bar padding (nullable)
     */
    public static void setupEdgeToEdge(AppCompatActivity activity,
                                        View rootView,
                                        View headerView,
                                        View footerView) {
        // Enable edge-to-edge: content draws behind system bars
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        // Apply insets dynamically
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply top inset to header
            if (headerView != null) {
                headerView.setPadding(
                        headerView.getPaddingLeft(),
                        systemBars.top,
                        headerView.getPaddingRight(),
                        headerView.getPaddingBottom()
                );
            } else {
                // If no header, apply top padding to root
                view.setPadding(
                        view.getPaddingLeft(),
                        systemBars.top,
                        view.getPaddingRight(),
                        view.getPaddingBottom()
                );
            }

            // Apply bottom inset to footer
            if (footerView != null) {
                footerView.setPadding(
                        footerView.getPaddingLeft(),
                        footerView.getPaddingTop(),
                        footerView.getPaddingRight(),
                        systemBars.bottom + footerView.getPaddingTop() // Add nav bar height
                );
            }

            return windowInsets;
        });
    }

    /**
     * Simplified version for screens with only a header (no footer).
     */
    public static void setupEdgeToEdge(AppCompatActivity activity,
                                        View rootView,
                                        View headerView) {
        setupEdgeToEdge(activity, rootView, headerView, null);
    }

    /**
     * For scrollable screens: apply top inset to header, bottom inset to the scroll view's
     * bottom padding so last content isn't hidden behind navigation bar.
     */
    public static void setupEdgeToEdgeWithScroll(AppCompatActivity activity,
                                                  View rootView,
                                                  View headerView,
                                                  View scrollView,
                                                  View footerView) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Top inset to header
            if (headerView != null) {
                headerView.setPadding(
                        headerView.getPaddingLeft(),
                        systemBars.top,
                        headerView.getPaddingRight(),
                        headerView.getPaddingBottom()
                );
            }

            // Bottom inset to footer if exists
            if (footerView != null) {
                footerView.setPadding(
                        footerView.getPaddingLeft(),
                        footerView.getPaddingTop(),
                        footerView.getPaddingRight(),
                        systemBars.bottom
                );
            } else if (scrollView != null) {
                // If no footer, add bottom padding to scroll view for nav bar clearance
                scrollView.setPadding(
                        scrollView.getPaddingLeft(),
                        scrollView.getPaddingTop(),
                        scrollView.getPaddingRight(),
                        systemBars.bottom
                );
            }

            return windowInsets;
        });
    }
}
