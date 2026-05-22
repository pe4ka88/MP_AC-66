package com.example.memoryezepchukac66;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    // UI
    private GridLayout gameField;
    private TextView statusText;
    private TextView pairsCounter;
    private TextView scoreText;
    private Button restartButton;
    private Button recordsButton;

    private int cardBackRes;
    private final int[] cardBacks = { R.drawable.backcard, R.drawable.backcard_2, R.drawable.backcard_3 };

    private final ArrayList<ImageView> opened = new ArrayList<>();
    private boolean blocked = false;

    private int groupsLeft;
    private int score = 0;
    private int errors = 0;

    private boolean infiniteMode = false;
    private int level = 0;
    private String nickname = "Player";

    private static final int MAX_ERRORS = 10;
    private final Handler handler = new Handler();

    private int columns, rows, cardWidth, cardHeight;

    // Images
    private final ArrayList<Integer> allImages = new ArrayList<>();
    private final ArrayList<Integer> gameImages = new ArrayList<>();

    // Triple cards
    private final int[] tripleImages = { R.drawable.eimiya_3, R.drawable.eula_3, R.drawable.fichl_3, R.drawable.diona_3 };

    // Levels
    private final int[][] LEVELS = { {2,2}, {3,3}, {4,4}, {5,5}, {6,6} };

    // Состояние карт
    private final ArrayList<CardState> cardsState = new ArrayList<>();

    private static class CardState {
        ImageView view;
        int imageRes;
        boolean isFaceUp = false;
        boolean isMatched = false;

        CardState(ImageView v, int res) { view = v; imageRes = res; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(v -> restartLevel());

        recordsButton = findViewById(R.id.recordsButton);
        recordsButton.setOnClickListener(v -> startActivity(new Intent(this, RecordsActivity.class)));

        SharedPreferences prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        int backIndex = prefs.getInt("card_back_index", 0);
        cardBackRes = cardBacks[Math.max(0, Math.min(backIndex, cardBacks.length - 1))];

        gameField = findViewById(R.id.gameField);
        statusText = findViewById(R.id.statusText);
        pairsCounter = findViewById(R.id.pairsCounter);
        scoreText = findViewById(R.id.scoreText);

        nickname = getIntent().getStringExtra("nickname");
        infiniteMode = getIntent().getBooleanExtra("infinite", false);
        String grid = getIntent().getStringExtra("grid");

        initImages();

        if (infiniteMode) {
            level = 0; score = 0; errors = 0;
            configureGridByLevel();
        } else {
            configureGrid(grid);
        }

        calculateCardSize();
    }

    private void initImages() {
        Collections.addAll(allImages,
                R.drawable.baiju, R.drawable.diluk, R.drawable.keya,
                R.drawable.saharoza, R.drawable.gayui, R.drawable.tartalia,
                R.drawable.furina, R.drawable.bennet, R.drawable.kli,
                R.drawable.hutao, R.drawable.liny, R.drawable.arlicino,
                R.drawable.raiden, R.drawable.venti);
    }

    private void configureGrid(String grid) {
        columns = 4; rows = 4;
        switch (grid) {
            case "2 x 2": columns = 2; rows = 2; break;
            case "3 x 3": columns = 3; rows = 3; break;
            case "4 x 4": columns = 4; rows = 4; break;
            case "4 x 6": columns = 4; rows = 6; break;
            case "5 x 5": columns = 5; rows = 5; break;
            case "6 x 6": columns = 6; rows = 6; break;
        }
        gameField.setColumnCount(columns);
        gameField.setRowCount(rows);
    }

    private void configureGridByLevel() {
        if (level < LEVELS.length) { columns = LEVELS[level][0]; rows = LEVELS[level][1]; }
        else { columns = 6; rows = 6; }
        gameField.setColumnCount(columns);
        gameField.setRowCount(rows);
    }

    private void calculateCardSize() {
        gameField.post(() -> {
            int spacing = 4;
            int totalHSpacing = spacing * (columns + 1);
            int totalVSpacing = spacing * (rows + 1);

            int width = gameField.getWidth() - totalHSpacing;
            int height = gameField.getHeight() - totalVSpacing;

            int size = Math.min(width / columns, height / rows);
            cardWidth = size; cardHeight = size;

            int paddingLeft = Math.max(0, (gameField.getWidth() - (columns * cardWidth + totalHSpacing)) / 2);
            int paddingTop = Math.max(0, (gameField.getHeight() - (rows * cardHeight + totalVSpacing)) / 2);
            gameField.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);

            // если есть сохранённые карты (поворот)
            if (!cardsState.isEmpty()) {
                restoreGameField();
            } else {
                startGame();
            }
        });
    }

    private boolean isTripleCard(int resId) {
        for (int t : tripleImages) if (resId == t) return true;
        return false;
    }

    private void startGame() {
        gameField.removeAllViews();
        gameImages.clear();
        opened.clear();
        blocked = false;
        cardsState.clear();

        int totalCards = columns * rows;
        int tripleCount = (totalCards == 9 || totalCards == 25) ? 1 :
                (totalCards == 24 ? 2 : (totalCards == 36 ? 4 : 0));

        ArrayList<Integer> triples = new ArrayList<>();
        for (int t : tripleImages) triples.add(t);
        Collections.shuffle(triples);

        for (int i = 0; i < tripleCount; i++)
            for (int j = 0; j < 3; j++) gameImages.add(triples.get(i));

        int remaining = totalCards - gameImages.size();
        int pairs = remaining / 2;

        Collections.shuffle(allImages);
        int idx = 0;
        while (pairs-- > 0) {
            int img = allImages.get(idx++);
            gameImages.add(img); gameImages.add(img);
        }

        Collections.shuffle(gameImages);
        groupsLeft = tripleCount + remaining / 2;

        for (int img : gameImages) {
            ImageView card = new ImageView(this);
            card.setImageResource(cardBackRes);
            card.setAdjustViewBounds(true);
            card.setScaleType(ImageView.ScaleType.FIT_CENTER);
            card.setTag(img);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cardWidth; lp.height = cardHeight;
            lp.setMargins(4,4,4,4);
            card.setLayoutParams(lp);

            card.setOnClickListener(v -> onCardClick(card));
            gameField.addView(card);

            cardsState.add(new CardState(card, img));
        }
        updateUI();
    }

    private void restoreGameField() {
        gameField.removeAllViews();
        for (CardState c : cardsState) {
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cardWidth; lp.height = cardHeight;
            lp.setMargins(4,4,4,4);
            c.view.setLayoutParams(lp);

            c.view.setImageResource(c.isFaceUp || c.isMatched ? c.imageRes : cardBackRes);
            c.view.setVisibility(c.isMatched ? View.INVISIBLE : View.VISIBLE);

            c.view.setOnClickListener(v -> onCardClick(c.view));
            gameField.addView(c.view);
        }
        updateUI();
    }


    private void onCardClick(ImageView card) {
        CardState clicked = null;
        for (CardState c : cardsState) {
            if (c.view == card) { clicked = c; break; }
        }
        if (clicked == null || blocked || clicked.isFaceUp || clicked.isMatched) return;

        clicked.isFaceUp = true;
        blocked = true;
        card.setEnabled(false);
        flip(card, clicked.imageRes);
        opened.add(card);

        final CardState finalClicked = clicked;

        handler.postDelayed(() -> {
            int imgRes = finalClicked.imageRes; // используем finalClicked
            boolean isTriple = isTripleCard(imgRes);
            int needed = isTriple ? 3 : 2;

            boolean same = true;
            for (ImageView cView : opened) {
                if ((int)cView.getTag() != imgRes) { same = false; break; }
            }

            if (!same) {
                for (ImageView cView : opened) {
                    flip(cView, cardBackRes);
                    for (CardState cs : cardsState)
                        if (cs.view == cView) cs.isFaceUp = false;
                    cView.setEnabled(true);
                }
                opened.clear();
                score = Math.max(0, score - 25);

                if (infiniteMode && ++errors >= MAX_ERRORS) endInfiniteGame();
                blocked = false;
                updateUI();
                return;
            }

            if (opened.size() < needed) {
                blocked = false;
                return;
            }

            for (ImageView cView : opened) {
                for (CardState cs : cardsState)
                    if (cs.view == cView) cs.isMatched = true;
                cView.setVisibility(View.INVISIBLE);
            }

            score += isTriple ? 150 : 100;
            groupsLeft--;
            if (infiniteMode && errors > 0) errors--;

            opened.clear();
            blocked = false;
            updateUI();

            if (groupsLeft == 0) checkGameEnd();

        }, 500);
    }


    private void checkGameEnd() {
        if (groupsLeft == 0) {
            if (infiniteMode) {
                level++;
                handler.postDelayed(this::nextLevel, 1000);
            } else {
                saveRecord();
                statusText.setText("🎉 Победа!");
            }
        }
    }

    private void flip(ImageView card, int img) {
        card.animate().rotationY(90).setDuration(150).withEndAction(() -> {
            card.setImageResource(img);
            card.setRotationY(-90);
            card.animate().rotationY(0).setDuration(150).start();
        }).start();
    }

    private void updateUI() {
        scoreText.setText("Счёт: " + score);
        if (infiniteMode)
            statusText.setText("Уровень " + (level+1) + " | Ошибки: " + errors + "/" + MAX_ERRORS);
        pairsCounter.setText("Осталось: " + groupsLeft);
    }

    private void nextLevel() {
        configureGridByLevel();
        calculateCardSize();
        startGame();
    }

    private void restartLevel() {
        opened.clear();
        blocked = false;
        if (infiniteMode) { errors = 0; configureGridByLevel(); }
        else { configureGrid(getIntent().getStringExtra("grid")); score = 0; }
        cardsState.clear();
        calculateCardSize();
        startGame();
    }

    private void saveRecord() {
        if (nickname == null || nickname.isEmpty()) return;

        // Получаем ссылку на Firebase Realtime Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("records");

        // Ссылка на конкретного пользователя
        DatabaseReference userRef = dbRef.child(nickname);

        // Считаем, что score — это int
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer oldScore = null;
                if (task.getResult().exists()) {
                    oldScore = task.getResult().getValue(Integer.class);
                }

                if (oldScore == null || score > oldScore) {
                    // Сохраняем новый рекорд
                    userRef.setValue(score).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            // Можно обновить UI
                            updateUI();
                        } else {
                            // Ошибка при записи
                            Log.e("Firebase", "Ошибка сохранения рекорда", saveTask.getException());
                        }
                    });
                }
            } else {
                Log.e("Firebase", "Ошибка чтения рекорда", task.getException());
            }
        });
    }

    private void endInfiniteGame() {
        saveRecord();
        handler.postDelayed(() -> {
            startActivity(new Intent(this, RecordsActivity.class));
            finish();
        }, 800);
    }

    @Override
    public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handler.removeCallbacksAndMessages(null);

        gameField.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gameField.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int spacing = 4;
                        int totalHSpacing = spacing * (columns + 1);
                        int totalVSpacing = spacing * (rows + 1);

                        int width = gameField.getWidth() - totalHSpacing;
                        int height = gameField.getHeight() - totalVSpacing;

                        if(width<=0||height<=0) return;

                        int size = Math.min(width/columns, height/rows);
                        cardWidth = size; cardHeight = size;

                        int paddingLeft = Math.max(0, (gameField.getWidth()-(columns*cardWidth+totalHSpacing))/2);
                        int paddingTop = Math.max(0, (gameField.getHeight()-(rows*cardHeight+totalVSpacing))/2);
                        gameField.setPadding(paddingLeft,paddingTop,paddingLeft,paddingTop);

                        restoreGameField();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}
