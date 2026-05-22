package com.example.shop.order;

public class Order {

    public int id;
    public String userEmail;
    public String title;
    public double price;
    public long date;

    public Order(int id, String userEmail, String title, double price, long date) {
        this.id = id;
        this.userEmail = userEmail;
        this.title = title;
        this.price = price;
        this.date = date;
    }

    public Order(String title, double price) {
        this.title = title;
        this.price = price;
    }
}