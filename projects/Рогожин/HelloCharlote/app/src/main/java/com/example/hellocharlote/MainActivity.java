package com.example.hellocharlote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private GridLayout gameField;
    private int pairTimeBonus;
    private int tripleTimeBonus;
    private TextView statusText, pairsCounter, scoreText, timerText;
    private int initialTime;
    private int difficultyLevel;
    private static final int EASY = R.id.diffEasy;
    private static final int HARD = R.id.diffHard;
    private static final int NORMAL = R.id.diffNormal;

    private final ArrayList<ImageView> opened = new ArrayList<>();
    private final ArrayList<CardState> cardsState = new ArrayList<>();
    private boolean blocked = false;

    private int columns, rows, cardWidth, cardHeight;
    private int score = 0;
    private int groupsLeft = 0;
    private String nickname = "Player";

    private boolean infiniteMode = false;
    private int infiniteTime = 60;
    private final Handler handler = new Handler();

    private int cardBackRes;

    private final int[] tripleImages = { R.drawable.delirium };

    private final ArrayList<Integer> allImages = new ArrayList<>();
    private final ArrayList<Integer> gameImages = new ArrayList<>();

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

        SharedPreferences prefs = getSharedPreferences("game_settings", MODE_PRIVATE);
        int difficulty = prefs.getInt("difficulty", R.id.diffNormal);
        difficultyLevel = difficulty;

        // Настройка таймера и бонусов
        if (difficulty == R.id.diffEasy) {
            infiniteTime = 120;
            pairTimeBonus = 10;
            tripleTimeBonus = 15;
        } else if (difficulty == R.id.diffHard) {
            infiniteTime = 30;
            pairTimeBonus = 3;
            tripleTimeBonus = 5;
        } else {
            infiniteTime = 60;
            pairTimeBonus = 5;
            tripleTimeBonus = 10;
        }
        initialTime = infiniteTime;

        cardBackRes = R.drawable.backcard;

        gameField = findViewById(R.id.gameField);
        statusText = findViewById(R.id.statusText);
        pairsCounter = findViewById(R.id.pairsCounter);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        nickname = getIntent().getStringExtra("nickname");
        infiniteMode = getIntent().getBooleanExtra("infinite", false);
        String grid = getIntent().getStringExtra("grid");

        findViewById(R.id.restartButton).setOnClickListener(v -> restartGame());
        findViewById(R.id.recordsButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordsActivity.class);
            startActivity(intent);
        });

        initImages();
        configureGrid(grid);

        gameField.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gameField.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        calculateCardSize();

                        if (savedInstanceState != null) {
                            restoreCards(savedInstanceState);
                        } else {
                            startGame();
                        }
                        startTimer();
                    }
                }
        );
    }

    private void restartGame() {
        blocked = false;
        score = 0;

        int diff = getSharedPreferences("game_settings", MODE_PRIVATE)
                .getInt("difficulty", R.id.diffNormal);

        if (diff == R.id.diffEasy) {
            infiniteTime = 120;
            pairTimeBonus = 10;
            tripleTimeBonus = 15;
        } else if (diff == R.id.diffHard) {
            infiniteTime = 30;
            pairTimeBonus = 3;
            tripleTimeBonus = 5;
        } else { // средний
            infiniteTime = 60;
            pairTimeBonus = 5;
            tripleTimeBonus = 10;
        }
        initialTime = infiniteTime;
        startGame();
        updateUI();
    }

    private void restoreCards(Bundle savedInstanceState) {
        score = savedInstanceState.getInt("score");
        groupsLeft = savedInstanceState.getInt("groupsLeft");
        infiniteTime = savedInstanceState.getInt("infiniteTime");
        columns = savedInstanceState.getInt("columns", 4);
        rows = savedInstanceState.getInt("rows", 4);

        int[] imageRes = savedInstanceState.getIntArray("cardImages");
        boolean[] isFaceUp = savedInstanceState.getBooleanArray("cardFaceUp");
        boolean[] isMatched = savedInstanceState.getBooleanArray("cardMatched");

        if (imageRes == null || isFaceUp == null || isMatched == null) return;

        gameImages.clear();
        cardsState.clear();
        gameField.removeAllViews();

        for (int i = 0; i < imageRes.length; i++) {
            int img = imageRes[i];
            gameImages.add(img);

            ImageView card = new ImageView(this);
            card.setAdjustViewBounds(true);
            card.setScaleType(ImageView.ScaleType.FIT_CENTER);
            card.setTag(img);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cardWidth; lp.height = cardHeight;
            lp.setMargins(4, 4, 4, 4);
            card.setLayoutParams(lp);

            if (isMatched[i]) {
                card.setVisibility(View.INVISIBLE);
                card.setEnabled(false);
            } else {
                card.setVisibility(View.VISIBLE);
                card.setImageResource(isFaceUp[i] ? img : cardBackRes);
                card.setEnabled(true);
            }

            card.setOnClickListener(v -> onCardClick(card));
            gameField.addView(card);

            CardState cs = new CardState(card, img);
            cs.isFaceUp = isFaceUp[i];
            cs.isMatched = isMatched[i];
            cardsState.add(cs);
        }

        updateUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("score", score);
        outState.putInt("groupsLeft", groupsLeft);
        outState.putInt("infiniteTime", infiniteTime);
        outState.putInt("columns", columns);
        outState.putInt("rows", rows);

        int size = cardsState.size();
        int[] imageRes = new int[size];
        boolean[] isFaceUp = new boolean[size];
        boolean[] isMatched = new boolean[size];

        for (int i = 0; i < size; i++) {
            CardState cs = cardsState.get(i);
            imageRes[i] = cs.imageRes;
            isFaceUp[i] = cs.isFaceUp;
            isMatched[i] = cs.isMatched;
        }

        outState.putIntArray("cardImages", imageRes);
        outState.putBooleanArray("cardFaceUp", isFaceUp);
        outState.putBooleanArray("cardMatched", isMatched);
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (infiniteTime > 0) {
                    infiniteTime--;
                    updateUI();
                    handler.postDelayed(this, 1000);
                } else {
                    if (infiniteMode) {
                        // В бесконечном режиме таймер = 0 — конец игры
                        finishGame();
                    } else {
                        statusText.setText("Время вышло!");
                        blocked = true;
                    }
                }
            }
        }, 1000);
    }

    private void initImages() {
        Collections.addAll(allImages,
                R.drawable.fool, R.drawable.magician, R.drawable.high_priestess,
                R.drawable.empress, R.drawable.emperor, R.drawable.hierophant,
                R.drawable.lovers, R.drawable.chariot, R.drawable.strength,
                R.drawable.hermit, R.drawable.wheel_fortune, R.drawable.justice,
                R.drawable.hanged_man, R.drawable.death, R.drawable.temperance,
                R.drawable.devil, R.drawable.tower, R.drawable.star,
                R.drawable.moon, R.drawable.sun, R.drawable.judgement,
                R.drawable.world
        );
    }

    private void configureGrid(String grid) {
        columns = 4; rows = 4;
        if (grid == null) grid = "4 x 4";

        switch (grid) {
            case "2 x 2": columns = 2; rows = 2; break;
            case "3 x 3": columns = 3; rows = 3; break;
            case "4 x 4": columns = 4; rows = 4; break;
            case "5 x 5": columns = 5; rows = 5; break;
            case "6 x 6": columns = 6; rows = 6; break;
        }

        gameField.setColumnCount(columns);
        gameField.setRowCount(rows);
    }

    private void calculateCardSize() {
        int spacing = 4;
        int totalHSpacing = spacing * (columns + 1);
        int totalVSpacing = spacing * (rows + 1);

        int width = gameField.getWidth() - totalHSpacing;
        int height = gameField.getHeight() - totalVSpacing;
        int size = Math.min(width / columns, height / rows);

        size = Math.max(size, 80);
        cardWidth = size; cardHeight = size;

        int paddingLeft = Math.max(0, (gameField.getWidth() - (columns * cardWidth + totalHSpacing)) / 2);
        int paddingTop = Math.max(0, (gameField.getHeight() - (rows * cardHeight + totalVSpacing)) / 2);
        gameField.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
    }

    private void startGame() {
        gameField.removeAllViews();
        gameImages.clear();
        opened.clear();
        blocked = false;
        cardsState.clear();

        int totalCards = columns * rows;

        int tripleCount = (totalCards == 9 || totalCards == 25) ? 1 : 0;

        ArrayList<Integer> triples = new ArrayList<>();
        for (int t : tripleImages) triples.add(t);
        Collections.shuffle(triples);

        for (int i = 0; i < tripleCount; i++)
            for (int j = 0; j < 3; j++)
                gameImages.add(triples.get(i));

        int remaining = totalCards - gameImages.size();
        int pairsNeeded = remaining / 2;

        Collections.shuffle(allImages);
        for (int i = 0; i < pairsNeeded; i++) {
            int img = allImages.get(i % allImages.size());
            gameImages.add(img);
            gameImages.add(img);
        }

        Collections.shuffle(gameImages);
        groupsLeft = tripleCount + pairsNeeded;

        for (int img : gameImages) {
            ImageView card = new ImageView(this);
            card.setImageResource(cardBackRes);
            card.setAdjustViewBounds(true);
            card.setScaleType(ImageView.ScaleType.FIT_CENTER);
            card.setTag(img);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cardWidth; lp.height = cardHeight;
            lp.setMargins(4, 4, 4, 4);
            card.setLayoutParams(lp);

            card.setOnClickListener(v -> onCardClick(card));
            gameField.addView(card);

            cardsState.add(new CardState(card, img));
        }

        updateUI();
    }

    private boolean isTripleCard(int resId) {
        for (int t : tripleImages) if (resId == t) return true;
        return false;
    }

    private void onCardClick(ImageView card) {
        CardState clicked = null;
        for (CardState c : cardsState) if (c.view == card) { clicked = c; break; }
        if (clicked == null || blocked || clicked.isFaceUp || clicked.isMatched) return;

        clicked.isFaceUp = true;
        blocked = true;
        card.setEnabled(false);
        flip(card, clicked.imageRes);
        opened.add(card);

        final CardState finalClicked = clicked;

        handler.postDelayed(() -> {
            int imgRes = finalClicked.imageRes;
            boolean isTriple = isTripleCard(imgRes);
            int needed = isTriple ? 3 : 2;

            boolean same = true;
            for (ImageView cView : opened) if ((int)cView.getTag() != imgRes) { same = false; break; }

            if (!same) {
                for (ImageView cView : opened) flipBack(cView);
                score = Math.max(0, score - 25);
                opened.clear();
                blocked = false;
                updateUI();
                return;
            }

            if (opened.size() < needed) {
                blocked = false;
                return;
            }

            for (ImageView cView : opened) {
                for (CardState cs : cardsState) if (cs.view == cView) cs.isMatched = true;
                cView.setVisibility(View.INVISIBLE);
            }

            score += isTriple ? 150 : 100;
            if (infiniteMode) {
                if (isTriple) infiniteTime += tripleTimeBonus;
                else infiniteTime += pairTimeBonus;
            }

            groupsLeft--;
            opened.clear();
            blocked = false;
            updateUI();

            if (groupsLeft == 0) checkGameEnd();
        }, 500);
    }

    private void flipBack(ImageView card) {
        card.animate().rotationY(90).setDuration(150).withEndAction(() -> {
            card.setImageResource(cardBackRes);
            card.setRotationY(-90);
            card.animate().rotationY(0).setDuration(150).withEndAction(() -> card.setEnabled(true)).start();
        }).start();

        for (CardState cs : cardsState) if (cs.view == card) cs.isFaceUp = false;
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
        pairsCounter.setText("Осталось: " + groupsLeft);
        timerText.setText("Время: " + infiniteTime + "с");
    }

    private void checkGameEnd() {
        if (groupsLeft != 0) return;

        if (infiniteMode) {
            // Увеличиваем сетку и создаём новые карты
            advanceLevel();
            startGame();
        } else {
            finishGame();
        }
    }

    private void finishGame() {
        blocked = true;
        statusText.setText("Время вышло или победа!");

        Intent intent = new Intent(this, RecordsActivity.class);
        intent.putExtra("nickname", nickname);
        intent.putExtra("score", score);
        intent.putExtra("difficulty", difficultyLevel);
        intent.putExtra("totalTime", initialTime - infiniteTime); // общее время игры
        startActivity(intent);
        finish();
    }


    private void advanceLevel() {
        if (columns < 6 && rows < 6) {
            columns++; rows++;
        }
        gameField.setColumnCount(columns);
        gameField.setRowCount(rows);
        calculateCardSize();
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
                        calculateCardSize();
                    }
                }
        );
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}