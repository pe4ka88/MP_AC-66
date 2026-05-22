package com.example.memorygamejava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        // Применяем тему
        applyTheme();

        // Находим элементы
        TextView title = findViewById(R.id.titleTextView);
        Button startButton = findViewById(R.id.startButton);
        Button recordsButton = findViewById(R.id.recordsButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button cookieButton = findViewById(R.id.cookieButton);
        Button taskButton = findViewById(R.id.taskButton);
        Button authorButton = findViewById(R.id.authorButton);
        Button exitButton = findViewById(R.id.exitButton);
        TextView developerText = findViewById(R.id.developerText);
        TextView initialsText = findViewById(R.id.initialsText);

        // Заголовок
        title.setText("MEMORY GAME");

        // Инициалы в правом верхнем углу
        initialsText.setText("М.С.");

        // Текст разработчика внизу
        developerText.setText("Разработчик: Савинец М. Д.\nГруппа: АС-66\n2026 год");

        // Обработчики кнопок
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ChooseSizeActivity.class);
            startActivity(intent);
        });

        recordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, RecordsActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        cookieButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CookiePolicyActivity.class);
            startActivity(intent);
        });

        taskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, TaskActivity.class);
            startActivity(intent);
        });

        authorButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, AuthorActivity.class);
            startActivity(intent);
        });

        exitButton.setOnClickListener(v -> finishAffinity());
    }
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
}