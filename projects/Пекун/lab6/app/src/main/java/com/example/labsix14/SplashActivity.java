package com.example.labsix14;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2500L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable navigateRunnable = () -> {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        ImageView logo = findViewById(R.id.splashLogo);
        TextView title = findViewById(R.id.textSplashTitle);
        TextView subtitle = findViewById(R.id.textSplashSubtitle);

        logo.startAnimation(fadeIn);
        title.startAnimation(fadeIn);
        subtitle.startAnimation(fadeIn);

        handler.postDelayed(navigateRunnable, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(navigateRunnable);
        super.onDestroy();
    }
}
