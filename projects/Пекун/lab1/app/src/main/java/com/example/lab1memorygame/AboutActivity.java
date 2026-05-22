package com.example.lab1memorygame;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AboutActivity - экран информации об авторе
 * Отображает информацию о разработчике игры
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * Метод onCreate - инициализация экрана "Об авторе"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Кнопка "Назад" - возврат в меню
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}
