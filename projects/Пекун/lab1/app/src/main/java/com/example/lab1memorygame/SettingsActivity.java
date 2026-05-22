package com.example.lab1memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * SettingsActivity - экран настроек игры
 * Позволяет выбрать размер поля и режим совпадений перед началом игры
 */
public class SettingsActivity extends AppCompatActivity {

    // Карточки выбора размера поля
    private MaterialCardView cardGrid4x4, cardGrid4x5, cardGrid6x6;
    
    // Карточки выбора режима игры
    private MaterialCardView cardModePairs, cardModeTriples, cardModeHybrid;
    
    // Кнопка запуска игры
    private MaterialButton btnStartGame;

    // Выбранный размер поля (по умолчанию 4x4)
    private String selectedGridSize = "4x4";
    
    // Выбранный режим игры (по умолчанию пары)
    private String selectedMatchMode = "pairs";

    /**
     * Метод onCreate - инициализация экрана настроек
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Инициализация карточек размера поля
        cardGrid4x4 = findViewById(R.id.cardGrid4x4);
        cardGrid4x5 = findViewById(R.id.cardGrid4x5);
        cardGrid6x6 = findViewById(R.id.cardGrid6x6);

        // Инициализация карточек режима игры
        cardModePairs = findViewById(R.id.cardModePairs);
        cardModeTriples = findViewById(R.id.cardModeTriples);
        cardModeHybrid = findViewById(R.id.cardModeHybrid);

        btnStartGame = findViewById(R.id.btnStartGame);

        // Устанавливаем начальный выбор (4x4, пары)
        cardGrid4x4.setChecked(true);
        cardModePairs.setChecked(true);

        // Обработчики для выбора размера поля
        cardGrid4x4.setOnClickListener(v -> selectGridSize(cardGrid4x4, "4x4"));
        cardGrid4x5.setOnClickListener(v -> selectGridSize(cardGrid4x5, "4x5"));
        cardGrid6x6.setOnClickListener(v -> selectGridSize(cardGrid6x6, "6x6"));

        // Обработчики для выбора режима игры
        cardModePairs.setOnClickListener(v -> selectMatchMode(cardModePairs, "pairs"));
        cardModeTriples.setOnClickListener(v -> selectMatchMode(cardModeTriples, "triples"));
        cardModeHybrid.setOnClickListener(v -> selectMatchMode(cardModeHybrid, "hybrid"));

        // Кнопка "Начать игру"
        btnStartGame.setOnClickListener(v -> startGame());

        // Обработка кнопки "Назад" - возврат в меню
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    /**
     * Выбор размера поля
     * @param selectedCard - выбранная карточка
     * @param size - размер поля ("4x4", "4x5", "6x6")
     */
    private void selectGridSize(MaterialCardView selectedCard, String size) {
        // Снимаем выделение со всех карточек размера
        cardGrid4x4.setChecked(false);
        cardGrid4x5.setChecked(false);
        cardGrid6x6.setChecked(false);

        // Выделяем выбранную карточку
        selectedCard.setChecked(true);
        selectedGridSize = size;
    }

    /**
     * Выбор режима игры
     * @param selectedCard - выбранная карточка
     * @param mode - режим игры ("pairs", "triples", "hybrid")
     */
    private void selectMatchMode(MaterialCardView selectedCard, String mode) {
        // Снимаем выделение со всех карточек режима
        cardModePairs.setChecked(false);
        cardModeTriples.setChecked(false);
        cardModeHybrid.setChecked(false);

        // Выделяем выбранную карточку
        selectedCard.setChecked(true);
        selectedMatchMode = mode;
    }

    /**
     * Запуск игры с выбранными настройками
     * Создаёт объект настроек и передаёт их в MainActivity
     */
    private void startGame() {
        GameSettings settings = new GameSettings();

        // Сначала устанавливаем режим совпадений
        settings.setMatchMode(selectedMatchMode);

        // Затем устанавливаем размер поля (пересчитает количество пар)
        switch (selectedGridSize) {
            case "4x4":
                settings.setGrid4x4();
                break;
            case "4x5":
                settings.setGrid4x5();
                break;
            case "6x6":
                settings.setGrid6x6();
                break;
        }

        // Запускаем игру с настройками
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra("grid_rows", settings.getGridRows());
        intent.putExtra("grid_columns", settings.getGridColumns());
        intent.putExtra("total_pairs", settings.getTotalPairs());
        intent.putExtra("match_mode", settings.getMatchMode());
        intent.putExtra("cards_to_match", settings.getCardsToMatch());
        startActivity(intent);
    }
}
