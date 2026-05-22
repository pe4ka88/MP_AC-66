package com.example.labb7new7;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector detector;

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new ScaleGestureDetector(context, new ScaleListener());
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        setImageMatrix(matrix);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.5f, Math.min(scale, 5f));
            matrix.setScale(scale, scale, detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }
}
