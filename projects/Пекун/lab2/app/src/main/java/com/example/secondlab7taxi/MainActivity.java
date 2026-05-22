package com.example.secondlab7taxi;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Главный Activity приложения (точка входа по умолчанию).
 *
 * Автор: Пекун Марк Сергеевич
 * Группа: АС-66
 * Лабораторная работа №7
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Метод onCreate() вызывается при создании Activity.
     * Настраивает интерфейс и отступы для системных элементов.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Включаем режим "от края до края" для современного внешнего вида
        EdgeToEdge.enable(this);
        
        // Устанавливаем макет интерфейса
        setContentView(R.layout.activity_main);
        
        // Настройка отступов для системных элементов (статус-бар, навигационная панель)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}