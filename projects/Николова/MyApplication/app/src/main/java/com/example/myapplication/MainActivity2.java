package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements CardAdapter.OnGameEventListener {

    private RecyclerView rvBoard;
    private TextView tvSteps, tvBestResult;
    private int steps = 0;
    private int matchCount = 2;
    private int boardSize = 16;
    private int columns = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        rvBoard = findViewById(R.id.rvBoard);
        tvSteps = findViewById(R.id.tvSteps);
        tvBestResult = findViewById(R.id.tvBestResult);
        RadioGroup rgMode = findViewById(R.id.rgMode);
        Button btnRestart = findViewById(R.id.btnRestart);

        loadBestScore();

        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            matchCount = (checkedId == R.id.rbPairs) ? 2 : 3;
            startGame();
        });

        btnRestart.setOnClickListener(v -> startGame());

        startGame();

        RadioGroup rgSize = findViewById(R.id.rgSize);

        rgSize.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb4x4) {
                boardSize = 16;
                columns = 4;
            } else if (checkedId == R.id.rb6x6) {
                boardSize = 36;
                columns = 6;
            } else if (checkedId == R.id.rb8x8) {
                boardSize = 64;
                columns = 8;
            }
            startGame();
        });
    }

    private void startGame() {
        steps = 0;
        updateUI();

        List<Integer> cards = generateCards(matchCount);
        rvBoard.setLayoutManager(new GridLayoutManager(this, columns));
        rvBoard.setAdapter(new CardAdapter(cards, matchCount, columns, this));
    }

    private List<Integer> generateCards(int match) {
        List<Integer> list = new ArrayList<>();

        int[] images = {
                R.drawable.card1, R.drawable.card2, R.drawable.card3, R.drawable.card4,
                R.drawable.card5, R.drawable.card6, R.drawable.card7, R.drawable.card8
        };

        int uniqueImagesNeeded = boardSize / match;

        for (int i = 0; i < uniqueImagesNeeded; i++) {
            for (int j = 0; j < match; j++) {
                list.add(images[i % images.length]);
            }
        }

        Collections.shuffle(list);
        return list;
    }

    @Override
    public void onStepMade() {
        steps++;
        updateUI();
    }

    @Override
    public void onAllMatched() {
        saveScore();
    }

    private void updateUI() {
        tvSteps.setText("Шаги: " + steps);
    }

    private void saveScore() {
        SharedPreferences prefs = getSharedPreferences("Game", MODE_PRIVATE);
        int best = prefs.getInt("best_" + matchCount, Integer.MAX_VALUE);
        if (steps < best) {
            prefs.edit().putInt("best_" + matchCount, steps).apply();
            loadBestScore();
        }
    }

    private void loadBestScore() {
        SharedPreferences prefs = getSharedPreferences("Game", MODE_PRIVATE);
        int best = prefs.getInt("best_" + matchCount, 0);
        tvBestResult.setText("Рекорд: " + (best == 0 ? "--" : best));
    }
}