package com.example.memorygame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

public class GameHardActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_hard);

        gridLayout = findViewById(R.id.gridLayout);
        pairsTextView = findViewById(R.id.pairsTextView);
        movesTextView = findViewById(R.id.movesTextView);
        restartButton = findViewById(R.id.restartButton);
        backButton = findViewById(R.id.backButton);

        gridLayout.setColumnCount(6);
        gridLayout.setRowCount(4);

        restartButton.setOnClickListener(v -> restartGame());
        backButton.setOnClickListener(v -> finish());

        initializeGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isChecking = false;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected int getTotalPairs() {
        return 12;
    }

    @Override
    protected int[] getCardColors() {
        return new int[] {
                0xFFE74C3C, 0xFFE74C3C,
                0xFF2E86C1, 0xFF2E86C1,
                0xFF27AE60, 0xFF27AE60,
                0xFFF39C12, 0xFFF39C12,
                0xFF8E44AD, 0xFF8E44AD,
                0xFF16A085, 0xFF16A085,
                0xFFF1C40F, 0xFFF1C40F,
                0xFF95A5A6, 0xFF95A5A6,
                0xFFE91E63, 0xFFE91E63,
                0xFF3F51B5, 0xFF3F51B5,
                0xFF009688, 0xFF009688,
                0xFFFF5722, 0xFFFF5722
        };
    }
}