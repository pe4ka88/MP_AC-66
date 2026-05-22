package com.example.labb7new7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryManager {

    private final AppDatabase dbHelper;

    public HistoryManager(Context context) {
        dbHelper = new AppDatabase(context);
    }

    public void addAction(String action) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("action", action);

        String time = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(new Date());
        values.put("time", time);

        db.insert("history", null, values);
        db.close();
    }

    public ArrayList<String> getHistory() {
        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT action, time FROM history ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            list.add(cursor.getString(0) + "\n" + cursor.getString(1));
        }

        cursor.close();
        db.close();

        return list;
    }
}
