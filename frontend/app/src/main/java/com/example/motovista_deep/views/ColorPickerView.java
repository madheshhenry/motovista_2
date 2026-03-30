package com.example.motovista_deep.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {

    private Paint huePaint;
    private Paint satValPaint;
    private Paint cursorPaint;

    private int[] hueColors = {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED};
    private float currentHue = 0f;
    private float currentSat = 1f;
    private float currentVal = 1f;

    private RectF hueRect;
    private RectF satValRect;

    private OnColorChangedListener listener;

    public interface OnColorChangedListener {
        void onColorChanged(int color, String hex);
    }

    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        huePaint = new Paint();
        satValPaint = new Paint();
        cursorPaint = new Paint();
        cursorPaint.setStyle(Paint.Style.STROKE);
        cursorPaint.setColor(Color.WHITE);
        cursorPaint.setStrokeWidth(4);
        cursorPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Layout: Sat/Val Box takes 80% width, Hue Bar takes 15% width on right
        float hueWidth = w * 0.15f;
        float gap = w * 0.05f;
        float satValWidth = w - hueWidth - gap;

        satValRect = new RectF(0, 0, satValWidth, h);
        hueRect = new RectF(satValWidth + gap, 0, w, h);

        Shader hueShader = new LinearGradient(0, 0, 0, h, hueColors, null, Shader.TileMode.CLAMP);
        huePaint.setShader(hueShader);

        updateSatValShader();
    }

    private void updateSatValShader() {
        // Compose Shader: Horizontal White -> Color(Hue), Vertical Transparent -> Black
        // But Android ComposeShader is complex. 
        // Standard way: 
        // 1. Horizontal LinearGradient (White -> HueColor)
        // 2. Vertical LinearGradient (Transparent -> Black)
        // Draw both.
        
        int hueColor = Color.HSVToColor(new float[]{currentHue, 1f, 1f});
        
        Shader valShader = new LinearGradient(0, 0, 0, satValRect.height(), 
                Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
        
        Shader satShader = new LinearGradient(0, 0, satValRect.width(), 0, 
                Color.WHITE, hueColor, Shader.TileMode.CLAMP);

        // We will just draw rectangle twice in onDraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw Hue Bar
        canvas.drawRect(hueRect, huePaint);

        // Draw Sat/Val Box
        // 1. Saturation Gradient (Left-White to Right-HueColor)
        int hueColor = Color.HSVToColor(new float[]{currentHue, 1f, 1f});
        Shader satShader = new LinearGradient(satValRect.left, satValRect.top, satValRect.right, satValRect.top, 
                Color.WHITE, hueColor, Shader.TileMode.CLAMP);
        satValPaint.setShader(satShader);
        canvas.drawRect(satValRect, satValPaint);

        // 2. Value Gradient (Top-Transparent to Bottom-Black)
        Shader valShader = new LinearGradient(satValRect.left, satValRect.top, satValRect.left, satValRect.bottom, 
                Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
        satValPaint.setShader(valShader);
        canvas.drawRect(satValRect, satValPaint);

        // Draw Cursors
        // Hue Cursor
        float hueY = (currentHue / 360f) * hueRect.height();
        canvas.drawRect(hueRect.left - 2, hueY - 4, hueRect.right + 2, hueY + 4, cursorPaint);

        // Sat/Val Cursor
        float satX = currentSat * satValRect.width();
        float valY = (1f - currentVal) * satValRect.height();
        canvas.drawCircle(satX, valY, 16, cursorPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean update = false;
        
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (hueRect.contains(x, y) || (x > satValRect.right && x <= getWidth())) {
                // Hue Selection
                float h = (y / hueRect.height()) * 360f;
                currentHue = Math.max(0f, Math.min(360f, h));
                update = true;
            } else if (satValRect.contains(x, y) || x < hueRect.left) {
                // Sat/Val Selection
                currentSat = x / satValRect.width();
                currentVal = 1f - (y / satValRect.height());
                
                currentSat = Math.max(0f, Math.min(1f, currentSat));
                currentVal = Math.max(0f, Math.min(1f, currentVal));
                update = true;
            }
        }

        if (update) {
            invalidate();
            if (listener != null) {
                int color = Color.HSVToColor(new float[]{currentHue, currentSat, currentVal});
                String hex = String.format("#%06X", (0xFFFFFF & color));
                listener.onColorChanged(color, hex);
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }
    
    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        currentHue = hsv[0];
        currentSat = hsv[1];
        currentVal = hsv[2];
        invalidate();
    }
}
