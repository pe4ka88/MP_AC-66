package com.example.geoezepchukac66.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SimplePieChartView extends View {

    private float[] values = new float[]{60, 40}; // Пешком, Транспорт
    private int[] colors = new int[]{0xFF4CAF50, 0xFF2196F3}; // Зеленый, Синий
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SimplePieChartView(Context context) {
        super(context);
    }

    public SimplePieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setValues(float[] values) {
        this.values = values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;

        float startAngle = 0f;

        for (int i = 0; i < values.length; i++) {
            paint.setColor(colors[i]);
            float sweepAngle = values[i] / sum(values) * 360f;
            canvas.drawArc(width/2 - radius, height/2 - radius,
                    width/2 + radius, height/2 + radius,
                    startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }

    private float sum(float[] arr) {
        float s = 0;
        for (float v : arr) s += v;
        return s;
    }
}