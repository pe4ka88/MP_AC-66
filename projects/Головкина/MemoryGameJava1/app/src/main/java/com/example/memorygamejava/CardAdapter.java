package com.example.memorygamejava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.List;

public class CardAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> cardValues;
    private int fieldSize;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public CardAdapter(Context context, List<Integer> cardValues, int fieldSize, OnCardClickListener listener) {
        this.context = context;
        this.cardValues = cardValues;
        this.fieldSize = fieldSize;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return cardValues.size();
    }

    @Override
    public Object getItem(int position) {
        return cardValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        }

        FrameLayout cardLayout = convertView.findViewById(R.id.cardLayout);
        ImageView cardBack = convertView.findViewById(R.id.cardBack);
        ImageView cardFront = convertView.findViewById(R.id.cardFront);

        // Размер карточки
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int cardSize = (screenWidth - 10 * (fieldSize + 1)) / fieldSize;
        cardLayout.getLayoutParams().width = cardSize;
        cardLayout.getLayoutParams().height = cardSize;

        // Картинка
        cardFront.setImageResource(cardValues.get(position));

        // Сброс состояния
        cardBack.setVisibility(View.VISIBLE);
        cardFront.setVisibility(View.INVISIBLE);

        // Клик
        cardLayout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(position);
            }
        });

        return convertView;
    }
}