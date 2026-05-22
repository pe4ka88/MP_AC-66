package com.example.shop.product;

import java.util.List;

public class Product {

    public int id;
    public String title;
    public String description;
    public double price;
    public double rating;
    public String category;
    public String thumbnail;
    public List<String> images;

    // используется приложением
    public boolean checked = false;
}