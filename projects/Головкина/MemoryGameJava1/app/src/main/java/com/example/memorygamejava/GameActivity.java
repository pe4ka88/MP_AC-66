package com.example.memorygamejava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private GridView gridView;
    private Button restartButton;
    private TextView timerTextView, movesTextView, pairsLeftTextView;

    private List<Integer> cardValues = new ArrayList<>();
    private List<Integer> flippedPositions = new ArrayList<>();
    private int fieldSize, totalSets, matchedSets, moveCount, seconds;
    private boolean canFlip = true;
    private Timer timer;
    private String playerName = "Игрок";

    private final int[] cardImages = {
            R.drawable.card_1, R.drawable.card_2, R.drawable.card_3,
            R.drawable.card_4, R.drawable.card_5, R.drawable.card_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        applyTheme();

        fieldSize = getIntent().getIntExtra("FIELD_SIZE", 4);
        totalSets = calculateTotalSets();

        initViews();
        loadSettings();
        startNewGame();
        startTimer();
    }

    private int calculateTotalSets() {
        int totalCards = fieldSize * fieldSize;
        return (fieldSize == 3) ? 3 : totalCards / 2;
    }

    private void initViews() {
        gridView = findViewById(R.id.gridView);
        restartButton = findViewById(R.id.restartButton);
        timerTextView = findViewById(R.id.timerTextView);
        movesTextView = findViewById(R.id.movesTextView);
        pairsLeftTextView = findViewById(R.id.pairsLeftTextView);

        gridView.setNumColumns(fieldSize);
        restartButton.setOnClickListener(v -> startNewGame());

        findViewById(R.id.menuButton).setOnClickListener(v -> finish());
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        int themeIndex = prefs.getInt("theme", 0);

        View rootView = getWindow().getDecorView().getRootView();
        int color;

        switch (themeIndex) {
            case 1: color = getResources().getColor(R.color.light_purple); break;
            case 2: color = getResources().getColor(R.color.light_blue); break;
            case 3: color = getResources().getColor(R.color.light_green); break;
            case 4: color = getResources().getColor(R.color.light_gray); break;
            default: color = getResources().getColor(android.R.color.white); break;
        }
        rootView.setBackgroundColor(color);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        playerName = prefs.getString("playerName", "Игрок");
    }

    private void startNewGame() {
        matchedSets = moveCount = seconds = 0;
        canFlip = true;
        flippedPositions.clear();
        cardValues.clear();

        updateUI();
        createCards();

        CardAdapter adapter = new CardAdapter(this, cardValues, fieldSize,
                position -> onCardClicked(position));
        gridView.setAdapter(adapter);

        stopTimer();
        startTimer();
    }

    private void createCards() {
        List<Integer> images = new ArrayList<>();
        int cardsPerSet = (fieldSize == 3) ? 3 : 2;

        for (int i = 0; i < totalSets; i++) {
            int imageId = cardImages[i % cardImages.length];
            for (int j = 0; j < cardsPerSet; j++) {
                images.add(imageId);
            }
        }

        Collections.shuffle(images);
        cardValues.addAll(images);
    }

    private void onCardClicked(int position) {
        if (!canFlip || flippedPositions.contains(position)) return;

        View card = gridView.getChildAt(position);
        if (card == null) return;

        flipCard(card, position);
    }

    private void flipCard(View card, int position) {
        View cardBack = card.findViewById(R.id.cardBack);
        View cardFront = card.findViewById(R.id.cardFront);

        if (cardBack.getVisibility() == View.VISIBLE) {
            cardBack.setVisibility(View.INVISIBLE);
            cardFront.setVisibility(View.VISIBLE);

            flippedPositions.add(position);
            moveCount++;
            updateUI();

            int cardsNeeded = (fieldSize == 3) ? 3 : 2;
            if (flippedPositions.size() == cardsNeeded) {
                canFlip = false;
                checkForMatch();
            }
        }
    }

    private void checkForMatch() {
        new Handler().postDelayed(() -> {
            boolean allMatch = true;
            int firstImageId = cardValues.get(flippedPositions.get(0));

            for (int pos : flippedPositions) {
                if (cardValues.get(pos) != firstImageId) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                matchedSets++;
                hideMatchedCards();
                checkGameEnd();
            } else {
                flipCardsBack();
            }

            flippedPositions.clear();
            canFlip = true;
            updateUI();
        }, 1000);
    }

    private void hideMatchedCards() {
        for (int pos : flippedPositions) {
            View card = gridView.getChildAt(pos);
            if (card != null) card.setVisibility(View.INVISIBLE);
        }
    }

    private void flipCardsBack() {
        for (int pos : flippedPositions) {
            View card = gridView.getChildAt(pos);
            if (card != null) {
                View cardBack = card.findViewById(R.id.cardBack);
                View cardFront = card.findViewById(R.id.cardFront);
                cardBack.setVisibility(View.VISIBLE);
                cardFront.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void checkGameEnd() {
        if (matchedSets == totalSets) {
            stopTimer();
            saveRecord();

            Toast.makeText(this,
                    "ПОБЕДА!\nВремя: " + formatTime(seconds) +
                            "\nХодов: " + moveCount +
                            "\nИгрок: " + playerName,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void saveRecord() {
        SharedPreferences.Editor editor = getSharedPreferences("GameRecords", MODE_PRIVATE).edit();
        String recordKey = "record_" + fieldSize + "x" + fieldSize;
        String newRecord = playerName + ":" + seconds + ":" + moveCount;
        editor.putString(recordKey, newRecord);
        editor.apply();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    seconds++;
                    timerTextView.setText("Время: " + formatTime(seconds));
                });
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void updateUI() {
        timerTextView.setText("Время: " + formatTime(seconds));
        movesTextView.setText("Ходов: " + moveCount);
        pairsLeftTextView.setText("Осталось: " + (totalSets - matchedSets));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}