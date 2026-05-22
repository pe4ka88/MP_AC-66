package com.example.myapplication;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Integer> cards;
    private final int matchCount;
    private final int columns;
    private final List<CardViewHolder> selectedCards = new ArrayList<>();
    private final OnGameEventListener listener;

    private int matchedPairsCount = 0;
    private boolean isProcessing = false;

    public interface OnGameEventListener {
        void onStepMade();
        void onAllMatched();
    }

    public CardAdapter(List<Integer> cards, int matchCount, int columns, OnGameEventListener listener) {
        this.cards = cards;
        this.matchCount = matchCount;
        this.columns = columns;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);

        int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        int cellSize = (screenWidth / columns) - 10;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = cellSize;
        params.height = cellSize;
        view.setLayoutParams(params);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCard;
        int imageRes;
        boolean isMatched = false;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCard = itemView.findViewById(R.id.ivCard);

            itemView.setOnClickListener(v -> {
                if (isProcessing || isMatched || selectedCards.contains(this)) return;

                ivCard.setImageResource(imageRes);
                selectedCards.add(this);

                if (selectedCards.size() == matchCount) {
                    isProcessing = true;
                    listener.onStepMade();
                    checkMatch();
                }
            });
        }

        void bind(int res) {
            imageRes = res;
            isMatched = false;
            itemView.setVisibility(View.VISIBLE);
            ivCard.setImageResource(R.drawable.card_back);
        }
    }

    private void checkMatch() {
        new Handler().postDelayed(() -> {
            if (selectedCards.isEmpty()) return;

            boolean allMatch = true;
            int firstRes = selectedCards.get(0).imageRes;

            for (CardViewHolder holder : selectedCards) {
                if (holder.imageRes != firstRes) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                for (CardViewHolder holder : selectedCards) {
                    holder.isMatched = true;
                    holder.itemView.setVisibility(View.INVISIBLE);
                    matchedPairsCount++;
                }

                if (matchedPairsCount == cards.size()) {
                    listener.onAllMatched();
                }
            } else {
                for (CardViewHolder holder : selectedCards) {
                    holder.ivCard.setImageResource(R.drawable.card_back);
                }
            }

            selectedCards.clear();
            isProcessing = false;
        }, 800);
    }
}