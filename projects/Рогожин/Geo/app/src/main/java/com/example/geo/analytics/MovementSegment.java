package com.example.geo.analytics;

import com.example.geo.data.LocationPoint;

import java.util.List;

public class MovementSegment {

    public enum Type {
        STOP,
        WALK,
        CAR
    }

    public Type type;

    // время сегмента
    public long startTime;
    public long endTime;

    // координаты начала
    public double latitude;
    public double longitude;

    // координаты конца сегмента (новое поле)
    public double endLatitude;
    public double endLongitude;

    // дистанция (метры)
    public float distance;

    // название места (геокодер)
    private String placeName;

    // точки маршрута
    public List<LocationPoint> points;

    public MovementSegment(Type type) {
        this.type = type;
    }

    // длительность сегмента
    public long getDuration() {
        return endTime - startTime;
    }

    // длительность в минутах
    public long getDurationMinutes() {
        return (endTime - startTime) / 60000;
    }

    // дистанция в км
    public float getDistanceKm() {
        return distance / 1000f;
    }

    // ---------------- Дополнения ----------------

    // Центр сегмента по широте
    public double getCenterLat() {
        if (endLatitude != 0) {
            return (latitude + endLatitude) / 2.0;
        } else {
            return latitude;
        }
    }

    // Центр сегмента по долготе
    public double getCenterLon() {
        if (endLongitude != 0) {
            return (longitude + endLongitude) / 2.0;
        } else {
            return longitude;
        }
    }

    // Setter для названия места
    public void setPlace(String place) {
        this.placeName = place;
    }

    // Getter для названия места
    public String getPlace() {
        return placeName != null ? placeName : "";
    }
}