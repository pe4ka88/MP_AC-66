package com.example.third9lab.models;

import java.io.Serializable;

public class Good implements Serializable {
    private int id;
    private String name;
    private double price;
    private boolean checked;

    public Good(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.checked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
