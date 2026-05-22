package com.example.lab_9;

import java.io.Serializable;

public class Product implements Serializable {
    public String name;
    public int price;
    public int image;
    public boolean box;

    public Product(String name, int price, int image, boolean box) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.box = box;
    }
}
// Force update
