package com.example.geo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "locations.db";
    private static final int DB_VERSION = 1;

    public LocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        // позволяет читать и писать одновременно
        db.enableWriteAheadLogging();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE locations (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "latitude REAL," +
                        "longitude REAL," +
                        "timestamp INTEGER)"
        );
    }
    public boolean hasData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM locations", null);

        boolean result = false;

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0) > 0;
        }

        cursor.close();
        return result;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // ------------------------------------------------
    // INSERT
    // ------------------------------------------------

    public void insert(double lat, double lon) {

        insert(lat, lon, System.currentTimeMillis());

    }

    public void insert(double lat, double lon, long timestamp) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitude", lat);
        values.put("longitude", lon);
        values.put("timestamp", timestamp);

        db.insert("locations", null, values);
    }

    // ------------------------------------------------
    // CLEAR
    // ------------------------------------------------

    public void clearAll() {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            db.delete("locations", null, null);

            db.execSQL("DELETE FROM sqlite_sequence WHERE name='locations'");

            db.setTransactionSuccessful();

        } finally {

            db.endTransaction();

        }
    }

    // ------------------------------------------------
    // CHECK EMPTY
    // ------------------------------------------------

    public boolean isEmpty() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM locations",
                null
        );

        try {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 0;
            }

        } finally {

            cursor.close();

        }

        return true;
    }

    // ------------------------------------------------
    // GET BY PERIOD
    // ------------------------------------------------

    public List<LocationPoint> getByPeriod(long from, long to) {

        SQLiteDatabase db = getReadableDatabase();

        List<LocationPoint> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT latitude, longitude, timestamp " +
                        "FROM locations WHERE timestamp BETWEEN ? AND ?",
                new String[]{
                        String.valueOf(from),
                        String.valueOf(to)
                }
        );

        try {

            while (cursor.moveToNext()) {

                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                long time = cursor.getLong(2);

                list.add(new LocationPoint(lat, lon, time));
            }

        } finally {

            cursor.close();

        }

        return list;
    }

    // ------------------------------------------------
    // GET ALL
    // ------------------------------------------------

    public List<LocationPoint> getAll() {

        SQLiteDatabase db = getReadableDatabase();

        List<LocationPoint> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT latitude, longitude, timestamp FROM locations ORDER BY timestamp",
                null
        );

        try {

            while (cursor.moveToNext()) {

                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                long time = cursor.getLong(2);

                list.add(new LocationPoint(lat, lon, time));

            }

        } finally {

            cursor.close();

        }

        return list;
    }
}