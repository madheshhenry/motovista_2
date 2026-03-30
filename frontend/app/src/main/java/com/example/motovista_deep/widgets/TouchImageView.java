package com.example.motovista_deep.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class TouchImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private PointF start = new PointF();
    private float[] m = new float[9];
    private float minScale = 1f;
    private float maxScale = 4f;
    private float saveScale = 1f;
    private float origWidth, origHeight;
    private int viewWidth, viewHeight;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    public TouchImageView(Context context) {
        super(context);
        init(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        super.setScaleType(ImageView.ScaleType.MATRIX);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (origWidth == 0 || origHeight == 0) {
            resetImage();
        }
    }

    private void resetImage() {
        if (getDrawable() == null) return;
        matrix.reset();
        float scale;
        float[] m = new float[9];
        matrix.getValues(m);

        origWidth = getDrawable().getIntrinsicWidth();
        origHeight = getDrawable().getIntrinsicHeight();

        if (origWidth > viewWidth || origHeight > viewHeight) {
            scale = Math.min((float)viewWidth / origWidth, (float)viewHeight / origHeight);
            matrix.postScale(scale, scale);
            saveScale = 1f;
        }

        centerImage();
        setImageMatrix(matrix);
    }

    private void centerImage() {
        float[] m = new float[9];
        matrix.getValues(m);

        float imageWidth = origWidth * saveScale;
        float imageHeight = origHeight * saveScale;

        float transX = 0, transY = 0;

        if (imageWidth < viewWidth) {
            transX = (viewWidth - imageWidth) / 2;
        } else {
            transX = Math.max(Math.min(m[Matrix.MTRANS_X], 0), viewWidth - imageWidth);
        }

        if (imageHeight < viewHeight) {
            transY = (viewHeight - imageHeight) / 2;
        } else {
            transY = Math.max(Math.min(m[Matrix.MTRANS_Y], 0), viewHeight - imageHeight);
        }

        matrix.postTranslate(transX - m[Matrix.MTRANS_X], transY - m[Matrix.MTRANS_Y]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start.set(curr);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mScaleDetector.isInProgress()) {
                    float dx = curr.x - start.x;
                    float dy = curr.y - start.y;

                    float[] m = new float[9];
                    matrix.getValues(m);
                    float transX = m[Matrix.MTRANS_X];
                    float transY = m[Matrix.MTRANS_Y];
                    
                    float imageWidth = origWidth * saveScale;
                    float imageHeight = origHeight * saveScale;

                    float fixTransX = getFixDragTrans(dx, viewWidth, imageWidth, transX);
                    float fixTransY = getFixDragTrans(dy, viewHeight, imageHeight, transY);

                    if (fixTransX != 0 || fixTransY != 0) {
                        matrix.postTranslate(fixTransX, fixTransY);
                        setImageMatrix(matrix);
                    }
                    
                    start.set(curr.x, curr.y);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }

        return true;
    }

    private float getFixDragTrans(float delta, float viewSize, float contentSize, float currentTrans) {
        if (contentSize <= viewSize) {
            return 0;
        }
        // contentSize > viewSize
        float minTrans = viewSize - contentSize;
        float maxTrans = 0;

        if (currentTrans + delta > maxTrans) {
            return maxTrans - currentTrans; // Overshoot top/left
        }
        if (currentTrans + delta < minTrans) {
            return minTrans - currentTrans; // Overshoot bottom/right
        }
        return delta;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float origScale = saveScale;
            saveScale *= scaleFactor;

            if (saveScale > maxScale) {
                saveScale = maxScale;
                scaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                scaleFactor = minScale / origScale;
            }

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
                matrix.postScale(scaleFactor, scaleFactor, viewWidth / 2, viewHeight / 2);
            } else {
                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            }

            centerImage();
            setImageMatrix(matrix); // Apply changes immediately
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (saveScale == minScale) {
                saveScale = maxScale;
                matrix.postScale(maxScale / minScale, maxScale / minScale, e.getX(), e.getY());
            } else {
                saveScale = minScale;
                matrix.postScale(minScale / saveScale, minScale / saveScale, e.getX(), e.getY());
            }
            centerImage();
            setImageMatrix(matrix); // Apply changes immediately
            return true;
        }
    }
}