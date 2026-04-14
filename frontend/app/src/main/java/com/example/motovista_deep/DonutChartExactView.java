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
        invalidate(); // Redraw the view
    }

    public void startAnimation(float targetPercentage) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(0, targetPercentage);
        animator.setDuration(1200);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            this.principalPercentage = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        
        // Use 90% of available size to leave room for the stroke cap and padding
        float strokeWidth = size * 0.08f; // Thinner stroke
        principalPaint.setStrokeWidth(strokeWidth);
        interestPaint.setStrokeWidth(strokeWidth);
        
        float margin = strokeWidth / 2f + 4f; // Small margin to avoid clipping
        RectF rect = new RectF(margin, margin, size - margin, size - margin);

        // Calculate angles - Start from top (-90 degrees)
        float principalAngle = (principalPercentage / 100) * 360;
        float startAngle = -90; 

        // Draw background circle (neutral guide line)
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#33808080")); // 20% opacity gray
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(size / 2f, size / 2f, (size - strokeWidth) / 2f, bgPaint);

        // Draw interest arc (background of the donut - optional if we have bgPaint)
        // canvas.drawArc(rect, startAngle, 360, false, interestPaint);

        // Draw principal arc (active part)
        canvas.drawArc(rect, startAngle, principalAngle, false, principalPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        int finalSize = Math.min(widthSize, heightSize);
        if (finalSize == 0) finalSize = 180; // Fallback
        
        setMeasuredDimension(finalSize, finalSize);
    }
}
