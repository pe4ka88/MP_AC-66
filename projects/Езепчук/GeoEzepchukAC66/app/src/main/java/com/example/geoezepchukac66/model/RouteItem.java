package com.example.geoezepchukac66.model;

import com.example.geoezepchukac66.data.LocationPoint;

public class RouteItem {
    public LocationPoint start;
    public LocationPoint end;
    public String placeName;
    public boolean isMoving; // true - путь, false - остановка

    public RouteItem(LocationPoint start, LocationPoint end, boolean isMoving) {
        this.start = start;
        this.end = end;
        this.isMoving = isMoving;
    }
}