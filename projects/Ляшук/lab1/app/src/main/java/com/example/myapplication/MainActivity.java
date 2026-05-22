package com.example.memorygame;

import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.memorygame.adapter.GameAdapter;
import com.example.memorygame.data.GameRepository;
import com.example.memorygame.logic.GameLogic;
import com.example.memorygame.model.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private GameRepository gameRepository;
    private GameLogic gameLogic;
    private GameAdapter gameAdapter;
    private RecyclerView recyclerView;
    private TextView tvMoves, tvPairsLeft, tvTime, tvMotivation;
    private ProgressBar progressBar;
    private Button btnNewGame, btnSettings, btnRecords;

    private GameSettings gameSettings = new GameSettings();
    private List<Card> cards = new ArrayList<>();
    private List<Card> selectedCards = new ArrayList<>();
    private int moves = 0;
    private int pairsFound = 0;
    private long gameTime = 0;
    private Timer timer;
    private boolean isGameActive = true;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameRepository = new GameRepository(this);
        gameLogic = new GameLogic();

        initViews();
        setupClickListeners();
        startNewGame();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvMoves = findViewById(R.id.tvMoves);
        tvPairsLeft = findViewById(R.id.tvPairsLeft);
        tvTime = findViewById(R.id.tvTime);
        tvMotivation = findViewById(R.id.tvMotivation);
        progressBar = findViewById(R.id.progressBar);
        btnNewGame = findViewById(R.id.btnNewGame);
        btnSettings = findViewById(R.id.btnSettings);
        btnRecords = findViewById(R.id.btnRecords);

        recyclerView.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        btnNewGame.setOnClickListener(v -> showNewGameDialog());
        btnSettings.setOnClickListener(v -> showSettingsDialog());
        btnRecords.setOnClickListener(v -> showRecordsDialog());
    }

    private void startNewGame() {
        cards = gameLogic.createCards(gameSettings);
        pairsFound = 0;
        moves = 0;
        gameTime = 0;
        selectedCards.clear();
        isGameActive = true;

        updateStats();
        startTimer();
        setupAdapter();
    }

    private void setupAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, gameSettings.getGridSize()));
        gameAdapter = new GameAdapter(cards, gameSettings, position -> onCardClick(position));
        recyclerView.setAdapter(gameAdapter);
    }

    private void onCardClick(int position) {
        if (!isGameActive) return;

        Card card = cards.get(position);
        if (card.isMatched() || card.isFlipped()) return;

        card.setFlipped(true);
        gameAdapter.notifyItemChanged(position);

        selectedCards.add(card);

        int groupSize = gameSettings.getCardType() == CardType.PAIRS ? 2 : 3;

        if (selectedCards.size() == groupSize) {
            moves++;
            checkMatch();
        }

        updateStats();
    }

    private void checkMatch() {
        boolean isMatch = gameLogic.checkMatch(selectedCards, gameSettings.getCardType());

        if (isMatch) {
            for (Card card : selectedCards) {
                card.setMatched(true);
            }
            pairsFound++;

            int totalGroups = cards.size() / (gameSettings.getCardType() == CardType.PAIRS ? 2 : 3);
            if (pairsFound == totalGroups) {
                gameCompleted();
            }

            selectedCards.clear();
            gameAdapter.notifyDataSetChanged();
        } else {
            handler.postDelayed(() -> {
                for (Card card : selectedCards) {
                    card.setFlipped(false);
                }
                selectedCards.clear();
                gameAdapter.notifyDataSetChanged();
            }, 500);
        }
    }

    private void gameCompleted() {
        isGameActive = false;
        stopTimer();

        String playerName = gameRepository.getPlayerName();
        GameRecord record = new GameRecord(
                playerName,
                calculateScore(),
                (int) gameTime,
                gameSettings.getGridSize(),
                gameSettings.getCardType(),
                System.currentTimeMillis()
        );

        gameRepository.saveRecord(record);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Победа! 🎉")
                .setMessage("Вы выиграли!\nХоды: " + moves + "\nВремя: " + formatTime(gameTime) +
                        "\nСчет: " + calculateScore())
                .setPositiveButton("Отлично!", null)
                .show();
    }

    private int calculateScore() {
        int baseScore = 1000;
        long timePenalty = gameTime * 10;
        int movePenalty = moves * 5;
        int sizeBonus = gameSettings.getGridSize() * gameSettings.getGridSize() * 10;
        return Math.max(0, baseScore + sizeBonus - (int) timePenalty - movePenalty);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isGameActive) {
                    gameTime++;
                    runOnUiThread(() -> updateTime());
                }
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateTime() {
        tvTime.setText(formatTime(gameTime));
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void updateStats() {
        tvMoves.setText("Ходы: " + moves);

        int totalPairs = cards.size() / (gameSettings.getCardType() == CardType.PAIRS ? 2 : 3);
        int pairsLeft = totalPairs - pairsFound;
        tvPairsLeft.setText("Осталось: " + pairsLeft);

        int progress = gameLogic.calculateProgress(totalPairs, pairsFound);
        progressBar.setProgress(progress);

        tvMotivation.setText(gameLogic.generateMotivationalMessage(progress));
    }

    private void showNewGameDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Новая игра")
                .setMessage("Начать новую игру?")
                .setPositiveButton("Да", (dialog, which) -> {
                    stopTimer();
                    startNewGame();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void showSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);

        Slider sliderSize = dialogView.findViewById(R.id.sliderSize);
        TextView tvSizeValue = dialogView.findViewById(R.id.tvSizeValue);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupCardType);
        Spinner spinnerImageSet = dialogView.findViewById(R.id.spinnerImageSet);
        EditText editTextName = dialogView.findViewById(R.id.editTextName);

        sliderSize.setValue(gameSettings.getGridSize());
        tvSizeValue.setText(gameSettings.getGridSize() + "x" + gameSettings.getGridSize());

        sliderSize.addOnChangeListener((slider, value, fromUser) -> {
            int size = (int) value;
            tvSizeValue.setText(size + "x" + size);
        });

        if (gameSettings.getCardType() == CardType.PAIRS) {
            radioGroup.check(R.id.radioPairs);
        } else {
            radioGroup.check(R.id.radioTriplets);
        }

        String[] imageSets = {"Эмодзи", "Животные", "Фрукты", "Спорт"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, imageSets);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImageSet.setAdapter(adapter);
        spinnerImageSet.setSelection(gameSettings.getImageSet().ordinal());

        editTextName.setText(gameRepository.getPlayerName());

        new MaterialAlertDialogBuilder(this)
                .setTitle("Настройки")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    gameSettings.setGridSize((int) sliderSize.getValue());
                    gameSettings.setCardType(radioGroup.getCheckedRadioButtonId() == R.id.radioPairs ?
                            CardType.PAIRS : CardType.TRIPLETS);
                    gameSettings.setImageSet(ImageSet.values()[spinnerImageSet.getSelectedItemPosition()]);

                    gameRepository.savePlayerName(editTextName.getText().toString());

                    stopTimer();
                    startNewGame();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showRecordsDialog() {
        List<GameRecord> records = gameRepository.getRecords();
        View recordsView = getLayoutInflater().inflate(R.layout.dialog_records, null);
        ListView listView = recordsView.findViewById(R.id.listViewRecords);

        String[] recordStrings = new String[records.size()];
        for (int i = 0; i < records.size(); i++) {
            GameRecord record = records.get(i);
            recordStrings[i] = (i + 1) + ". " + record.getPlayerName() + " - " + record.getScore() + " очков";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, recordStrings);
        listView.setAdapter(adapter);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Рекорды")
                .setView(recordsView)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        handler.removeCallbacksAndMessages(null);
    }
}