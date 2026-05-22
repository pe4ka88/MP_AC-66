package com.example.test1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GridAdapter extends BaseAdapter {

    private final Context context;
    private final int cols;
    private final int rows;
    private final int groupSize;

    private final ArrayList<String> cards = new ArrayList<>();
    private final ArrayList<CardState> states = new ArrayList<>();
    
    private final HashMap<String, Integer> cardColors = new HashMap<>();

    private final ArrayList<Integer> currentSelection = new ArrayList<>();
    private boolean isLocked = false;

    private enum CardState { CLOSED, OPEN, REMOVED }

    public GridAdapter(Context ctx, int c, int r, int gSize) {
        context = ctx;
        cols = c;
        rows = r;
        groupSize = gSize;

        initCards();
        initStates();
        generateColors();
    }

    private void initCards() {
        cards.clear();
        int total = cols * rows;
        int groups = total / groupSize;

        for (int i = 1; i <= groups; i++) {
            String name = "pic" + i;
            for (int k = 0; k < groupSize; k++) {
                cards.add(name);
            }
        }

        while (cards.size() < total) {
            cards.add("none");
        }

        Collections.shuffle(cards);
    }

    private void initStates() {
        states.clear();
        for (int i = 0; i < cols * rows; i++) {
            states.add(CardState.CLOSED);
        }
    }

    private void generateColors() {
        cardColors.clear();
        int[] pastelPalette = {
            Color.parseColor("#FFD1DC"), Color.parseColor("#B2EBF2"),
            Color.parseColor("#C8E6C9"), Color.parseColor("#FFF9C4"),
            Color.parseColor("#F8BBD0"), Color.parseColor("#E1BEE7"),
            Color.parseColor("#D1C4E9"), Color.parseColor("#FFCCBC"),
            Color.parseColor("#CFD8DC"), Color.parseColor("#DCEDC8")
        };

        int colorIndex = 0;
        for (String card : cards) {
            if (!card.equals("none") && !cardColors.containsKey(card)) {
                cardColors.put(card, pastelPalette[colorIndex % pastelPalette.length]);
                colorIndex++;
            }
        }
    }

    @Override
    public int getCount() {
        return cols * rows;
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
    public View getView(int pos, View convertView, ViewGroup parent) {
        ImageView iv;
        if (convertView == null) {
            iv = new ImageView(context);
            float density = context.getResources().getDisplayMetrics().density;
            int spacing = (int) (8 * density);
            int availableWidth = parent.getWidth();
            if (availableWidth <= 0) {
                availableWidth = context.getResources().getDisplayMetrics().widthPixels - (int)(32 * density);
            }
            int width = (availableWidth - (cols - 1) * spacing) / cols;
            
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width));
            iv.setPadding(8, 8, 8, 8);
        } else {
            iv = (ImageView) convertView;
        }

        CardState st = states.get(pos);
        String cardId = cards.get(pos);

        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (st == CardState.REMOVED) {
            iv.setImageResource(0); 
            Integer color = cardColors.get(cardId);
            iv.setImageDrawable(new ColorDrawable(color != null ? color : Color.LTGRAY));
            iv.setBackgroundColor(Color.TRANSPARENT);
        } else {
            iv.setImageDrawable(null);
            iv.setBackgroundColor(Color.TRANSPARENT);
            if (st == CardState.OPEN) {
                int resId = context.getResources().getIdentifier(cardId, "drawable", context.getPackageName());
                iv.setImageResource(resId != 0 ? resId : R.drawable.close);
            } else {
                iv.setImageResource(R.drawable.close);
            }
        }

        return iv;
    }

    public boolean onCardClicked(int pos, Runnable onMismatch) {
        if (isLocked) return false;
        if (states.get(pos) != CardState.CLOSED) return false;
        if (cards.get(pos).equals("none")) return false;

        states.set(pos, CardState.OPEN);
        currentSelection.add(pos);
        notifyDataSetChanged();

        if (currentSelection.size() < groupSize) {
            return true;
        }

        isLocked = true;
        String firstVal = cards.get(currentSelection.get(0));
        boolean allMatch = true;
        for (int p : currentSelection) {
            if (!cards.get(p).equals(firstVal)) {
                allMatch = false;
                break;
            }
        }

        if (allMatch) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                for (int p : currentSelection) {
                    states.set(p, CardState.REMOVED);
                }
                currentSelection.clear();
                isLocked = false;
                notifyDataSetChanged();
            }, 300);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                for (int p : currentSelection) {
                    states.set(p, CardState.CLOSED);
                }
                currentSelection.clear();
                isLocked = false;
                notifyDataSetChanged();
                if (onMismatch != null) onMismatch.run();
            }, 800);
        }
        return true;
    }

    public boolean isGameOver() {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i) == CardState.CLOSED && !cards.get(i).equals("none")) return false;
        }
        return true;
    }

    public int getRemainingGroups() {
        java.util.HashSet<String> closedGroups = new java.util.HashSet<>();
        for (int i = 0; i < cards.size(); i++) {
            if (states.get(i) == CardState.CLOSED && !cards.get(i).equals("none")) {
                closedGroups.add(cards.get(i));
            }
        }
        return closedGroups.size();
    }
}
