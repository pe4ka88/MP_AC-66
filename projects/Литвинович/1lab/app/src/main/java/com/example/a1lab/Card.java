package com.example.a1lab;

public class Card {
    private int imageResource;
    private boolean isMatched;
    private boolean isRevealed;

    public Card(int imageResource, boolean isMatched) {
        this.imageResource = imageResource;
        this.isMatched = isMatched;
        this.isRevealed = false;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
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
}