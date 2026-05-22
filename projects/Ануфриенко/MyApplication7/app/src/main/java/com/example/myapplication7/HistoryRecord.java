package com.example.myapplication7;

public class HistoryRecord {
    public int id;
    public String activityType;
    public String description;
    public String timestamp;
    public double latitude;
    public double longitude;

    public HistoryRecord(int id, String activityType, String description,
                         String timestamp, double latitude, double longitude) {
        this.id = id;
        this.activityType = activityType;
        this.description = description;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "[" + activityType + "] " + description + " | " + timestamp;
    }
}