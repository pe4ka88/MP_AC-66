package com.example.myapplication;

import java.io.Serializable;

public class Product implements Serializable {
    public int id;
    public String name;
    public String price;
    public boolean box;

    public Product(int id, String name, String price, boolean box) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.box = box;
    }
}
