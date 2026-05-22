package com.example.a1lab;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private Button restartButton;
    private ArrayList<Integer> cards;
    private ArrayList<ImageView> imageViews;
    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private boolean canClick = true;
    private int matchedPairs = 0;
    private int totalPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        restartButton = findViewById(R.id.restartButton);

        setupGame();

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    private void setupGame() {
        cards = new ArrayList<>();
        imageViews = new ArrayList<>();
        gridLayout.removeAllViews();

        int rows = 4;
        int cols = 4;
        totalPairs = (rows * cols) / 2;
        matchedPairs = 0;

        gridLayout.setColumnCount(cols);
        gridLayout.setRowCount(rows);

        for (int i = 0; i < totalPairs; i++) {
            cards.add(i);
            cards.add(i);
        }
        Collections.shuffle(cards);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int padding = 32;
        int availableWidth = screenWidth - (2 * padding);
        int cardSize = availableWidth / cols;

        for (int i = 0; i < rows * cols; i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(android.R.color.transparent);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardSize;
            params.height = cardSize;
            params.setMargins(2, 2, 2, 2);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(R.drawable.card_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setTag(i);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canClick) return;

                    int position = (int) v.getTag();
                    if (position == firstCardIndex) return;

                    handleCardClick(position);
                }
            });

            gridLayout.addView(imageView);
            imageViews.add(imageView);
        }
    }

    private void handleCardClick(int position) {
        ImageView imageView = imageViews.get(position);
        imageView.setImageResource(getCardResource(cards.get(position)));

        if (firstCardIndex == -1) {
            firstCardIndex = position;
        } else {
            secondCardIndex = position;
            canClick = false;

            checkForMatch();
        }
    }

    private void checkForMatch() {
        if (cards.get(firstCardIndex).equals(cards.get(secondCardIndex))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageViews.get(firstCardIndex).setVisibility(View.INVISIBLE);
                    imageViews.get(secondCardIndex).setVisibility(View.INVISIBLE);
                    matchedPairs++;
                    resetSelections();

                    if (matchedPairs == totalPairs) {
                        Toast.makeText(MainActivity.this, "Игра завершена (Невар В.А.)!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageViews.get(firstCardIndex).setImageResource(android.R.color.transparent);
                    imageViews.get(secondCardIndex).setImageResource(android.R.color.transparent);
                    resetSelections();
                }
            }, 1000);
        }
    }

    private void resetSelections() {
        firstCardIndex = -1;
        secondCardIndex = -1;
        canClick = true;
    }

    private int getCardResource(int cardValue) {
        switch (cardValue) {
            case 0: return R.drawable.card1;
            case 1: return R.drawable.card2;
            case 2: return R.drawable.card3;
            case 3: return R.drawable.card4;
            case 4: return R.drawable.card5;
            case 5: return R.drawable.card6;
            case 6: return R.drawable.card7;
            case 7: return R.drawable.card8;
            default: return R.drawable.card1;
        }
    }

    private void resetGame() {
        setupGame();
    }
}