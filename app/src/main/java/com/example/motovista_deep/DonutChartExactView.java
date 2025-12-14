package com.example.motovista_deep;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DonutChartExactView extends View {

    private Paint principalPaint;
    private Paint interestPaint;
    private Paint centerPaint;

    private float principalPercentage = 87.4f; // From HTML: --principal-percentage: 87.4%
    private float interestPercentage;

    public DonutChartExactView(Context context) {
        super(context);
        init();
    }

    public DonutChartExactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DonutChartExactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        principalPercentage = 87.4f;
        interestPercentage = 12.6f; // 100 - 87.4
        // Principal color: #13c8ec (from HTML)
        principalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        principalPaint.setColor(Color.parseColor("#13c8ec"));
        principalPaint.setStyle(Paint.Style.STROKE);
        principalPaint.setStrokeWidth(16f);
        principalPaint.setStrokeCap(Paint.Cap.ROUND);

        // Interest color: #13c8ec4d (from HTML - 30% opacity)
        interestPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        interestPaint.setColor(Color.parseColor("#4D13C8EC"));
        interestPaint.setStyle(Paint.Style.STROKE);
        interestPaint.setStrokeWidth(16f);
        interestPaint.setStrokeCap(Paint.Cap.ROUND);

        // Center circle - white (from HTML: background-color: white)
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.WHITE);
        centerPaint.setStyle(Paint.Style.FILL);
    }

    public void setPrincipalPercentage(float percentage) {
        this.principalPercentage = percentage;
        this.interestPercentage = 100 - percentage;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        // From HTML: width: 160px, height: 160px
        int totalSize = 160;
        int padding = (size - totalSize) / 2;

        if (padding < 0) padding = 0;

        // Calculate the rectangle for the donut (matching HTML dimensions)
        RectF rect = new RectF(padding, padding, size - padding, size - padding);

        // Adjust for exact 160px size
        if (size > totalSize) {
            int diff = (size - totalSize) / 2;
            rect.set(diff, diff, size - diff, size - diff);
        }

        // Calculate angles - Start from top (-90 degrees)
        float principalAngle = (principalPercentage / 100) * 360;
        float startAngle = -90; // Start from top like HTML conic-gradient

        // Draw interest arc first (background)
        canvas.drawArc(rect, startAngle, 360, false, interestPaint);

        // Draw principal arc on top
        canvas.drawArc(rect, startAngle, principalAngle, false, principalPaint);

        // Draw center circle - 80% of size (from HTML: width: 80%, height: 80%)
        float centerRadius = (size * 0.8f) / 2;
        float centerX = size / 2f;
        float centerY = size / 2f;
        canvas.drawCircle(centerX, centerY, centerRadius, centerPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // From HTML: width: 160px, height: 160px
        int desiredSize = 160;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        // Measure width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredSize, widthSize);
        } else {
            width = desiredSize;
        }

        // Measure height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredSize, heightSize);
        } else {
            height = desiredSize;
        }

        // Make it square like HTML
        int finalSize = Math.min(width, height);
        setMeasuredDimension(finalSize, finalSize);
    }
}