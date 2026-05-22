package com.example.memorygamejava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseSizeActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_choose_size);
        // Применяем тему
        applyTheme();

        TextView title = findViewById(R.id.titleTextView);
        RadioGroup sizeRadioGroup = findViewById(R.id.sizeRadioGroup);
        Button startButton = findViewById(R.id.startButton);
        Button backButton = findViewById(R.id.backButton);
        TextView developerText = findViewById(R.id.developerText);
        TextView initialsText = findViewById(R.id.initialsText);

        title.setText("ВЫБЕРИТЕ РАЗМЕР ПОЛЯ");
        initialsText.setText("М.С.");
        developerText.setText("Разработчик: Савинец М. Д.\nГруппа: АС-66\n2026 год");

        // Устанавливаем выбор по умолчанию
        sizeRadioGroup.check(R.id.radio4x4);

        startButton.setOnClickListener(v -> {
            int selectedId = sizeRadioGroup.getCheckedRadioButtonId();
            int fieldSize = 4;

            if (selectedId == R.id.radio2x2) {
                fieldSize = 2;
            } else if (selectedId == R.id.radio3x3) {
                fieldSize = 3;
            }

            // Сохраняем выбор
            SharedPreferences.Editor editor = getSharedPreferences("GameSettings", MODE_PRIVATE).edit();
            editor.putInt("fieldSize", fieldSize);
            editor.apply();

            // Запускаем игру
            Intent intent = new Intent(ChooseSizeActivity.this, GameActivity.class);
            intent.putExtra("FIELD_SIZE", fieldSize);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());
    }
}