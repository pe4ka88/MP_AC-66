package com.example.labb4new4;

import java.io.Serializable;

public class Product implements Serializable {
    public int id;
    public String name;
    public double price;
    public boolean checked;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.checked = false;
    }
}
