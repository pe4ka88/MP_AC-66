package com.example.geoezepchukac66.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SimpleColumnChartView extends View {

    private float[] values = new float[]{100, 80, 60, 120}; // Пример месяцев
    private int[] colors = new int[]{0xFF4CAF50, 0xFF2196F3, 0xFFFFC107, 0xFFE91E63};
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SimpleColumnChartView(Context context) {
        super(context);
    }

    public SimpleColumnChartView(Context context, @Nullable AttributeSet attrs) {
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
        int n = values.length;

        float max = 0;
        for (float v : values) max = Math.max(max, v);

        float barWidth = width / (n * 2f);

        for (int i = 0; i < n; i++) {
            paint.setColor(colors[i % colors.length]);
            float barHeight = (values[i] / max) * (height - 20);
            float left = i * 2 * barWidth + barWidth / 2;
            float top = height - barHeight;
            float right = left + barWidth;
            float bottom = height;
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }
}