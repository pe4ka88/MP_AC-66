package com.example.lab_8;


public class Item {
    private int id;
    private String title;
    private String body;

    public Item(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getId() {
        return id;
    }
}