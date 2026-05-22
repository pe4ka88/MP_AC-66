package com.example.test1;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GridView grid;
    private GridAdapter adapter;
    private TextView tvGroupsCount, tvTimer, tvMovesLabel, tvBestLabel, tvStatus, tvVictoryInfo;
    private View overlayVictory;
    private Button btnRestart, btnToMenu, btnOnceMore;

    private int moves = 0;
    private long startTime;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("MemoryGame", MODE_PRIVATE);
        showMenu();
    }

    private void showMenu() {
        setContentView(R.layout.start_activity);
        
        Button startBtn = findViewById(R.id.start_button);
        Spinner spinnerW = findViewById(R.id.spinner_width);
        Spinner spinnerH = findViewById(R.id.spinner_height);
        Spinner spinnerG = findViewById(R.id.spinner_groups);

        // Настраиваем адаптеры для спиннеров, чтобы текст был белым
        setupWhiteSpinner(spinnerW, R.array.numbers);
        setupWhiteSpinner(spinnerH, R.array.numbers);
        setupWhiteSpinner(spinnerG, R.array.numbers);

        startBtn.setOnClickListener(v -> {
            try {
                int w = Integer.parseInt(spinnerW.getSelectedItem().toString());
                int h = Integer.parseInt(spinnerH.getSelectedItem().toString());
                int g = Integer.parseInt(spinnerG.getSelectedItem().toString());
                startGame(w, h, g);
            } catch (Exception e) {
                startGame(4, 4, 2);
            }
        });
    }

    private void setupWhiteSpinner(Spinner spinner, int arrayRes) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getTextArray(arrayRes)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(Color.parseColor("#2C2C34")); // Темный фон выпадающего списка
                ((TextView) v).setTextColor(Color.WHITE); // Белый текст
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void startGame(int cols, int rows, int gSize) {
        setContentView(R.layout.activity_main);

        grid = findViewById(R.id.gridView);
        tvGroupsCount = findViewById(R.id.tv_groups_count);
        tvTimer = findViewById(R.id.tv_timer);
        tvMovesLabel = findViewById(R.id.tv_moves_label);
        tvBestLabel = findViewById(R.id.tv_best_label);
        tvStatus = findViewById(R.id.tv_status);
        btnRestart = findViewById(R.id.btn_restart);
        
        overlayVictory = findViewById(R.id.overlay_victory);
        tvVictoryInfo = findViewById(R.id.victory_info);
        btnToMenu = findViewById(R.id.btn_to_menu);
        btnOnceMore = findViewById(R.id.btn_once_more);

        grid.setNumColumns(cols);
        adapter = new GridAdapter(this, cols, rows, gSize);
        grid.setAdapter(adapter);

        moves = 0;
        startTime = System.currentTimeMillis();
        int bestMoves = prefs.getInt("best_" + cols + "x" + rows + "_" + gSize, 0);
        tvBestLabel.setText("Рекорд: " + (bestMoves == 0 ? "-" : bestMoves));

        if (timerRunnable != null) timerHandler.removeCallbacks(timerRunnable);
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateUI();
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);

        grid.setOnItemClickListener((parent, view, pos, id) -> {
            if (adapter.onCardClicked(pos, () -> {})) {
                moves++;
                updateUI();
                if (adapter.isGameOver()) {
                    showVictory(cols, rows, gSize);
                }
            }
        });

        btnRestart.setOnClickListener(v -> startGame(cols, rows, gSize));
        btnToMenu.setOnClickListener(v -> showMenu());
        btnOnceMore.setOnClickListener(v -> startGame(cols, rows, gSize));
    }

    private void updateUI() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        int minutes = (int) (elapsed / 60);
        int seconds = (int) (elapsed % 60);
        
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        tvGroupsCount.setText(String.valueOf(adapter.getRemainingGroups()));
        tvMovesLabel.setText("Ходов: " + moves);
    }

    private void showVictory(int c, int r, int g) {
        timerHandler.removeCallbacks(timerRunnable);
        overlayVictory.setVisibility(View.VISIBLE);
        
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        int min = (int) (elapsed / 60);
        int sec = (int) (elapsed % 60);
        
        tvVictoryInfo.setText(String.format(Locale.getDefault(), "Время: %02d:%02d\nХодов: %d", min, sec, moves));
        
        String key = "best_" + c + "x" + r + "_" + g;
        int best = prefs.getInt(key, Integer.MAX_VALUE);
        if (moves < best) {
            prefs.edit().putInt(key, moves).apply();
        }
    }

    @Override
    protected void onDestroy() {
        if (timerRunnable != null) timerHandler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }
}
