package com.example.laba1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.VH> {

    public interface OnCardClick {
        void onClick(int pos);
    }

    private final List<Card> cards;
    private final OnCardClick onCardClick;

    public Integer openedPos = null;

    public CardsAdapter(List<Card> cards, OnCardClick onCardClick) {
        this.cards = cards;
        this.onCardClick = onCardClick;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgCard);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Card card = cards.get(position);

        if (card.isRemoved) {
            holder.img.setAlpha(0f);
            holder.itemView.setClickable(false);
            return;
        } else {
            holder.img.setAlpha(1f);
            holder.itemView.setClickable(true);
        }

        boolean isOpened = (openedPos != null && openedPos == position);

        holder.img.setImageResource(isOpened ? card.imageRes : R.drawable.card_back);

        holder.itemView.setOnClickListener(v -> onCardClick.onClick(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
