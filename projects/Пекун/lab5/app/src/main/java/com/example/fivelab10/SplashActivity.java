package com.example.fivelab10;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logoMark = findViewById(R.id.logoMark);
        TextView textTitle = findViewById(R.id.textSplashTitle);
        LinearProgressIndicator progressIndicator = findViewById(R.id.splashProgress);

        logoMark.setScaleX(0.85f);
        logoMark.setScaleY(0.85f);
        logoMark.setAlpha(0f);
        textTitle.setTranslationY(24f);
        textTitle.setAlpha(0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(logoMark, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(logoMark, View.SCALE_X, 0.85f, 1f),
                ObjectAnimator.ofFloat(logoMark, View.SCALE_Y, 0.85f, 1f),
                ObjectAnimator.ofFloat(textTitle, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(textTitle, View.TRANSLATION_Y, 24f, 0f)
        );
        animatorSet.setDuration(700);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();

        progressIndicator.setIndeterminate(true);

        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
