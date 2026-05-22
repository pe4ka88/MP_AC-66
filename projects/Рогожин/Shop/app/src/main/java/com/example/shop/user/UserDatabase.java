package com.example.shop.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 2;

    public UserDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "email TEXT UNIQUE," +
                        "password TEXT," +
                        "balance REAL DEFAULT 0," +
                        "address TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE orders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_email TEXT," +
                        "title TEXT," +
                        "price REAL," +
                        "date TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS orders (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "user_email TEXT," +
                            "title TEXT," +
                            "price REAL," +
                            "date INTEGER" +
                            ")"
            );
        }
    }
}
