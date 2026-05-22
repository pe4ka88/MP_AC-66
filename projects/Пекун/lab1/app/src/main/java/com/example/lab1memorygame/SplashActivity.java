package com.example.lab1memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity - экран приветствия при запуске приложения
 * Отображается 3 секунды с анимацией, затем переходит в меню
 */
public class SplashActivity extends AppCompatActivity {

    // Длительность отображения экрана загрузки (в миллисекундах)
    private static final int SPLASH_DURATION = 3000; // 3 секунды

    /**
     * Метод onCreate - инициализация экрана загрузки
     * Загружает анимацию и планирует переход в меню
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Загружаем анимацию появления с увеличением
        Animation fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        
        // Находим текстовые элементы
        TextView tvTitle = findViewById(R.id.tvSplashTitle);
        TextView tvSubtitle = findViewById(R.id.tvSplashSubtitle);
        TextView tvAuthor = findViewById(R.id.tvSplashAuthor);
        
        // Применяем анимацию ко всем элементам
        tvTitle.startAnimation(fadeInScale);
        tvSubtitle.startAnimation(fadeInScale);
        tvAuthor.startAnimation(fadeInScale);

        // Переход в главное меню после задержки
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
            startActivity(intent);
            finish(); // Закрываем SplashActivity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}
