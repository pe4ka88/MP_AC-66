package com.example.lab1memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity - основной экран игры "Найди пару"
 * Реализует игровую логику для трёх режимов: пары, тройки и гибридный режим
 * Управляет игровым полем, таймером, подсчётом ходов и проверкой совпадений
 */
public class MainActivity extends AppCompatActivity {

    // Элементы интерфейса
    private GridLayout gridLayout;              // Сетка карточек
    private Button btnRestart;                  // Кнопка "Начать заново"
    private Button btnExit;                     // Кнопка "Выход"
    private TextView tvPairsLeft;               // Счётчик оставшихся пар
    private TextView tvTimer;                   // Таймер игры
    private TextView tvMoves;                   // Счётчик ходов
    private TextView tvModeIndicator;           // Индикатор режима игры

    // Игровые данные
    private List<Card> cards;                   // Список всех карточек на поле
    private List<Card> selectedCards = new ArrayList<>();  // Текущие открытые карточки
    private int pairsFound = 0;                 // Количество найденных пар/троек
    private int totalPairs;                     // Общее количество пар/троек
    private int movesCount = 0;                 // Количество ходов
    private boolean isProcessing = false;       // Флаг блокировки нажатий

    // Таймер
    private long startTime;                     // Время начала игры
    private Handler timerHandler = new Handler(); // Обработчик таймера
    private Runnable timerRunnable;             // Задача обновления таймера

    // Настройки игры
    private int gridRows;                       // Количество строк
    private int gridColumns;                    // Количество столбцов
    private String matchMode;                   // Режим игры ("pairs", "triples", "hybrid")
    private int currentCardsToMatch;            // Количество карточек для совпадения

    // Статистика
    private GameStatistics statistics;

    // Массив изображений фруктов для карточек
    private int[] cardImages = {
            R.drawable.ic_apple,        // Яблоко
            R.drawable.ic_banana,       // Банан
            R.drawable.ic_cherry,       // Вишня
            R.drawable.ic_grapes,       // Виноград
            R.drawable.ic_orange,       // Апельсин
            R.drawable.ic_pear,         // Груша
            R.drawable.ic_strawberry,   // Клубника
            R.drawable.ic_watermelon,   // Арбуз
            R.drawable.ic_kiwi,         // Киви
            R.drawable.ic_pineapple,    // Ананас
            R.drawable.ic_mango,        // Манго
            R.drawable.ic_peach,        // Персик
            R.drawable.ic_lemon,        // Лимон
            R.drawable.ic_coconut,      // Кокос
            R.drawable.ic_avocado,      // Авокадо
            R.drawable.ic_plum,         // Слива
            R.drawable.ic_papaya,       // Папайя
            R.drawable.ic_pomegranate   // Гранат
    };

    /**
     * Метод onCreate - инициализация игрового экрана
     * Получает настройки из Intent и запускает игру
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statistics = new GameStatistics(this);

        // Получаем настройки игры из Intent
        Intent intent = getIntent();
        gridRows = intent.getIntExtra("grid_rows", 4);
        gridColumns = intent.getIntExtra("grid_columns", 4);
        totalPairs = intent.getIntExtra("total_pairs", 8);
        matchMode = intent.getStringExtra("match_mode");
        currentCardsToMatch = intent.getIntExtra("cards_to_match", 2);

        if (matchMode == null) {
            matchMode = "pairs";
        }

        // Инициализация элементов интерфейса
        gridLayout = findViewById(R.id.gridLayout);
        btnRestart = findViewById(R.id.btnRestart);
        btnExit = findViewById(R.id.btnExit);
        tvPairsLeft = findViewById(R.id.tvPairsLeft);
        tvTimer = findViewById(R.id.tvTimer);
        tvMoves = findViewById(R.id.tvMoves);
        tvModeIndicator = findViewById(R.id.tvModeIndicator);

        // Обработчики кнопок
        btnRestart.setOnClickListener(v -> showRestartConfirmation());
        btnExit.setOnClickListener(v -> showExitConfirmation());

        // Обработка кнопки "Назад" - показываем диалог выхода
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        });

        setupTimer();
        initializeGame();
    }

    /**
     * Настройка таймера игры
     * Обновляет отображение времени каждую секунду
     */
    private void setupTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
    }

    /**
     * Инициализация игры
     * Создаёт карточки, перемешивает их и создаёт UI
     */
    private void initializeGame() {
        cards = new ArrayList<>();
        pairsFound = 0;
        movesCount = 0;
        selectedCards.clear();
        isProcessing = false;

        // Создаем карточки в зависимости от выбранного режима
        if ("triples".equals(matchMode)) {
            // Режим троек: создаём по 3 одинаковые карточки
            for (int i = 0; i < totalPairs; i++) {
                cards.add(new Card(i, cardImages[i % cardImages.length]));
                cards.add(new Card(i, cardImages[i % cardImages.length]));
                cards.add(new Card(i, cardImages[i % cardImages.length]));
            }
        } else if ("hybrid".equals(matchMode)) {
            // Гибридный режим: создаём смесь пар и троек
            int totalCells = gridRows * gridColumns;
            
            // Подбираем оптимальную комбинацию пар и троек
            // Формула: pairsCount * 2 + triplesCount * 3 = totalCells
            int triplesCount = 0;
            int pairsCount = 0;
            
            int maxTriples = totalCells / 3;
            
            // Пробуем разные комбинации, стремясь к балансу
            for (int t = maxTriples / 2; t <= maxTriples; t++) {
                int remaining = totalCells - (t * 3);
                if (remaining >= 0 && remaining % 2 == 0) {
                    triplesCount = t;
                    pairsCount = remaining / 2;
                    break;
                }
            }
            
            // Если не нашли решение, ищем с меньшим количеством троек
            if (triplesCount == 0 && pairsCount == 0) {
                for (int t = 0; t <= maxTriples; t++) {
                    int remaining = totalCells - (t * 3);
                    if (remaining >= 0 && remaining % 2 == 0) {
                        triplesCount = t;
                        pairsCount = remaining / 2;
                        break;
                    }
                }
            }
            
            // Создаём пары (id от 0 до pairsCount-1)
            for (int i = 0; i < pairsCount; i++) {
                cards.add(new Card(i, cardImages[i % cardImages.length]));
                cards.add(new Card(i, cardImages[i % cardImages.length]));
            }
            
            // Создаём тройки (id от pairsCount до pairsCount+triplesCount-1)
            for (int i = pairsCount; i < pairsCount + triplesCount; i++) {
                cards.add(new Card(i, cardImages[i % cardImages.length]));
                cards.add(new Card(i, cardImages[i % cardImages.length]));
                cards.add(new Card(i, cardImages[i % cardImages.length]));
            }
        } else {
            // Режим пар: создаём по 2 одинаковые карточки
            for (int i = 0; i < totalPairs; i++) {
                cards.add(new Card(i, cardImages[i % cardImages.length]));
                cards.add(new Card(i, cardImages[i % cardImages.length]));
            }
        }

        // Перемешиваем карточки
        Collections.shuffle(cards);

        // Создаем UI - сетку карточек
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(gridColumns);
        gridLayout.setRowCount(gridRows);

        // Создаём каждую карточку
        for (int i = 0; i < cards.size(); i++) {
            final int position = i;
            CardView cardContainer = new CardView(this);
            
            // Настройка параметров расположения карточки
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            
            cardContainer.setLayoutParams(params);
            cardContainer.setCardElevation(12);
            cardContainer.setRadius(20);
            cardContainer.setCardBackgroundColor(getResources().getColor(R.color.card_back));

            // Создаём изображение карточки
            ImageView cardView = new ImageView(this);
            cardView.setLayoutParams(new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.MATCH_PARENT
            ));
            cardView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            cardView.setImageResource(R.drawable.card_back);
            cardView.setPadding(20, 20, 20, 20);

            cardContainer.addView(cardView);
            cardContainer.setOnClickListener(v -> onCardClicked(position, cardContainer, cardView));

            gridLayout.addView(cardContainer);
        }

        updateGameInfo();
        updateModeIndicator();

        // Запускаем таймер
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /**
     * Обработчик нажатия на карточку
     * @param position - позиция карточки в массиве
     * @param cardContainer - контейнер карточки
     * @param cardView - изображение карточки
     */
    private void onCardClicked(int position, CardView cardContainer, ImageView cardView) {
        // Блокируем нажатия во время обработки
        if (isProcessing) return;

        Card clickedCard = cards.get(position);

        // Игнорируем уже открытые или найденные карточки
        if (clickedCard.isFaceUp() || clickedCard.isMatched()) return;

        // Анимация переворота карточки
        animateCardFlip(cardView, cardContainer, clickedCard, true);

        clickedCard.setFaceUp(true);
        selectedCards.add(clickedCard);

        // Выбираем логику обработки в зависимости от режима
        if ("hybrid".equals(matchMode)) {
            handleHybridMode();
        } else {
            handleStandardMode();
        }
    }

    /**
     * Логика гибридного режима
     * Обрабатывает смесь пар и троек карточек
     */
    private void handleHybridMode() {
        if (selectedCards.size() == 1) {
            // Открыта первая карточка — ждём вторую
            return;
        }

        if (selectedCards.size() == 2) {
            // Открыты 2 карточки — проверяем совпадение
            Card first = selectedCards.get(0);
            Card second = selectedCards.get(1);

            if (first.getId() == second.getId()) {
                // Совпали! Проверяем: это пара или тройка?
                int totalCardsWithId = countCardsWithId(first.getId());

                if (totalCardsWithId == 2) {
                    // Это пара — завершаем ход
                    isProcessing = true;
                    movesCount++;
                    tvMoves.setText(String.valueOf(movesCount));
                    handleMatchFound();
                } else {
                    // Это тройка — ждём третью карточку
                    updateModeIndicator("Открой третью карточку!");
                }
            } else {
                // Не совпали — разрешаем открыть третью карточку
            }
            return;
        }

        if (selectedCards.size() == 3) {
            // Открыты 3 карточки — финальная проверка
            isProcessing = true;
            movesCount++;
            tvMoves.setText(String.valueOf(movesCount));

            boolean allMatch = true;
            int firstId = selectedCards.get(0).getId();
            for (Card card : selectedCards) {
                if (card.getId() != firstId) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                handleMatchFound();
            } else {
                handleMatchFailed();
            }
        }
    }

    /**
     * Стандартная логика для режимов пар и троек
     */
    private void handleStandardMode() {
        int requiredCards = "triples".equals(matchMode) ? 3 : 2;

        if (selectedCards.size() == requiredCards) {
            isProcessing = true;
            movesCount++;
            tvMoves.setText(String.valueOf(movesCount));

            // Проверяем, все ли карточки совпадают
            boolean allMatch = true;
            int firstId = selectedCards.get(0).getId();
            for (Card card : selectedCards) {
                if (card.getId() != firstId) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                handleMatchFound();
            } else {
                handleMatchFailed();
            }
        }
    }

    /**
     * Подсчитывает количество карточек с данным ID на поле
     * @param id - ID карточки
     * @return количество незакрытых карточек с этим ID
     */
    private int countCardsWithId(int id) {
        int count = 0;
        for (Card card : cards) {
            if (card.getId() == id && !card.isMatched()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Обработка успешного совпадения карточек
     * Отмечает карточки как найденные и проверяет победу
     */
    private void handleMatchFound() {
        new Handler().postDelayed(() -> {
            for (Card card : selectedCards) {
                card.setMatched(true);
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i) == card) {
                        CardView container = (CardView) gridLayout.getChildAt(i);
                        animateMatchedCard(container);
                    }
                }
            }
            pairsFound++;
            updateGameInfo();
            selectedCards.clear();
            isProcessing = false;
            updateModeIndicator();

            // Проверяем победу
            if (pairsFound == totalPairs) {
                timerHandler.removeCallbacks(timerRunnable);
                showVictoryDialog();
            }
        }, 500);
    }

    /**
     * Обработка неудачной попытки
     * Закрывает открытые карточки обратно
     */
    private void handleMatchFailed() {
        new Handler().postDelayed(() -> {
            for (Card card : selectedCards) {
                card.setFaceUp(false);
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i) == card) {
                        CardView container = (CardView) gridLayout.getChildAt(i);
                        ImageView image = (ImageView) container.getChildAt(0);
                        animateCardFlip(image, container, card, false);
                    }
                }
            }
            selectedCards.clear();
            isProcessing = false;
            updateModeIndicator();
        }, 1000);
    }

    /**
     * Обновляет индикатор режима игры
     */
    private void updateModeIndicator() {
        updateModeIndicator(null);
    }

    /**
     * Обновляет индикатор режима игры с пользовательским сообщением
     * @param customMessage - сообщение для отображения
     */
    private void updateModeIndicator(String customMessage) {
        if (tvModeIndicator == null) return;
        
        if ("hybrid".equals(matchMode)) {
            if (customMessage != null) {
                tvModeIndicator.setText(customMessage);
            } else {
                tvModeIndicator.setText("Гибридный режим: пары и тройки");
            }
            tvModeIndicator.setVisibility(android.view.View.VISIBLE);
        } else {
            tvModeIndicator.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * Анимация переворота карточки
     * @param imageView - изображение карточки
     * @param container - контейнер карточки
     * @param card - объект карточки
     * @param faceUp - перевернуть лицом вверх или вниз
     */
    private void animateCardFlip(ImageView imageView, CardView container, Card card, boolean faceUp) {
        imageView.animate()
                .scaleX(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    if (faceUp) {
                        imageView.setImageResource(card.getImageResId());
                        container.setCardBackgroundColor(getResources().getColor(R.color.card_front));
                    } else {
                        imageView.setImageResource(R.drawable.card_back);
                        container.setCardBackgroundColor(getResources().getColor(R.color.card_back));
                    }
                    imageView.setScaleX(0f);
                    imageView.animate()
                            .scaleX(1f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }

    /**
     * Анимация найденной карточки
     * Делает карточку полупрозрачной и слегка уменьшает
     * @param container - контейнер карточки
     */
    private void animateMatchedCard(CardView container) {
        container.animate()
                .alpha(0.4f)
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(300)
                .withEndAction(() -> {
                    container.setCardBackgroundColor(getResources().getColor(R.color.card_matched));
                })
                .start();
    }

    /**
     * Обновляет информацию об игре (оставшиеся пары)
     */
    private void updateGameInfo() {
        tvPairsLeft.setText(String.valueOf(totalPairs - pairsFound));
    }

    /**
     * Показывает диалог победы
     * Предлагает вернуться в меню или играть снова
     */
    private void showVictoryDialog() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);

        // Сохраняем результат игры в статистику
        statistics.saveGameResult(seconds, matchMode, movesCount, gridRows, gridColumns);

        String time = statistics.formatTime(seconds);
        String modeName = getModeName(matchMode);

        String message = String.format(Locale.getDefault(),
                getString(R.string.victory_message), 
                time, movesCount, modeName, gridRows, gridColumns);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.victory_title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_main_menu, (dialog, which) -> {
                    finish(); // Возврат в главное меню
                })
                .setNeutralButton(R.string.btn_play_again, (dialog, which) -> {
                    restartGame(); // Играть снова
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Получает локализованное название режима игры
     * @param mode - режим игры
     * @return название режима
     */
    private String getModeName(String mode) {
        switch (mode) {
            case "triples":
                return getString(R.string.mode_name_triples);
            case "hybrid":
                return getString(R.string.mode_name_hybrid);
            default:
                return getString(R.string.mode_name_pairs);
        }
    }

    /**
     * Показывает диалог подтверждения перезапуска игры
     */
    private void showRestartConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.restart_game)
                .setMessage(R.string.exit_confirm_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> restartGame())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Показывает диалог подтверждения выхода из игры
     */
    private void showExitConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.exit_confirm_title)
                .setMessage(R.string.exit_confirm_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    timerHandler.removeCallbacks(timerRunnable);
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Перезапускает игру с текущими настройками
     */
    private void restartGame() {
        timerHandler.removeCallbacks(timerRunnable);
        initializeGame();
    }

    /**
     * Метод onDestroy - очистка ресурсов при закрытии Activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}