package com.example.file.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class WaveformView extends View {

    private float[] amplitudes = new float[0];
    private int progressPercent = 0; // 0-100
    private Paint paintWave = new Paint();
    private Paint paintProgress = new Paint();

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paintWave.setStrokeWidth(4f);
        paintWave.setColor(0xFF888888); // серая волна
        paintProgress.setStrokeWidth(4f);
        paintProgress.setColor(0xFF90EE90);// закрашенный прогресс
    }

    public void setAmplitudes(float[] amps) {
        this.amplitudes = amps;
        invalidate();
    }

    public void setProgress(int percent) {
        this.progressPercent = percent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (amplitudes == null || amplitudes.length == 0) return;

        int width = getWidth();
        int height = getHeight();
        int n = amplitudes.length;

        float spacing = (float) width / n;
        float centerY = height / 2f;

        for (int i = 0; i < n; i++) {
            float amp = amplitudes[i];

            float lineHeight = amp;
            float x = i * spacing;

            float yStart = centerY - lineHeight / 2;
            float yEnd = centerY + lineHeight / 2;

            Paint paint = (i * 100 / n <= progressPercent) ? paintProgress : paintWave;

            canvas.drawLine(x, yStart, x, yEnd, paint);
        }
    }
}