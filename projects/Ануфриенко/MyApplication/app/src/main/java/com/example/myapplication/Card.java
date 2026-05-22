package com.example.myapplication;

public class Card {

    private final int imageResId;
    private final int groupSize;
    private boolean matched;
    private final boolean isJoker;

    public Card(int imageResId, boolean isJoker, int groupSize) {
        this.imageResId = imageResId;
        this.isJoker = isJoker;
        this.groupSize = groupSize;
        this.matched = false;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
    public boolean isJoker() {
        return isJoker;
    }
}