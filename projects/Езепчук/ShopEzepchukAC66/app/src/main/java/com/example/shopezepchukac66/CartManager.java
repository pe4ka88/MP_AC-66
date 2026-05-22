package com.example.shopezepchukac66;

import java.util.ArrayList;

public class CartManager {

    private static final ArrayList<Product> cart = new ArrayList<>();

    public static ArrayList<Product> getCart() {
        return cart;
    }

    // Добавить один товар
    public static void addProduct(Product product) {
        cart.add(product);
    }

    // Заменить всю корзину
    public static void setCart(ArrayList<Product> products) {
        cart.clear();
        cart.addAll(products);
    }

    public static void clearCart() {
        cart.clear();
    }
}
