package com.example.a1lab;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView textViewPairsLeft;
    private Button buttonRestart;

    private ArrayList<Card> cards;
    private ArrayList<Integer> imageResources;
    private int firstSelectedPosition = -1;
    private int pairsFound = 0;
    private int totalPairs = 8;
    private boolean isWaiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        textViewPairsLeft = findViewById(R.id.textViewPairsLeft);
        buttonRestart = findViewById(R.id.buttonRestart);

        initGame();

        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
    }

    private void initGame() {
        setupImages();
        createCardDeck();
        cards = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            cards.add(new Card(imageResources.get(i), false));
            cards.add(new Card(imageResources.get(i), false));
        }
        Collections.shuffle(cards);
        pairsFound = 0;
        firstSelectedPosition = -1;
        isWaiting = false;
        updatePairsLeftText();
        setupGrid();
    }

    private void setupImages() {
        imageResources = new ArrayList<>();
        imageResources.add(R.drawable.ic_emoji_1);
        imageResources.add(R.drawable.ic_emoji_2);
        imageResources.add(R.drawable.ic_emoji_3);
        imageResources.add(R.drawable.ic_emoji_4);
        imageResources.add(R.drawable.ic_emoji_5);
        imageResources.add(R.drawable.ic_emoji_6);
        imageResources.add(R.drawable.ic_emoji_7);
        imageResources.add(R.drawable.ic_emoji_8);
    }

    private void createCardDeck() {
    }

    private void setupGrid() {
        gridLayout.removeAllViews();
        int cardSize = getResources().getDisplayMetrics().widthPixels / 4;

        for (int i = 0; i < cards.size(); i++) {
            CardButton cardButton = new CardButton(this, i);
            cardButton.setLayoutParams(new GridLayout.LayoutParams());
            cardButton.getLayoutParams().width = cardSize;
            cardButton.getLayoutParams().height = cardSize;
            cardButton.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
            cardButton.setPadding(16, 16, 16, 16);

            if (cards.get(i).isMatched()) {
                cardButton.setImageResource(android.R.color.transparent);
                cardButton.setEnabled(false);
                cardButton.setVisibility(View.INVISIBLE);
            } else if (cards.get(i).isRevealed()) {
                cardButton.setImageResource(cards.get(i).getImageResource());
            } else {
                cardButton.setImageResource(R.drawable.card_back);
            }

            final int position = i;
            cardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isWaiting && !cards.get(position).isMatched() && !cards.get(position).isRevealed()) {
                        revealCard(position);
                    }
                }
            });

            gridLayout.addView(cardButton);
        }
    }

    private void revealCard(int position) {
        cards.get(position).setRevealed(true);
        updateCardImage(position);

        if (firstSelectedPosition == -1) {
            firstSelectedPosition = position;
        } else {
            int firstRes = cards.get(firstSelectedPosition).getImageResource();
            int secondRes = cards.get(position).getImageResource();

            if (firstRes == secondRes && firstSelectedPosition != position) {
                cards.get(firstSelectedPosition).setMatched(true);
                cards.get(position).setMatched(true);
                pairsFound++;
                updatePairsLeftText();

                cards.get(firstSelectedPosition).setRevealed(false);
                cards.get(position).setRevealed(false);

                updateCardImage(firstSelectedPosition);
                updateCardImage(position);

                hideMatchedCards(firstSelectedPosition);
                hideMatchedCards(position);

                firstSelectedPosition = -1;

                if (pairsFound == totalPairs) {
                    Toast.makeText(this, "Поздравляем! Вы выиграли!", Toast.LENGTH_LONG).show();
                }
            } else {
                isWaiting = true;
                final int firstPos = firstSelectedPosition;
                final int secondPos = position;

                gridLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cards.get(firstPos).setRevealed(false);
                        cards.get(secondPos).setRevealed(false);
                        updateCardImage(firstPos);
                        updateCardImage(secondPos);
                        firstSelectedPosition = -1;
                        isWaiting = false;
                    }
                }, 1000);
            }
        }
    }

    private void updateCardImage(int position) {
        View view = gridLayout.getChildAt(position);
        if (view instanceof CardButton) {
            CardButton cardButton = (CardButton) view;
            if (cards.get(position).isMatched()) {
                cardButton.setImageResource(android.R.color.transparent);
                cardButton.setEnabled(false);
                cardButton.setVisibility(View.INVISIBLE);
            } else if (cards.get(position).isRevealed()) {
                cardButton.setImageResource(cards.get(position).getImageResource());
                cardButton.setVisibility(View.VISIBLE);
            } else {
                cardButton.setImageResource(R.drawable.card_back);
                cardButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideMatchedCards(int position) {
        View view = gridLayout.getChildAt(position);
        if (view instanceof CardButton) {
            CardButton cardButton = (CardButton) view;
            cardButton.setVisibility(View.INVISIBLE);
            cardButton.setEnabled(false);
        }
    }

    private void updatePairsLeftText() {
        int left = totalPairs - pairsFound;
        textViewPairsLeft.setText("Осталось пар: " + left);
    }

    private void restartGame() {
        Collections.shuffle(cards);
        for (Card card : cards) {
            card.setMatched(false);
            card.setRevealed(false);
        }
        pairsFound = 0;
        firstSelectedPosition = -1;
        isWaiting = false;
        updatePairsLeftText();
        setupGrid();
    }
}