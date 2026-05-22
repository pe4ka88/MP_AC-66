package com.example.labb8new8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class GeoManager {

    private final GeoDatabase dbHelper;

    public GeoManager(Context context) {
        dbHelper = new GeoDatabase(context);
    }

    public void addLocation(double lat, double lng) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("lat", lat);
        values.put("lng", lng);
        values.put("time", System.currentTimeMillis());

        db.insert("locations", null, values);
        db.close();
    }

    public ArrayList<LocationPoint> getAllPoints() {
        ArrayList<LocationPoint> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT lat, lng, time FROM locations", null);

        while (cursor.moveToNext()) {
            double lat = cursor.getDouble(0);
            double lng = cursor.getDouble(1);
            long time = cursor.getLong(2);

            list.add(new LocationPoint(lat, lng, time));
        }

        cursor.close();
        db.close();

        return list;
    }
    public ArrayList<LocationPoint> getPointsSince(long timeMillis) {
        ArrayList<LocationPoint> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT lat, lng, time FROM locations WHERE time >= ?",
                new String[]{String.valueOf(timeMillis)}
        );

        while (cursor.moveToNext()) {
            list.add(new LocationPoint(
                    cursor.getDouble(0),
                    cursor.getDouble(1),
                    cursor.getLong(2)
            ));
        }

        cursor.close();
        db.close();

        return list;
    }
    public ArrayList<LocationPoint> getTodayPoints() {
        long now = System.currentTimeMillis();
        long dayAgo = now - 24L * 60 * 60 * 1000;
        return getPointsSince(dayAgo);
    }

    public ArrayList<LocationPoint> getWeekPoints() {
        long now = System.currentTimeMillis();
        long weekAgo = now - 7L * 24 * 60 * 60 * 1000;
        return getPointsSince(weekAgo);
    }

    public ArrayList<LocationPoint> getMonthPoints() {
        long now = System.currentTimeMillis();
        long monthAgo = now - 30L * 24 * 60 * 60 * 1000;
        return getPointsSince(monthAgo);
    }
    public double calculateTotalDistance(ArrayList<LocationPoint> points) {
        if (points.size() < 2) return 0;

        double total = 0;

        for (int i = 1; i < points.size(); i++) {
            LocationPoint p1 = points.get(i - 1);
            LocationPoint p2 = points.get(i);

            float[] result = new float[1];
            android.location.Location.distanceBetween(
                    p1.lat, p1.lng,
                    p2.lat, p2.lng,
                    result
            );

            total += result[0]; // метры
        }

        return total; // метры
    }

    public double calculateAverageSpeed(ArrayList<LocationPoint> points) {
        if (points.size() < 2) return 0;

        long start = points.get(0).time;
        long end = points.get(points.size() - 1).time;

        double hours = (end - start) / 3600000.0;
        double km = calculateTotalDistance(points) / 1000.0;

        if (hours == 0) return 0;

        return km / hours; // км/ч
    }
    public ArrayList<String> getHistory() {
        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT lat, lng, time FROM locations", null);

        while (cursor.moveToNext()) {
            double lat = cursor.getDouble(0);
            double lng = cursor.getDouble(1);
            long time = cursor.getLong(2);

            String formatted = String.format(
                    "Широта: %.5f\nДолгота: %.5f\nВремя: %s",
                    lat, lng,
                    new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                            .format(new java.util.Date(time))
            );

            list.add(formatted);
        }

        cursor.close();
        db.close();

        return list;
    }

}
