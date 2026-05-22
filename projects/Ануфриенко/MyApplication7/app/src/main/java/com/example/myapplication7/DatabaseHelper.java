package com.example.myapplication7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper — хранилище истории действий пользователя.
 * Таблица history: id, activity_type, description, timestamp, latitude, longitude
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "media_history.db";
    private static final int    DB_VERSION = 1;
    static final String TABLE  = "history";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id            INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "activity_type TEXT    NOT NULL, " +
                "description   TEXT, " +
                "timestamp     DATETIME DEFAULT (datetime('now','localtime')), " +
                "latitude      REAL    DEFAULT 0.0, " +
                "longitude     REAL    DEFAULT 0.0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /** Добавить запись в историю. */
    public void addRecord(String activityType, String description,
                          double latitude, double longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("activity_type", activityType);
        cv.put("description",   description);
        cv.put("latitude",      latitude);
        cv.put("longitude",     longitude);
        db.insert(TABLE, null, cv);
        db.close();
    }

    /** Вернуть все записи, новые первыми. */
    public List<HistoryRecord> getAllRecords() {
        List<HistoryRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, null, null, null, null, "id DESC");
        if (c.moveToFirst()) {
            do {
                list.add(new HistoryRecord(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("activity_type")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        c.getString(c.getColumnIndexOrThrow("timestamp")),
                        c.getDouble(c.getColumnIndexOrThrow("latitude")),
                        c.getDouble(c.getColumnIndexOrThrow("longitude"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    /** Удалить всю историю. */
    public void clearHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }

    /** Количество записей. */
    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }
}