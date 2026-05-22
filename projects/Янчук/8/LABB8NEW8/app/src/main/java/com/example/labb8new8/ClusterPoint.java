package com.example.labb8new8;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterPoint implements ClusterItem {

    private final LatLng position;

    public ClusterPoint(double lat, double lng) {
        this.position = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return "Точка";
    }

    @Override
    public String getSnippet() {
        return "";
    }

    @Override
    public Float getZIndex() {
        return 0f; // можно оставить 0f, этого достаточно
    }
}
