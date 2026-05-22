package com.example.secondlab7taxi;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Экран загрузки (Splash Screen) приложения такси.
 * Отображается при первом запуске приложения с анимированной иконкой такси.
 * После 3 секунд автоматически переходит на экран регистрации.
 * 
 * Цель: создать приятное впечатление от запуска приложения
 * и подготовить пользователя к работе с приложением.
 * 
 * Автор: Пекун Марк Сергеевич
 * Группа: АС-66
 * Лабораторная работа №7
 */
public class SplashActivity extends AppCompatActivity {

    // Тег для логирования (не используется активно, но доступен при необходимости)
    private static final String TAG = "SplashActivity";
    
    // Длительность показа экрана загрузки в миллисекундах (3 секунды)
    private static final long SPLASH_DELAY = 3000;

    /**
     * Метод onCreate() - точка входа при создании Splash Screen.
     * Инициализирует анимацию и запускает таймер для перехода на следующий экран.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Устанавливаем макет с иконкой такси и текстом
        setContentView(R.layout.activity_splash);

        // Получаем TextView с иконкой такси для применения анимации
        TextView tvTaxiIcon = findViewById(R.id.tvTaxiIcon);
        
        // === АНИМАЦИЯ 1: Горизонтальное движение (имитация поездки) ===
        // Такси движется слева направо и обратно
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(tvTaxiIcon, "translationX", -300f, 300f);
        animatorX.setDuration(2000);                              // Длительность одного цикла: 2 секунды
        animatorX.setRepeatCount(ObjectAnimator.INFINITE);        // Бесконечное повторение
        animatorX.setRepeatMode(ObjectAnimator.REVERSE);          // Реверс: туда-обратно
        animatorX.setInterpolator(new AccelerateDecelerateInterpolator()); // Плавное ускорение/замедление
        animatorX.start();

        // === АНИМАЦИЯ 2: Вертикальное подпрыгивание ===
        // Создаёт эффект движения по неровной дороге
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(tvTaxiIcon, "translationY", 0f, -50f, 0f);
        animatorY.setDuration(1000);                              // Длительность: 1 секунда
        animatorY.setRepeatCount(ObjectAnimator.INFINITE);        // Бесконечное повторение
        animatorY.setInterpolator(new AccelerateDecelerateInterpolator()); // Плавное движение
        animatorY.start();

        // === АНИМАЦИЯ 3: Лёгкое покачивание (вращение) ===
        // Имитирует повороты на дороге
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(tvTaxiIcon, "rotation", -5f, 5f);
        rotationAnimator.setDuration(500);                        // Быстрое покачивание: 0.5 секунды
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Бесконечное повторение
        rotationAnimator.setRepeatMode(ObjectAnimator.REVERSE);   // Туда-обратно
        rotationAnimator.start();

        // === ТАЙМЕР ДЛЯ ПЕРЕХОДА НА СЛЕДУЮЩИЙ ЭКРАН ===
        // После задержки SPLASH_DELAY автоматически переходим к регистрации
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Создаём Intent для перехода на экран регистрации
            Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
            startActivity(intent);
            
            // Закрываем SplashActivity, чтобы пользователь не мог вернуться назад
            finish();
        }, SPLASH_DELAY);
    }
}

