package com.example.shopezepchukac66;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryManager {

    private static HashMap<String, ArrayList<ArrayList<Product>>> history = new HashMap<>();

    public static void addOrder(Context context, ArrayList<Product> products) {

        String user = UserSession.getUser(context);

        if (!history.containsKey(user)) {
            history.put(user, new ArrayList<>());
        }

        history.get(user).add(new ArrayList<>(products));
    }

    public static ArrayList<ArrayList<Product>> getOrders(Context context) {

        String user = UserSession.getUser(context);

        if (!history.containsKey(user)) {
            return new ArrayList<>();
        }

        return history.get(user);
    }
}