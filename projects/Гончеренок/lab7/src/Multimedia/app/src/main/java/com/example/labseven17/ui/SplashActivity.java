package com.example.labseven17.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labseven17.MainActivity;
import com.example.labseven17.R;
import com.example.labseven17.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 2400L;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animateViews();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_DURATION_MS);
    }

    private void animateViews() {
        binding.ivSplashArt.setAlpha(0f);
        binding.ivSplashArt.setScaleX(0.9f);
        binding.ivSplashArt.setScaleY(0.9f);
        binding.tvAuthor.setAlpha(0f);

        binding.ivSplashArt.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .start();
        binding.tvAuthor.animate().alpha(1f).setDuration(700).setStartDelay(380).start();

        binding.progressIndicator.setScaleX(0.86f);
        binding.progressIndicator.setScaleY(0.86f);
        binding.progressIndicator.animate().scaleX(1f).scaleY(1f).setDuration(900).start();
    }
}
