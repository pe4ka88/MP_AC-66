package com.example.geo.data;

public class LocationPoint {
    public double latitude;
    public double longitude;
    public long timestamp;

    // Добавляем новые поля для PlacesFragment
    public String name;     // Название места
    public String osmClass; // Класс места (например, amenity, shop, leisure и т.д.)

    public LocationPoint(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.name = null;
        this.osmClass = null;
    }

    // Дополнительно можно добавить конструктор с именем и классом
    public LocationPoint(double latitude, double longitude, long timestamp, String name, String osmClass) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.name = name;
        this.osmClass = osmClass;
    }
}