package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GameTimer.TimerListener {

    private enum GameScreen {
            MENU,
            GAME,
            SETTINGS,
    }

    private static final int INITIAL_TIME_SECONDS = 20;
    private static final int MATCH_DELAY_MS = 400;
    private static final int MISMATCH_DELAY_MS = 600;

    // layouts
    private View layoutMenu;
    private View layoutGame;
    private View layoutSettings;

    // game UI
    private GridLayout gameGrid;
    private TextView tvTimer;
    private TextView tvBonus;

    // settings
    private Switch switchHardMode;

    private GameEngine gameEngine;
    private GameState gameState;
    private GameTimer gameTimer;

    private boolean isGameOver = false;
    private boolean isBusy = false;

    private final List<Card> openedCards = new ArrayList<>();
    private final List<ImageButton> openedButtons = new ArrayList<>();

    private final int[] images = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img7,
            R.drawable.img8,
            R.drawable.img9,
            R.drawable.img10,
            R.drawable.img11,
            R.drawable.img12,
            R.drawable.img13,
            R.drawable.img14,
            R.drawable.img15,
            R.drawable.img16,
            R.drawable.img17,
            R.drawable.img18
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnRecords = findViewById(R.id.btnRecords);

        btnRecords.setOnClickListener(v ->
                startActivity(new Intent(this, RecordsActivity.class))
        );
        gameEngine = new GameEngine();
        gameState = new GameState();
        gameTimer = new GameTimer(this);

        initViews();
        showScreen(GameScreen.MENU);
    }

    private void initViews() {

        layoutMenu = findViewById(R.id.layoutMenu);
        layoutGame = findViewById(R.id.layoutGame);
        layoutSettings = findViewById(R.id.layoutSettings);
        RadioGroup group = findViewById(R.id.radioGroupFieldSize);

        group.setOnCheckedChangeListener((g, checkedId) -> {

            if (checkedId == R.id.radio4)
                gameState.setFieldMode(GameState.FieldMode.FIXED_4);

            else if (checkedId == R.id.radio5)
                gameState.setFieldMode(GameState.FieldMode.FIXED_5);

            else if (checkedId == R.id.radio6)
                gameState.setFieldMode(GameState.FieldMode.FIXED_6);

            else if (checkedId == R.id.radioOrder)
                gameState.setFieldMode(GameState.FieldMode.ORDER);
        });

        Switch switchEndless = findViewById(R.id.switchEndless);
        switchEndless.setOnCheckedChangeListener((v, checked) ->
                gameState.setEndlessMode(checked));
        gameGrid = findViewById(R.id.gameGrid);
        tvTimer = findViewById(R.id.tvTimer);
        tvBonus = findViewById(R.id.tvBonus);

        switchHardMode = findViewById(R.id.switchHardMode);

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            showScreen(GameScreen.GAME);
            layoutGame.post(this::startGame);
        });

        findViewById(R.id.btnSettings).setOnClickListener(v ->
                showScreen(GameScreen.SETTINGS));

        findViewById(R.id.btnPause).setOnClickListener(v -> {
            gameTimer.pause();
            showPauseDialog();
        });

        findViewById(R.id.btnBackToMenu).setOnClickListener(v ->
                showScreen(GameScreen.MENU));

        switchHardMode.setOnCheckedChangeListener((v, checked) ->
                gameState.setHardMode(checked));
    }

    private void startGame() {
        gameTimer.stop();
        gameState.reset();

        isGameOver = false;
        isBusy = false;

        gameTimer.start(INITIAL_TIME_SECONDS);
        startLevel();
    }
    private void showScreen(GameScreen screen) {

        layoutMenu.setVisibility(View.GONE);
        layoutGame.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.GONE);

        switch (screen) {
            case MENU:
                layoutMenu.setVisibility(View.VISIBLE);
                break;
            case GAME:
                layoutGame.setVisibility(View.VISIBLE);
                break;
            case SETTINGS:
                layoutSettings.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void startLevel() {
        gameState.resetProgress();
        openedCards.clear();
        openedButtons.clear();
        gameGrid.removeAllViews();

        int size = gameState.getNextFieldSize();

        int rows = size;
        int cols = size;

        gameGrid.setRowCount(rows);
        gameGrid.setColumnCount(cols);

        List<Card> cards = gameEngine.startNewGame(
                rows,
                cols,
                images,
                rows % 2 != 0,
                gameState.isHardMode()
        );

        gameState.setTotalGroups(gameEngine.getTotalGroups());

        gameGrid.post(() -> {
            int gridWidth  = gameGrid.getWidth();
            int gridHeight = gameGrid.getHeight();

            // Отступ уменьшается с ростом размерности: 4→4dp, 5→3dp, 6→2dp
            int marginDp = Math.max(0, 6 - cols);
            float scale  = getResources().getDisplayMetrics().density;
            int marginPx = (int)(marginDp * scale + 0.5f);

            int cellByWidth  = (gridWidth  - marginPx * 2 * cols) / cols;
            int cellByHeight = (gridHeight - marginPx * 2 * rows) / rows;
            int cellSize     = Math.min(cellByWidth, cellByHeight);


            for (int i = 0; i < cards.size(); i++) {

                Card card = cards.get(i);

                ImageButton button = new ImageButton(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(i / cols),
                        GridLayout.spec(i % cols)
                );

                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(marginPx, marginPx, marginPx, marginPx);

                button.setLayoutParams(params);
                button.setTag(card);
                button.setScaleType(ImageButton.ScaleType.CENTER_CROP);
                button.setAdjustViewBounds(false);
                button.setBackground(null);
                button.setImageResource(R.drawable.card_back);

                button.setOnClickListener(v -> onCardClicked(button, card));

                gameGrid.addView(button);
            }
        });
    }
    private void onCardClicked(ImageButton button, Card card) {

        if (isGameOver || isBusy) return;

        GameEngine.MoveResult result = gameEngine.onCardSelected(card);
        if (result == null) return;

// Анимация открытия
        animateOpen(button, card.getImageResId(), () -> {
            switch (result.type) {

                case OPENED:
                case WAIT_FOR_THIRD:
                    return;

                case JOKER:
                case MISMATCH:
                    isBusy = true;
                    gameGrid.postDelayed(() -> {
                        for (Card c : result.affectedCards) {
                            flipCardBack(c);
                        }
                        isBusy = false;
                    }, MISMATCH_DELAY_MS);
                    return;

                case MATCH:
                    isBusy = true;
                    gameGrid.postDelayed(() -> {

                        for (Card c : result.affectedCards) {
                            hideCard(c);
                        }

                        gameState.incrementMatchedGroups();
                        long bonus = gameState.getBonusTimeMillis();
                        gameTimer.addTimeMillis(bonus);
                        showBonus((int) (bonus / 1000));

                        if (gameState.isLevelComplete()) {
                            onLevelCompleted();
                        }

                        isBusy = false;

                    }, MATCH_DELAY_MS);
                    break;
            }
        });
    }
private void animateOpen(ImageButton button, int newImageResId, Runnable onEnd) {
    button.animate()
            .scaleX(0.7f).scaleY(0.7f)
            .alpha(0f)
            .setDuration(80)
            .withEndAction(() -> {
                button.setImageResource(newImageResId);
                button.animate()
                        .scaleX(1f).scaleY(1f)
                        .alpha(1f)
                        .setDuration(80)
                        .withEndAction(onEnd)
                        .start();
            })
            .start();
}

    private void animateClose(ImageButton button, int imageResId, Runnable onEnd) {
        button.animate()
                .scaleX(1.15f).scaleY(1.15f)
                .alpha(0f)
                .setDuration(80)
                .withEndAction(() -> {
                    button.setImageResource(imageResId);
                    button.animate()
                            .scaleX(1f).scaleY(1f)
                            .alpha(1f)
                            .setDuration(80)
                            .withEndAction(onEnd)
                            .start();
                })
                .start();
    }

        private void flipCardBack(Card card) {
            for (int i = 0; i < gameGrid.getChildCount(); i++) {
                ImageButton btn = (ImageButton) gameGrid.getChildAt(i);
                if (btn.getTag() == card) {
                    animateClose(btn, R.drawable.card_back, null);
                    break;
                }
            }
        }

    private void hideCard(Card card) {
        for (int i = 0; i < gameGrid.getChildCount(); i++) {
            ImageButton btn = (ImageButton) gameGrid.getChildAt(i);
            if (btn.getTag() == card) {
                btn.setVisibility(View.INVISIBLE);
                break;
            }
        }
    }
    private void onLevelCompleted() {

        if (gameState.isEndlessMode()) {
            startLevel();
        } else {
            showWinDialog();
        }
    }

    private void showWinDialog() {
        gameTimer.pause();
        new AlertDialog.Builder(this)
                .setTitle("Победа!")
                .setMessage("Вы прошли все уровни")
                .setCancelable(false)
                .setPositiveButton("Заново", (d, w) -> {
                    startGame();
                    showScreen(GameScreen.GAME);
                })
                .setNegativeButton("Меню", (d, w) -> {
                    gameTimer.stop();
                    showScreen(GameScreen.MENU);
                })
                .setNeutralButton("Сохранить рекорд", (d, w) -> showSaveRecordDialog())

                .show();
    }
    private void showPauseDialog() {
        gameTimer.pause();
        new AlertDialog.Builder(this)
                .setTitle("Пауза")
                .setCancelable(false)
                .setPositiveButton("Продолжить", (d, w) -> {
                    gameTimer.resume();
                })
                .setNeutralButton("Заново", (d, w) -> {
                    startGame();
                })
                .setNegativeButton("Меню", (d, w) -> {
                    gameTimer.stop();
                    showScreen(GameScreen.MENU);
                })
                .setNeutralButton("Сохранить рекорд", (d, w) -> showSaveRecordDialog())
                .show();
    }
    private void showSaveRecordDialog() {

        EditText input = new EditText(this);
        input.setHint("Введите имя");

        new AlertDialog.Builder(this)
                .setTitle("Сохранить рекорд")
                .setView(input)
                .setPositiveButton("Сохранить", (d, w) -> {

                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        name = "User" + (int)(Math.random() * 9999);
                    }

                    int timeLeft = gameTimer.getTimeLeft();
                    String mode = gameState.getModeDescription();

                    saveRecordToFirestore(name, timeLeft, mode);
                    showPauseDialog();
                })
                .setNegativeButton("Отмена", (d, w) -> showPauseDialog())
                .show();
    }
    private void saveRecordToFirestore(String name, int timeLeft, String mode) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Record record = new Record(name, timeLeft, mode);

        db.collection("records").add(record);
    }
    private void showLoseDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Время вышло")
                .setMessage("Вы проиграли")
                .setCancelable(false)
                .setPositiveButton("Заново", (d, w) -> {
                    startGame();
                    showScreen(GameScreen.GAME);
                })
                .setNegativeButton("Меню", (d, w) -> {
                    showScreen(GameScreen.MENU);
                })
                .show();
    }
    private void showBonus(int seconds) {

        tvBonus.setText("+" + seconds);
        tvBonus.setAlpha(1f);
        tvBonus.setVisibility(View.VISIBLE);

        tvBonus.animate()
                .translationY(-50f)
                .alpha(0f)
                .setDuration(800)
                .withEndAction(() -> {
                    tvBonus.setVisibility(View.INVISIBLE);
                    tvBonus.setTranslationY(0f);
                })
                .start();
    }

    @Override
    public void onTick(int secondsLeft) {
        tvTimer.setText(String.valueOf(secondsLeft));
    }

    @Override
    public void onFinish() {
        isGameOver = true;
        showLoseDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameTimer.stop();
    }
}