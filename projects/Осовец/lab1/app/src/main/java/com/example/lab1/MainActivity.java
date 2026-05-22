package com.example.lab1;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SETTINGS = 1;

    private LinearLayout mainLayout;
    private GridView gridView;
    private TextView titleTextView;
    private TextView motivationTextView;
    private TextView movesTextView;
    private TextView pairsTextView;
    private TextView pairsLabelTextView;
    private TextView timerTextView;
    private TextView progressPercentText;
    private ProgressBar gameProgressBar;
    private TextView streakTextView;
    private TextView bestTimeTextView;
    private Button restartButton;
    private Button settingsButton;
    private Button statisticsButton;
    private Button aboutButton;
    
    private MemoryGame game;
    private CardAdapter adapter;
    private Handler handler;
    private long startTime;
    private boolean isTimerRunning;
    private boolean isWaitingForClose = false;
    private GameSettings settings;
    private int currentGridRows = 4;
    private int currentGridCols = 4;
    private int currentMatchCount = 2; // 2 для пар, 3 для троек
    private boolean currentMixedMode = false; // Смешанный режим (пары + тройки)
    
    // Наборы изображений (18 штук для поддержки 6x6 поля)
    private int[] shapesImages = {
        R.drawable.card_1,
        R.drawable.card_2,
        R.drawable.card_3,
        R.drawable.card_4,
        R.drawable.card_5,
        R.drawable.card_6,
        R.drawable.card_7,
        R.drawable.card_8,
        R.drawable.card_9,
        R.drawable.card_10,
        R.drawable.card_11,
        R.drawable.card_12,
        R.drawable.card_13,
        R.drawable.card_14,
        R.drawable.card_15,
        R.drawable.card_16,
        R.drawable.card_17,
        R.drawable.card_18
    };
    
    private int[] animalsImages = {
        R.drawable.animal_1,
        R.drawable.animal_2,
        R.drawable.animal_3,
        R.drawable.animal_4,
        R.drawable.animal_5,
        R.drawable.animal_6,
        R.drawable.animal_7,
        R.drawable.animal_8,
        R.drawable.animal_9,
        R.drawable.animal_10,
        R.drawable.animal_1,  // Повторяем для больших полей
        R.drawable.animal_2,
        R.drawable.animal_3,
        R.drawable.animal_4,
        R.drawable.animal_5,
        R.drawable.animal_6,
        R.drawable.animal_7,
        R.drawable.animal_8
    };
    
    private int[] fruitsImages = {
        R.drawable.fruit_1,
        R.drawable.fruit_2,
        R.drawable.fruit_3,
        R.drawable.fruit_4,
        R.drawable.fruit_5,
        R.drawable.fruit_6,
        R.drawable.fruit_7,
        R.drawable.fruit_8,
        R.drawable.fruit_1,  // Повторяем для больших полей
        R.drawable.fruit_2,
        R.drawable.fruit_3,
        R.drawable.fruit_4,
        R.drawable.fruit_5,
        R.drawable.fruit_6,
        R.drawable.fruit_7,
        R.drawable.fruit_8
    };
    
    private int[] emojisImages = {
        R.drawable.emoji_1,
        R.drawable.emoji_2,
        R.drawable.emoji_3,
        R.drawable.emoji_4,
        R.drawable.emoji_5,
        R.drawable.emoji_6,
        R.drawable.emoji_7,
        R.drawable.emoji_8,
        R.drawable.emoji_1,  // Повторяем для больших полей
        R.drawable.emoji_2,
        R.drawable.emoji_3,
        R.drawable.emoji_4,
        R.drawable.emoji_5,
        R.drawable.emoji_6,
        R.drawable.emoji_7,
        R.drawable.emoji_8
    };
    
    private ActivityResultLauncher<Intent> settingsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        settings = new GameSettings(this);
        loadSettings();
        applyColorScheme();
        startNewGame();
        setupClickListeners();
        
        // Регистрируем launcher для результата из SettingsActivity
        settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadSettings();
                    applyColorScheme();
                    startNewGame();
                }
            }
        );
    }

    private void initializeViews() {
        mainLayout = findViewById(R.id.main);
        gridView = findViewById(R.id.gridView);
        titleTextView = findViewById(R.id.titleTextView);
        motivationTextView = findViewById(R.id.motivationTextView);
        movesTextView = findViewById(R.id.movesTextView);
        pairsTextView = findViewById(R.id.pairsTextView);
        pairsLabelTextView = findViewById(R.id.pairsLabelTextView);
        timerTextView = findViewById(R.id.timerTextView);
        progressPercentText = findViewById(R.id.progressPercentText);
        gameProgressBar = findViewById(R.id.gameProgressBar);
        streakTextView = findViewById(R.id.streakTextView);
        bestTimeTextView = findViewById(R.id.bestTimeTextView);
        restartButton = findViewById(R.id.restartButton);
        settingsButton = findViewById(R.id.settingsButton);
        statisticsButton = findViewById(R.id.statisticsButton);
        aboutButton = findViewById(R.id.aboutButton);
        
        handler = new Handler(Looper.getMainLooper());
    }
    
    private void loadSettings() {
        currentGridRows = settings.getGridRows();
        currentGridCols = settings.getGridCols();
        currentMatchCount = settings.getMatchCount();
        currentMixedMode = settings.isMixedMode();
    }
    
    private void applyColorScheme() {
        int primaryColor = settings.getPrimaryColor();
        int backgroundColor = settings.getBackgroundColor();
        int textColor = settings.getTextColor();
        
        // Применяем фон
        mainLayout.setBackgroundColor(backgroundColor);
        
        // Применяем цвет заголовка
        titleTextView.setTextColor(textColor);
        
        // Применяем цвет кнопки
        restartButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
    }
    
    private int[] getCurrentCardImages() {
        int cardSet = settings.getCardSet();
        switch (cardSet) {
            case GameSettings.CARD_SET_ANIMALS:
                return animalsImages;
            case GameSettings.CARD_SET_FRUITS:
                return fruitsImages;
            case GameSettings.CARD_SET_EMOJIS:
                return emojisImages;
            case GameSettings.CARD_SET_SHAPES:
            default:
                return shapesImages;
        }
    }
    
    private int getCardBackResource() {
        int colorScheme = settings.getColorScheme();
        switch (colorScheme) {
            case GameSettings.COLOR_GREEN:
                return R.drawable.card_back_green;
            case GameSettings.COLOR_PURPLE:
                return R.drawable.card_back_purple;
            case GameSettings.COLOR_ORANGE:
                return R.drawable.card_back_orange;
            case GameSettings.COLOR_PINK:
                return R.drawable.card_back_pink;
            case GameSettings.COLOR_BLUE:
            default:
                return R.drawable.card_back;
        }
    }

    private void startNewGame() {
        // Сбрасываем флаг ожидания
        isWaitingForClose = false;
        handler.removeCallbacksAndMessages(null);
        
        // Получаем текущий набор изображений
        int[] cardImages = getCurrentCardImages();
        
        if (currentMixedMode) {
            // Смешанный режим: создаем поле с парами и тройками
            int totalCards = currentGridRows * currentGridCols;
            // Подбираем количество пар и троек для заданного размера поля
            // Нужно решить: pairsCount * 2 + tripletsCount * 3 = totalCards
            // Пытаемся найти оптимальное сочетание
            int pairsCount = 0;
            int tripletsCount = 0;
            
            // Приоритет: больше троек, остаток - пары
            tripletsCount = totalCards / 3;
            int remaining = totalCards - tripletsCount * 3;
            
            while (remaining % 2 != 0 && tripletsCount > 0) {
                tripletsCount--;
                remaining = totalCards - tripletsCount * 3;
            }
            pairsCount = remaining / 2;
            
            // Если не получилось - делаем наоборот
            if (pairsCount * 2 + tripletsCount * 3 != totalCards || tripletsCount == 0) {
                pairsCount = totalCards / 2;
                remaining = totalCards - pairsCount * 2;
                while (remaining % 3 != 0 && pairsCount > 0) {
                    pairsCount--;
                    remaining = totalCards - pairsCount * 2;
                }
                tripletsCount = remaining / 3;
            }
            
            int groupsCount = pairsCount + tripletsCount;
            int[] selectedImages = new int[groupsCount];
            for (int i = 0; i < groupsCount; i++) {
                selectedImages[i] = cardImages[i % cardImages.length];
            }
            
            game = new MemoryGame(selectedImages, 2, true);
            // Пересоздаем карты с правильным распределением
            game.getCards().clear();
            int imageIndex = 0;
            
            // Создаем пары
            for (int i = 0; i < pairsCount && imageIndex < selectedImages.length; i++) {
                int imageRes = selectedImages[imageIndex++];
                for (int j = 0; j < 2; j++) {
                    game.getCards().add(new Card(imageRes, imageRes, 2));
                }
            }
            
            // Создаем тройки
            for (int i = 0; i < tripletsCount && imageIndex < selectedImages.length; i++) {
                int imageRes = selectedImages[imageIndex++];
                for (int j = 0; j < 3; j++) {
                    game.getCards().add(new Card(imageRes, imageRes, 3));
                }
            }
            
            // Перемешиваем карты
            java.util.Collections.shuffle(game.getCards());
        } else {
            // Обычный режим
            int totalCards = currentGridRows * currentGridCols;
            int groupsCount = totalCards / currentMatchCount;
            
            // Создаем массив изображений нужного размера
            int[] selectedImages = new int[groupsCount];
            for (int i = 0; i < groupsCount; i++) {
                selectedImages[i] = cardImages[i % cardImages.length];
            }
            
            game = new MemoryGame(selectedImages, currentMatchCount);
        }
        
        adapter = new CardAdapter(this, game.getCards(), getCardBackResource());
        adapter.setNumColumns(currentGridCols);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(currentGridCols);
        
        updateUI();
        startTimer();
    }

    private void setupClickListeners() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleCardClick(position);
            }
        });
        
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRestartDialog();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatisticsDialog();
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTaskDescription();
            }
        });
    }

    private void handleCardClick(final int position) {
        // Если идет ожидание закрытия карт, игнорируем клики
        if (isWaitingForClose) {
            return;
        }
        
        Card clickedCard = game.getCards().get(position);
        
        // Игнорируем клики на уже открытые или найденные карты
        if (clickedCard.isRevealed() || clickedCard.isMatched()) {
            return;
        }
        
        // Открываем карту
        clickedCard.setRevealed(true);
        game.addRevealedCard(clickedCard);
        adapter.notifyDataSetChanged();
        
        int revealedCount = game.getRevealedCount();
        // В смешанном режиме используем размер группы первой карты
        int requiredMatchCount = game.getCurrentRequiredMatchCount();
        
        // Если открыто нужное количество карт (2 для пар, 3 для троек)
        if (revealedCount == requiredMatchCount) {
            // Увеличиваем счетчик ходов
            game.incrementMoves();
            
            // Проверяем совпадение
            if (game.checkMatch()) {
                // Совпадение найдено!
                for (Card card : game.getRevealedCards()) {
                    card.setMatched(true);
                }
                game.incrementPairsFound();
                game.clearRevealedCards();
                adapter.notifyDataSetChanged();
                updateUI();
                showMotivationalMessage();
                checkGameWon();
            } else {
                // Не совпадают - показываем карты на секунду, затем закрываем
                isWaitingForClose = true;
                final java.util.List<Card> cardsToHide = new java.util.ArrayList<>(game.getRevealedCards());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (Card card : cardsToHide) {
                            card.setRevealed(false);
                        }
                        game.clearRevealedCards();
                        adapter.notifyDataSetChanged();
                        updateUI();
                        isWaitingForClose = false;
                    }
                }, 1000);
            }
            updateUI();
        }
    }
    
    private void showMotivationalMessage() {
        String[] messages = {
            "Отлично! 👍",
            "Молодец! 🌟",
            "Супер! 🎉",
            "Так держать! 💪",
            "Класс! ⭐",
            "Браво! 👏",
            "Невероятно! 🔥",
            "Восхитительно! ✨",
            "Гениально! 🧠",
            "Великолепно! 🏆"
        };
        int pairsFound = game.getPairsFound();
        String message = messages[pairsFound % messages.length];
        
        // Обновляем мотивационный текст
        motivationTextView.setText(message);
        motivationTextView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        movesTextView.setText(String.valueOf(game.getMoves()));
        
        int pairsFound = game.getPairsFound();
        int totalPairs = game.getTotalPairs();
        pairsTextView.setText(pairsFound + "/" + totalPairs);
        
        // Обновляем label для пар/троек
        String groupLabel;
        if (currentMixedMode) {
            groupLabel = "Группы";
        } else {
            groupLabel = currentMatchCount == 2 ? "Пары" : "Тройки";
        }
        pairsLabelTextView.setText(groupLabel);
        
        // Обновляем прогресс
        int progressPercent = totalPairs > 0 ? (pairsFound * 100 / totalPairs) : 0;
        gameProgressBar.setProgress(progressPercent);
        progressPercentText.setText(progressPercent + "%");
        
        // Обновляем серию побед и рекорд
        streakTextView.setText("🔥 Серия: " + settings.getWinStreak());
        
        long bestTime = settings.getBestTime();
        if (bestTime == Long.MAX_VALUE) {
            bestTimeTextView.setText("⭐ Рекорд: —");
        } else {
            int seconds = (int) (bestTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            bestTimeTextView.setText(String.format("⭐ Рекорд: %02d:%02d", minutes, seconds));
        }
        
        // Обновляем мотивационный текст в зависимости от прогресса
        updateMotivationText(progressPercent);
    }
    
    private void updateMotivationText(int progressPercent) {
        if (progressPercent == 0) {
            motivationTextView.setText("Удачи! 🍀");
        } else if (progressPercent < 25) {
            motivationTextView.setText("Хорошее начало! 🌱");
        } else if (progressPercent < 50) {
            motivationTextView.setText("Продолжай в том же духе! 💪");
        } else if (progressPercent < 75) {
            motivationTextView.setText("Уже больше половины! 🎯");
        } else if (progressPercent < 100) {
            motivationTextView.setText("Почти победа! 🔥");
        } else {
            motivationTextView.setText("Победа! 🏆");
        }
    }

    private void checkGameWon() {
        if (game.isGameWon()) {
            stopTimer();
            
            long elapsedTime = System.currentTimeMillis() - startTime;
            int moves = game.getMoves();
            int totalPairs = game.getTotalPairs();
            
            // Сохраняем расширенную статистику
            settings.incrementGamesPlayed();
            settings.incrementGamesWon();
            settings.incrementWinStreak();
            settings.updateBestTime(elapsedTime);
            settings.updateBestMoves(moves);
            settings.addPlayTime(elapsedTime);
            settings.addMoves(moves);
            settings.addPairsFound(totalPairs);
            settings.updateLastPlayed();
            
            // Статистика по размерам и режимам
            settings.updateGridSizeStats(currentGridRows, currentGridCols, elapsedTime, moves);
            settings.updateModeStats(currentMatchCount, currentMixedMode, elapsedTime);
            
            // Добавляем в историю
            settings.addGameToHistory(currentGridRows, currentGridCols, currentMatchCount, 
                                       currentMixedMode, elapsedTime, moves, true);
            
            // Проверяем достижения
            settings.checkAndUnlockAchievements(elapsedTime, moves, totalPairs);
            
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showWinDialog(elapsedTime, moves);
                }
            }, 500);
        }
    }

    private void showWinDialog(long elapsedTime, int moves) {
        int seconds = (int) (elapsedTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        
        // Проверяем, был ли побит рекорд
        boolean isNewRecord = elapsedTime == settings.getBestTime();
        boolean isNewBestMoves = moves == settings.getBestMoves();
        
        StringBuilder message = new StringBuilder();
        message.append("🎉 Поздравляем, ").append(settings.getPlayerName()).append("!\n\n");
        
        if (isNewRecord) {
            message.append("🏆 НОВЫЙ РЕКОРД ВРЕМЕНИ!\n\n");
        }
        if (isNewBestMoves) {
            message.append("⭐ ЛУЧШИЙ РЕЗУЛЬТАТ ПО ХОДАМ!\n\n");
        }
        
        message.append("📊 Результаты игры:\n");
        message.append("• Ходы: ").append(moves).append("\n");
        message.append("• Время: ").append(timeStr).append("\n");
        message.append("• Поле: ").append(currentGridRows).append("×").append(currentGridCols).append("\n\n");
        
        message.append("📈 Ваша статистика:\n");
        message.append("• Всего игр: ").append(settings.getGamesPlayed()).append("\n");
        message.append("• Побед: ").append(settings.getGamesWon()).append("\n");
        message.append("• Серия побед: ").append(settings.getWinStreak()).append(" 🔥\n");
        message.append("• Лучшая серия: ").append(settings.getBestWinStreak());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Победа! 🏆")
            .setMessage(message.toString())
            .setPositiveButton("Новая игра", (dialog, which) -> {
                startNewGame();
            })
            .setNeutralButton("Статистика", (dialog, which) -> {
                showStatisticsActivity();
            })
            .setNegativeButton("Закрыть", null)
            .setCancelable(false)
            .show();
    }

    private void showRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Перезапустить игру?")
            .setMessage("Текущий прогресс будет потерян.")
            .setPositiveButton("Да", (dialog, which) -> {
                startNewGame();
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void showSettingsDialog() {
        Intent intent = new Intent(this, SettingsActivity.class);
        settingsLauncher.launch(intent);
    }

    private void showStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    private void showStatisticsDialog() {
        showStatisticsActivity();
    }

    private void showTaskDescription() {
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.task_title))
            .setMessage(getString(R.string.task_description))
            .setPositiveButton("Закрыть", null)
            .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        } else if (id == R.id.action_statistics) {
            showStatisticsDialog();
            return true;
        } else if (id == R.id.action_about) {
            showTaskDescription();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        isTimerRunning = true;
        updateTimer();
    }

    private void stopTimer() {
        isTimerRunning = false;
    }

    private void updateTimer() {
        if (isTimerRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            
            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateTimer();
                }
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        handler.removeCallbacksAndMessages(null);
    }
}

