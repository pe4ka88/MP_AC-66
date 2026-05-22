package com.example.shopezepchukac66;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    public int id;
    public String title;
    public double price;
    public String description;
    public String brand;
    public double rating;
    public String thumbnail;
    public List<String> images;
    public boolean checked = false;

    public Product() {}

    public Product(int id, String title, double price, String description,
                   String brand, double rating, String thumbnail, List<String> images) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.rating = rating;
        this.thumbnail = thumbnail;
        this.images = images;
    }
    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
