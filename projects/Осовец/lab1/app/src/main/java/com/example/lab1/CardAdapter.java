package com.example.lab1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class CardAdapter extends BaseAdapter {
    private Context context;
    private List<Card> cards;
    private int cardBackResource;
    private int numColumns = 4;

    public CardAdapter(Context context, List<Card> cards, int cardBackResource) {
        this.context = context;
        this.cards = cards;
        this.cardBackResource = cardBackResource;
    }
    
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        
        if (convertView == null) {
            imageView = new ImageView(context);
            // Рассчитываем размер карточки динамически
            int parentWidth = parent.getWidth();
            if (parentWidth == 0) parentWidth = 800; // Fallback
            int spacing = 8; // Отступы
            int size = (parentWidth - (numColumns + 1) * spacing) / numColumns;
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        Card card = cards.get(position);
        
        // Показываем картинку если карта открыта или найдена
        if (card.isRevealed() || card.isMatched()) {
            imageView.setImageResource(card.getImageResourceId());
            if (card.isMatched()) {
                imageView.setAlpha(0.4f);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setAlpha(1.0f);
            }
        } else {
            // Показываем рубашку карты
            imageView.setImageResource(cardBackResource);
            imageView.setAlpha(1.0f);
        }

        return imageView;
    }

    public void updateCards(List<Card> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }
}
