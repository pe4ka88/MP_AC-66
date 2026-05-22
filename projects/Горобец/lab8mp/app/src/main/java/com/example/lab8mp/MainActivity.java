package com.example.lab8mp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private ArrayList<GeoPoint> points = new ArrayList<>();
    private Polyline line;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        Button btnAddPoint = findViewById(R.id.btnAddPoint);

        // Линия маршрута
        line = new Polyline();
        line.setColor(Color.RED);
        line.setWidth(6f);
        map.getOverlays().add(line);

        // Чистый GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnAddPoint.setOnClickListener(v -> addRealGpsPoint());

        requestPermissions();
    }

    private void initMap() {

        double centerLat = 52.09254;
        double centerLng = 23.69150;

        GeoPoint start = new GeoPoint(centerLat, centerLng);
        map.getController().setZoom(15.5);
        map.getController().setCenter(start);

        points.clear();
        points.add(start);
        addMarker(start);

        Random random = new Random();

        double latRadius = 0.009;
        double lngRadius = 0.015;

        double currentLat = centerLat;
        double currentLng = centerLng;

        // Генерация первых 30 точек
        for (int i = 0; i < 30; i++) {

            double stepMeters = 200 + random.nextInt(200);

            double stepLat = stepMeters / 111000.0;
            double stepLng = stepMeters / (111000.0 * Math.cos(Math.toRadians(currentLat)));

            double angle = random.nextDouble() * 2 * Math.PI;

            double newLat = currentLat + stepLat * Math.sin(angle);
            double newLng = currentLng + stepLng * Math.cos(angle);

            if (Math.abs(newLat - centerLat) > latRadius ||
                    Math.abs(newLng - centerLng) > lngRadius) {
                i--;
                continue;
            }

            currentLat = newLat;
            currentLng = newLng;

            GeoPoint p = new GeoPoint(currentLat, currentLng);
            points.add(p);
            addMarker(p);
        }

        updatePolyline();
    }

    // ➤ Добавление точки по реальному GPS без Google
    private void addRealGpsPoint() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            Toast.makeText(this, "GPS недоступен", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint p = new GeoPoint(location.getLatitude(), location.getLongitude());
        points.add(p);
        addMarker(p);
        updatePolyline();
    }

    private void addMarker(GeoPoint p) {
        Marker marker = new Marker(map);
        marker.setPosition(p);
        marker.setTitle("Точка " + points.size());
        map.getOverlays().add(marker);
        map.invalidate();
    }

    private void updatePolyline() {
        line.setPoints(points);
        map.invalidate();
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1001);
        } else {
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMap();
        }
    }
}
