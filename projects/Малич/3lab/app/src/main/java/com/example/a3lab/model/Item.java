package com.example.a3lab.model;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("category")
    private String category;

    @SerializedName("rating")
    private double rating;

    // Конструктор по умолчанию (нужен для Gson)
    public Item() {
    }

    public Item(int id, String title, String description, String imageUrl, String category, double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.rating = rating;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }

    // Setters (опционально, но могут пригодиться)
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setRating(double rating) { this.rating = rating; }
}