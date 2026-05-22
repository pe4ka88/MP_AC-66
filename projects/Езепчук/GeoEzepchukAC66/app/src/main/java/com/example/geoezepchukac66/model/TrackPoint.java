package com.example.geoezepchukac66.model;

public class TrackPoint {

    public double lat;
    public double lon;
    public long timestamp;

    public TrackPoint(double lat, double lon, long timestamp) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }
}
