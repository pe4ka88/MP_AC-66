package com.example.labb1new.models;

public class Card {
    private int id;
    private int imageRes;
    private boolean open;
    private boolean matched;

    public Card(int id, int imageRes) {
        this.id = id;
        this.imageRes = imageRes;
        this.open = false;
        this.matched = false;
    }

    public int getId() { return id; }
    public int getImageRes() { return imageRes; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    public boolean isMatched() { return matched; }
    public void setMatched(boolean matched) { this.matched = matched; }
}

