package com.example.shopezepchukac66;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private UserDatabase dbHelper;
    private Context context;   // ← добавили

    public OrderRepository(Context context) {
        this.context = context;   // ← сохраняем
        dbHelper = new UserDatabase(context);
    }

    // Сохранение заказа
    public void saveOrder(String title, double price) {

        String user = UserSession.getUser(context);
        if (user == null) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_email", user);
        values.put("title", title);
        values.put("price", price);
        values.put("date", System.currentTimeMillis());

        db.insert("orders", null, values);
    }

    // Получение заказов пользователя
    public List<Order> getOrders() {

        List<Order> list = new ArrayList<>();

        String user = UserSession.getUser(context);
        if (user == null) return list;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM orders WHERE user_email=? ORDER BY id DESC",
                new String[]{user}
        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));

            list.add(new Order(id, email, title, price, date));
        }

        cursor.close();

        return list;
    }
}