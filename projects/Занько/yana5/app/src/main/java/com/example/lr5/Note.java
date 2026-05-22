package com.example.lr5;

public class Note {
    private final int id;
    private final String description;

    public Note(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
