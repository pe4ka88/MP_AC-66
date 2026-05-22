package com.example.lab4;


import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private boolean isSelected;
    private String imageUrl; // для бонуса с картинками

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isSelected = false;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
