package com.example.lr4.model;

import java.io.Serializable;

public class Product implements Serializable {
    private final int id;
    private final String name;
    private final double price;
    private boolean checked;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.checked = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
