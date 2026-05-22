package com.example.memoryezepchukac66;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private int currentBackIndex = 0;
    private final int[] cardBacks = {
            R.drawable.backcard,
            R.drawable.backcard_2,
            R.drawable.backcard_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        EditText nicknameInput = findViewById(R.id.nicknameInput);
        Spinner gridSpinner = findViewById(R.id.gridSpinner);
        Switch infiniteSwitch = findViewById(R.id.infiniteSwitch);
        Button startButton = findViewById(R.id.startGameButton);
        ImageButton infoButton = findViewById(R.id.infoButton);
        Button recordsButton = findViewById(R.id.recordsButton);
        ImageButton changeBackButton = findViewById(R.id.changeBackButton);

        // Spinner с размерами сетки от 2x2 до 6x6
        String[] grids = {
                "2 x 2", "3 x 3", "4 x 4", "4 x 6", "5 x 5", "6 x 6"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                grids
        );
        gridSpinner.setAdapter(adapter);

        // Preferences
        SharedPreferences prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        currentBackIndex = prefs.getInt("card_back_index", 0);

        // Установить текущую рубашку
        changeBackButton.setImageResource(cardBacks[currentBackIndex]);

        // Смена рубашки
        changeBackButton.setOnClickListener(v -> {
            currentBackIndex = (currentBackIndex + 1) % cardBacks.length;
            changeBackButton.setImageResource(cardBacks[currentBackIndex]);

            prefs.edit()
                    .putInt("card_back_index", currentBackIndex)
                    .apply();
        });

        // Старт игры
        startButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            if (nickname.isEmpty()) nickname = "Player";

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("nickname", nickname);
            intent.putExtra("grid", gridSpinner.getSelectedItem().toString());
            intent.putExtra("infinite", infiniteSwitch.isChecked());
            startActivity(intent);
        });

        // Рекорды
        recordsButton.setOnClickListener(v ->
                startActivity(new Intent(this, RecordsActivity.class))
        );
        infoButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("О программе")
                    .setMessage(
                            "Игра: Memory\n\n" +
                                    "Разработчик: Езепчук А.С.\n" +
                                    "Группа: АС-66\n\n" +
                                    "Необходимо реализовать интерфейс приложения для последовательного открытия пар карточек для запоминания."
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}
