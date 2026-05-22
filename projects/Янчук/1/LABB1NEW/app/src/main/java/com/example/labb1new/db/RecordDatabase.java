package com.example.labb1new.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "records.db";
    public static final int DB_VERSION = 1;

    public RecordDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE records (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "gridSize INTEGER," +
                        "mode TEXT," +
                        "time INTEGER," +
                        "moves INTEGER" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
