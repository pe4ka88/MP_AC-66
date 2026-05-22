package com.example.lab1memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

/**
 * MenuActivity - главное меню приложения
 * Предоставляет доступ к игре, статистике и информации об авторе
 */
public class MenuActivity extends AppCompatActivity {

    /**
     * Метод onCreate - инициализация главного меню
     * Настраивает кнопки навигации
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Находим кнопки меню
        MaterialCardView btnStartGame = findViewById(R.id.btnStartGame);
        MaterialCardView btnStatistics = findViewById(R.id.btnStatistics);
        MaterialCardView btnAbout = findViewById(R.id.btnAbout);

        // Кнопка "Начать игру" - переход к настройкам
        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Кнопка "Статистика" - переход к экрану статистики
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        // Кнопка "Об авторе" - переход к информации об авторе
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Обработка кнопки "Назад" - выход из приложения
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity(); // Закрываем все Activity и выходим
            }
        });
    }
}
