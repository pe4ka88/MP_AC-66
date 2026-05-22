package com.example.labb8new8;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.maps.android.clustering.ClusterManager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TileOverlay heatmapOverlay;

    private ClusterManager<ClusterPoint> clusterManager;

    private GeoManager geoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        geoManager = new GeoManager(this);

        // --- Кнопки фильтров ---
        Button btnDay = findViewById(R.id.btnDay);
        Button btnWeek = findViewById(R.id.btnWeek);
        Button btnMonth = findViewById(R.id.btnMonth);
        Button btnAll = findViewById(R.id.btnAll);

        // --- Обработчики фильтров с подсветкой ---
        btnDay.setOnClickListener(v -> {
            loadPoints("day");
            highlight(btnDay, btnWeek, btnMonth, btnAll);
        });

        btnWeek.setOnClickListener(v -> {
            loadPoints("week");
            highlight(btnWeek, btnDay, btnMonth, btnAll);
        });

        btnMonth.setOnClickListener(v -> {
            loadPoints("month");
            highlight(btnMonth, btnDay, btnWeek, btnAll);
        });

        btnAll.setOnClickListener(v -> {
            loadPoints("all");
            highlight(btnAll, btnDay, btnWeek, btnMonth);
        });

        // --- Инициализация карты ---
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        clusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);


        ArrayList<LocationPoint> points = geoManager.getAllPoints();

        if (points.isEmpty()) {
            return;
        }

        // Маркеры
        for (LocationPoint p : points) {
            LatLng pos = new LatLng(p.lat, p.lng);
            mMap.addMarker(new MarkerOptions().position(pos).title("Точка"));
        }

        // Маршрут
        PolylineOptions polyline = new PolylineOptions()
                .color(Color.BLUE)
                .width(8);

        for (LocationPoint p : points) {
            polyline.add(new LatLng(p.lat, p.lng));
        }

        mMap.addPolyline(polyline);

        // Центрируем карту на первой точке
        LatLng first = new LatLng(points.get(0).lat, points.get(0).lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 15f));
    }
    private void loadPoints(String mode) {
        ArrayList<LocationPoint> points;

        switch (mode) {
            case "day":
                points = geoManager.getTodayPoints();
                break;
            case "week":
                points = geoManager.getWeekPoints();
                break;
            case "month":
                points = geoManager.getMonthPoints();
                break;
            default:
                points = geoManager.getAllPoints();
        }

        mMap.clear();

        if (points.isEmpty()) return;

        PolylineOptions polyline = new PolylineOptions()
                .color(Color.BLUE)
                .width(8);

        for (LocationPoint p : points) {
            LatLng pos = new LatLng(p.lat, p.lng);
            mMap.addMarker(new MarkerOptions().position(pos));
            polyline.add(pos);
        }

        mMap.addPolyline(polyline);

        LatLng first = new LatLng(points.get(0).lat, points.get(0).lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 15f));
    }
    private void showHeatmap(ArrayList<LocationPoint> points) {
        if (points.isEmpty()) return;

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (LocationPoint p : points) {
            latLngs.add(new LatLng(p.lat, p.lng));
        }

        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .radius(40)
                .build();

        if (heatmapOverlay != null) {
            heatmapOverlay.remove();
        }

        heatmapOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
    private void showClusters(ArrayList<LocationPoint> points) {

        // Очищаем старые маркеры
        clusterManager.getClusterMarkerCollection().clear();
        clusterManager.getMarkerCollection().clear();

        // Добавляем новые точки
        for (LocationPoint p : points) {
            clusterManager.addItem(new ClusterPoint(p.lat, p.lng));
        }

        // Перестраиваем кластеры
        clusterManager.cluster();
    }

    private void highlight(Button active, Button... others) {
        active.setBackgroundResource(R.drawable.bg_button_active);
        active.setTextColor(getColor(R.color.white));

        for (Button b : others) {
            b.setBackgroundResource(R.drawable.bg_button_inactive);
            b.setTextColor(getColor(R.color.black));
        }
    }


}
