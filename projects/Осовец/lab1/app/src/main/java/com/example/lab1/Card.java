package com.example.lab1;

public class Card {
    private int id;
    private int imageResourceId;
    private boolean isMatched;
    private boolean isRevealed;
    private int groupSize;

    public Card(int id, int imageResourceId) {
        this.id = id;
        this.imageResourceId = imageResourceId;
        this.isMatched = false;
        this.isRevealed = false;
        this.groupSize = 2;
    }

    public Card(int id, int imageResourceId, int groupSize) {
        this.id = id;
        this.imageResourceId = imageResourceId;
        this.isMatched = false;
        this.isRevealed = false;
        this.groupSize = groupSize;
    }

    public int getId() {
        return id;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }
}
