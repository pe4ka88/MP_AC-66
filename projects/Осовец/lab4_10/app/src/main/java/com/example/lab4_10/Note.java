package com.example.lab4_10;

public class Note {
    private int id;
    private String description;

    public Note() {}

    public Note(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
