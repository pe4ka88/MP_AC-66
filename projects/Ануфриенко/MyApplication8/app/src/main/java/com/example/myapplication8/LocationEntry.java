package com.example.myapplication8;

/**
 * Модель одной записи геолокации.
 */
public class LocationEntry {

    private long id;
    private double latitude;
    private double longitude;
    private long timestamp;       // Unix time в миллисекундах
    private String placeName;     // Название места (может быть null)
    private float accuracy;       // Точность в метрах

    public LocationEntry() {}

    public LocationEntry(double latitude, double longitude, long timestamp,
                         String placeName, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.placeName = placeName;
        this.accuracy = accuracy;
    }

    // ── Геттеры / сеттеры ──────────────────────────────────────────────

    public long getId()                    { return id; }
    public void setId(long id)             { this.id = id; }

    public double getLatitude()            { return latitude; }
    public void setLatitude(double v)      { this.latitude = v; }

    public double getLongitude()           { return longitude; }
    public void setLongitude(double v)     { this.longitude = v; }

    public long getTimestamp()             { return timestamp; }
    public void setTimestamp(long v)       { this.timestamp = v; }

    public String getPlaceName()           { return placeName; }
    public void setPlaceName(String v)     { this.placeName = v; }

    public float getAccuracy()             { return accuracy; }
    public void setAccuracy(float v)       { this.accuracy = v; }
}