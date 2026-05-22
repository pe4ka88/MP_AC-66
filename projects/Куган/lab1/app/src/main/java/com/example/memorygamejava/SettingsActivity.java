package com.example.memorygamejava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText playerNameEditText;
    private Spinner themeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Применяем тему сразу
        applyTheme();

        // Находим элементы
        TextView title = findViewById(R.id.titleTextView);
        playerNameEditText = findViewById(R.id.playerNameEditText);
        themeSpinner = findViewById(R.id.themeSpinner);
        Button saveButton = findViewById(R.id.saveButton);
        Button defaultButton = findViewById(R.id.defaultButton);
        Button backButton = findViewById(R.id.backButton);
        TextView developerText = findViewById(R.id.developerText);
        TextView initialsText = findViewById(R.id.initialsText);

        // Устанавливаем тексты
        title.setText("НАСТРОЙКИ");
        initialsText.setText("К.Н.");
        developerText.setText("Разработчик: Куган Н. Л.\nГруппа: АС-66\n2026 год");

        // Настраиваем Spinner
        setupThemeSpinner();

        // Загружаем сохраненные настройки
        loadSettings();

        // Обработчики кнопок
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreDefaults();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupThemeSpinner() {
        // Создаем адаптер для Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.theme_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);
    }

    private void applyTheme() {
        // Получаем сохраненную тему
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        int themeIndex = prefs.getInt("theme", 0);

        // Применяем тему к корневому View
        View rootView = getWindow().getDecorView().getRootView();
        if (rootView == null) return;

        int color;
        switch (themeIndex) {
            case 1: // светло-фиолетовый
                color = getResources().getColor(R.color.light_purple);
                break;
            case 2: // светло-голубой
                color = getResources().getColor(R.color.light_blue);
                break;
            case 3: // светло-зеленый
                color = getResources().getColor(R.color.light_green);
                break;
            case 4: // светло-серый
                color = getResources().getColor(R.color.light_gray);
                break;
            default: // белый
                color = getResources().getColor(android.R.color.white);
                break;
        }
        rootView.setBackgroundColor(color);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);

        // Имя игрока
        String playerName = prefs.getString("playerName", "Игрок");
        playerNameEditText.setText(playerName);

        // Тема
        int themeIndex = prefs.getInt("theme", 0);
        themeSpinner.setSelection(themeIndex);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences("GameSettings", MODE_PRIVATE).edit();

        // Сохраняем имя игрока
        String playerName = playerNameEditText.getText().toString().trim();
        if (playerName.isEmpty()) {
            playerName = "Игрок";
        }
        editor.putString("playerName", playerName);

        // Сохраняем выбранную тему
        int themeIndex = themeSpinner.getSelectedItemPosition();
        editor.putInt("theme", themeIndex);

        // Применяем изменения
        editor.apply();

        // Применяем новую тему
        applyTheme();

        // Показываем сообщение
        Toast.makeText(this,
                "Настройки сохранены\n" + playerName + ", тема: " +
                        themeSpinner.getSelectedItem().toString(),
                Toast.LENGTH_SHORT).show();
    }

    private void restoreDefaults() {
        // Сбрасываем к значениям по умолчанию
        playerNameEditText.setText("Игрок");
        themeSpinner.setSelection(0);

        // Сохраняем значения по умолчанию
        SharedPreferences.Editor editor = getSharedPreferences("GameSettings", MODE_PRIVATE).edit();
        editor.putString("playerName", "Игрок");
        editor.putInt("theme", 0);
        editor.apply();

        // Применяем стандартную тему
        applyTheme();

        // Показываем сообщение
        Toast.makeText(this,
                "Настройки сброшены к значениям по умолчанию",
                Toast.LENGTH_SHORT).show();
    }
}