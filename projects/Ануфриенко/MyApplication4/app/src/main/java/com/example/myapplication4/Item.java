package com.example.myapplication4;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String name;
    private double price;
    private boolean checked;

    public Item(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId()                { return id; }
    public String getName()           { return name; }
    public double getPrice()          { return price; }
    public boolean isChecked()        { return checked; }
    public void setChecked(boolean c) { this.checked = c; }
}
