package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

public abstract class GameActivity extends AppCompatActivity {

    protected GridLayout gridLayout;
    protected TextView pairsTextView;
    protected TextView movesTextView;
    protected Button restartButton;
    protected Button backButton;

    protected int moves = 0;
    protected int matchedPairs = 0;
    protected int totalPairs;

    protected Button[] cards;
    protected int[] cardColors;
    protected boolean[] cardRevealed;
    protected boolean[] cardMatched;

    protected int firstSelected = -1;
    protected int secondSelected = -1;
    protected boolean isChecking = false;

    protected Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            moves = savedInstanceState.getInt("moves", 0);
            matchedPairs = savedInstanceState.getInt("matchedPairs", 0);
            firstSelected = savedInstanceState.getInt("firstSelected", -1);
            secondSelected = savedInstanceState.getInt("secondSelected", -1);
            isChecking = savedInstanceState.getBoolean("isChecking", false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("moves", moves);
        outState.putInt("matchedPairs", matchedPairs);
        outState.putInt("firstSelected", firstSelected);
        outState.putInt("secondSelected", secondSelected);
        outState.putBoolean("isChecking", isChecking);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    protected abstract int getTotalPairs();
    protected abstract int[] getCardColors();

    protected void initializeGame() {
        totalPairs = getTotalPairs();
        int[] colors = getCardColors();

        if (colors == null) return;

        cardColors = colors.clone();
        cardRevealed = new boolean[cardColors.length];
        cardMatched = new boolean[cardColors.length];

        moves = 0;
        matchedPairs = 0;
        firstSelected = -1;
        secondSelected = -1;
        isChecking = false;

        createCards();
        updateUI();
    }

    protected void createCards() {
        if (gridLayout == null) return;

        gridLayout.removeAllViews();

        ArrayList<Integer> colorList = new ArrayList<>();
        for (int color : cardColors) {
            colorList.add(color);
        }
        Collections.shuffle(colorList);

        for (int i = 0; i < cardColors.length; i++) {
            cardColors[i] = colorList.get(i);
        }

        int columns = gridLayout.getColumnCount();
        int totalCards = cardColors.length;
        cards = new Button[totalCards];

        for (int i = 0; i < totalCards; i++) {
            Button card = new Button(this);

            GridLayout.Spec rowSpec = GridLayout.spec(i / columns, 1f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % columns, 1f);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 8, 8, 8);

            card.setLayoutParams(params);
            card.setBackgroundColor(0xFF95A5A6);
            card.setText("?");
            card.setTextSize(18);

            final int index = i;
            card.setOnClickListener(v -> {
                if (!isChecking && !cardMatched[index]) {
                    onCardClick(index);
                }
            });

            gridLayout.addView(card);
            cards[i] = card;
        }
    }

    protected void onCardClick(int index) {
        if (cardRevealed[index] || cardMatched[index]) {
            return;
        }

        revealCard(index);

        if (firstSelected == -1) {
            firstSelected = index;
        } else if (secondSelected == -1 && index != firstSelected) {
            secondSelected = index;

            moves++;
            updateUI();

            isChecking = true;

            if (handler != null) {
                handler.postDelayed(this::checkMatch, 800);
            }
        }
    }

    protected void revealCard(int index) {
        if (cards[index] != null) {
            cards[index].setBackgroundColor(cardColors[index]);
            cards[index].setText("");
            cardRevealed[index] = true;
        }
    }

    protected void hideCard(int index) {
        if (cards[index] != null) {
            cards[index].setBackgroundColor(0xFF95A5A6);
            cards[index].setText("?");
            cardRevealed[index] = false;
        }
    }

    protected void checkMatch() {
        if (firstSelected != -1 && secondSelected != -1) {
            if (cardColors[firstSelected] == cardColors[secondSelected]) {
                cards[firstSelected].setVisibility(View.INVISIBLE);
                cards[secondSelected].setVisibility(View.INVISIBLE);

                cardMatched[firstSelected] = true;
                cardMatched[secondSelected] = true;

                matchedPairs++;

                if (matchedPairs == totalPairs) {
                    showWinMessage();
                }
            } else {
                hideCard(firstSelected);
                hideCard(secondSelected);
            }

            firstSelected = -1;
            secondSelected = -1;
            isChecking = false;
            updateUI();
        }
    }

    protected void updateUI() {
        if (pairsTextView != null) {
            pairsTextView.setText("Пар: " + matchedPairs + "/" + totalPairs);
        }
        if (movesTextView != null) {
            movesTextView.setText("Ходов: " + moves);
        }
    }

    protected void showWinMessage() {
        runOnUiThread(() -> {
            Toast.makeText(this,
                    "ПОБЕДА! Все " + totalPairs + " пар найдены за " + moves + " ходов!",
                    Toast.LENGTH_LONG).show();
        });
    }

    protected void restartGame() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        firstSelected = -1;
        secondSelected = -1;
        isChecking = false;

        if (cards != null) {
            for (int i = 0; i < cards.length; i++) {
                if (cards[i] != null) {
                    cards[i].setVisibility(View.VISIBLE);
                    hideCard(i);
                    cardMatched[i] = false;
                    cardRevealed[i] = false;
                }
            }
        }

        ArrayList<Integer> colorList = new ArrayList<>();
        for (int color : cardColors) {
            colorList.add(color);
        }
        Collections.shuffle(colorList);

        for (int i = 0; i < cardColors.length; i++) {
            cardColors[i] = colorList.get(i);
        }

        moves = 0;
        matchedPairs = 0;
        updateUI();
    }
}