package com.example.motovista_deep.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class TouchEffects {

    /**
     * Applies a scale-down effect when pressed and scale-up when released.
     * @param view The view to apply the effect to.
     */
    public static void applyClickEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    break;
            }
            return false;
        });
    }

    /**
     * Applies a slight lift effect (elevation increase) when touched.
     * @param view The view to apply the effect to.
     */
    public static void applyLiftEffect(View view) {
        final float originalElevation = view.getElevation();
        final float targetElevation = originalElevation + 8f;

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .translationZ(targetElevation)
                            .setDuration(100)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .translationZ(originalElevation)
                            .setDuration(100)
                            .start();
                    break;
            }
            return false;
        });
    }
}
