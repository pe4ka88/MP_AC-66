package com.example.memorygamejava;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorActivity extends AppCompatActivity {

    private void applyTheme() {
        // Получаем сохраненную тему
        android.content.SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        int themeIndex = prefs.getInt("theme", 0);

        // Применяем тему к корневому View
        android.view.View rootView = getWindow().getDecorView().getRootView();
        if (rootView == null) return;

        switch (themeIndex) {
            case 1: // светло-фиолетовый
                rootView.setBackgroundColor(getResources().getColor(R.color.light_purple));
                break;
            case 2: // светло-голубой
                rootView.setBackgroundColor(getResources().getColor(R.color.light_blue));
                break;
            case 3: // светло-зеленый
                rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
                break;
            case 4: // светло-серый
                rootView.setBackgroundColor(getResources().getColor(R.color.light_gray));
                break;
            default: // белый
                rootView.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        // Применяем тему
        applyTheme();

        TextView authorInfo = findViewById(R.id.authorInfo);
        Button backButton = findViewById(R.id.backButton);

        authorInfo.setText(
                " РАЗРАБОТЧИК ПРИЛОЖЕНИЯ \n\n" +
                        "ФИО: Головкина Виктория\n" +
                        "Группа: АС-66\n" +
                        "Курс: 3\n" +
                        "Семестр: 5\n" +
                        "Дисциплина: Мобильные приложения\n" +
                        "Лабораторная работа: №1\n\n" +
                        " ПРИЛОЖЕНИЕ: Memory Game\n" +
                        " Тип: Игра на память\n" +
                        " Год разработки: 2026\n\n" +
                        " Особенности реализации:\n" +
                        " Главное меню с навигацией\n" +
                        " Выбор размера игрового поля\n" +
                        " Система рекордов\n" +
                        " API для правил использования cookie\n" +
                        " Кастомизация настроек\n" +
                        " Таймер и счетчик ходов\n" +
                        " Мотивационные сообщения"
        );

        backButton.setOnClickListener(v -> finish());
    }
}