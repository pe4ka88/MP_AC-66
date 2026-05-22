package com.example.labb8new8;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeoDatabase extends SQLiteOpenHelper {

    public GeoDatabase(Context context) {
        super(context, "geo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE locations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "lat REAL, " +
                "lng REAL, " +
                "time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
