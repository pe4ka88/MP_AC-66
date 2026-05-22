package com.example.geotracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "locations")
public class LocationPoint {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double latitude;
    private double longitude;
    private float accuracy;
    private double altitude;
    private float speed;
    private long timestamp;
    private String address; // новое поле для хранения адреса/названия

    public LocationPoint(double latitude, double longitude, float accuracy,
                         double altitude, float speed, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public float getAccuracy() { return accuracy; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }

    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { this.altitude = altitude; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFormattedDate() {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date);
    }

    public String getFormattedTime() {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }
}