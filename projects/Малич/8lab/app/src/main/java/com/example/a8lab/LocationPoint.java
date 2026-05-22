package com.example.a8lab;

public class LocationPoint {
    private double latitude;
    private double longitude;
    private String title;
    private String type;
    private long timestamp;

    public LocationPoint(double latitude, double longitude, String title, String type, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.type = type;
        this.timestamp = timestamp;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public long getTimestamp() { return timestamp; }

    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}