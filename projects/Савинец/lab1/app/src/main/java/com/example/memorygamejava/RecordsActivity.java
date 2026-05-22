package com.example.memorygamejava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecordsActivity extends AppCompatActivity {

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

    private TextView records2x2;
    private TextView records3x3;
    private TextView records4x4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        // Применяем тему
        applyTheme();

        TextView recordsTitle = findViewById(R.id.recordsTitle);
        records2x2 = findViewById(R.id.records2x2);
        records3x3 = findViewById(R.id.records3x3);
        records4x4 = findViewById(R.id.records4x4);
        TextView developerInfo = findViewById(R.id.developerInfo);
        Button backButton = findViewById(R.id.backButton);

        // Заголовок с номером ЛР
        recordsTitle.setText(" ТАБЛИЦА РЕКОРДОВ \nЛабораторная работа №1");

        // Загружаем рекорды
        loadRecords();

        // Информация о разработчике
        developerInfo.setText("Разработчик приложения:\nСавинец М. Д.\nГруппа АС-66\n2026 год");

        // Обработчик кнопки "Назад"
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadRecords() {
        SharedPreferences prefs = getSharedPreferences("GameRecords", MODE_PRIVATE);

        // Рекорды для поля 2x2
        String record2x2Str = prefs.getString("record_2x2", "Нет рекордов");
        records2x2.setText(formatRecord("Поле 2x2", record2x2Str));

        // Рекорды для поля 3x3
        String record3x3Str = prefs.getString("record_3x3", "Нет рекордов");
        records3x3.setText(formatRecord("Поле 3x3", record3x3Str));

        // Рекорды для поля 4x4
        String record4x4Str = prefs.getString("record_4x4", "Нет рекордов");
        records4x4.setText(formatRecord("Поле 4x4", record4x4Str));
    }

    private String formatRecord(String fieldSize, String record) {
        if (record.equals("Нет рекордов")) {
            return fieldSize + ":\n" + record + "\n";
        }

        String[] parts = record.split(":");
        if (parts.length >= 3) {
            return fieldSize + ":\n" +
                    "Игрок: " + parts[0] + "\n" +
                    "Время: " + formatTime(Integer.parseInt(parts[1])) + "\n" +
                    "Ходов: " + parts[2] + "\n";
        }
        return fieldSize + ":\n" + record + "\n";
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}