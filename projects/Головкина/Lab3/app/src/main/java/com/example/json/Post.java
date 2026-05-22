package com.example.json;

import java.io.Serializable;
public class Post implements Serializable {
    private int id;
    private int userId;
    private String title;
    private String body;
    private String imageUrl; // для бонуса с картинками

    // Для JSONPlaceholder API
    public Post(int id, int userId, String title, String body) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // Геттеры
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}