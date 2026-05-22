package com.example.geolocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "locations.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE locations (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "ts INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS locations");
        onCreate(db);
    }

    public void insert(double lat, double lon) {
        ContentValues cv = new ContentValues();
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("ts", System.currentTimeMillis());
        getWritableDatabase().insert("locations", null, cv);
    }

    public List<LocationPoint> getAll() {
        List<LocationPoint> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT _id, lat, lon, ts FROM locations ORDER BY ts ASC", null);
        while (c.moveToNext()) {
            LocationPoint p = new LocationPoint();
            p.id  = c.getLong(0);
            p.lat = c.getDouble(1);
            p.lon = c.getDouble(2);
            p.ts  = c.getLong(3);
            list.add(p);
        }
        c.close();
        return list;
    }

    public void deleteById(long id) {
        getWritableDatabase().delete("locations", "_id=?", new String[]{String.valueOf(id)});
    }

    public static class LocationPoint {
        public long id;
        public double lat, lon;
        public long ts;

        public String getTime() {
            return new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()).format(new Date(ts));
        }
    }
}
