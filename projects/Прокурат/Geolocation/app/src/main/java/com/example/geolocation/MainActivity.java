package com.example.geolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private DatabaseHelper db;
    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(20.0);
        GeoPoint startPoint = new GeoPoint(52.475600, 25.178087);
        map.getController().setCenter(startPoint);

        Button btnAdd    = findViewById(R.id.btnAdd);
        Button btnRoute  = findViewById(R.id.btnRoute);
        Button btnClear  = findViewById(R.id.btnClear);

        btnAdd.setOnClickListener(v -> addCurrentLocation());
        btnRoute.setOnClickListener(v -> drawRoute());
        btnClear.setOnClickListener(v -> clearAll());

        requestPermissions();
        redrawMarkers();
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void addCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Нет разрешения", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(loc -> {
                    if (loc == null) {
                        Toast.makeText(this, "Локация недоступна", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.insert(loc.getLatitude(), loc.getLongitude());
                    redrawMarkers();
                    map.getController().animateTo(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                    Toast.makeText(this, "Точка сохранена", Toast.LENGTH_SHORT).show();
                });
    }


    private void redrawMarkers() {
        map.getOverlays().clear();
        List<DatabaseHelper.LocationPoint> points = db.getAll();

        for (DatabaseHelper.LocationPoint p : points) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(p.lat, p.lon));
            m.setTitle(p.getTime());
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            m.setOnMarkerClickListener((marker, mapView) -> {
                db.deleteById(p.id);
                redrawMarkers();
                Toast.makeText(this, "Точка удалена", Toast.LENGTH_SHORT).show();
                return true;
            });
            map.getOverlays().add(m);
        }
        map.invalidate();
    }

    private void drawRoute() {
        map.getOverlays().clear();
        List<DatabaseHelper.LocationPoint> points = db.getAll();

        if (points.size() < 2) {
            Toast.makeText(this, "Нужно минимум 2 точки", Toast.LENGTH_SHORT).show();
            redrawMarkers();
            return;
        }

        List<GeoPoint> geoPoints = new ArrayList<>();
        for (DatabaseHelper.LocationPoint p : points) {
            geoPoints.add(new GeoPoint(p.lat, p.lon));
        }

        Polyline line = new Polyline(map);
        line.setPoints(geoPoints);
        line.getOutlinePaint().setColor(Color.BLUE);
        line.getOutlinePaint().setStrokeWidth(7f);
        map.getOverlays().add(line);

        for (DatabaseHelper.LocationPoint p : points) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(p.lat, p.lon));
            m.setTitle(p.getTime());
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(m);
        }

        map.invalidate();
        Toast.makeText(this, "Маршрут: " + points.size() + " точек", Toast.LENGTH_SHORT).show();
    }

    private void clearAll() {
        for (DatabaseHelper.LocationPoint p : db.getAll()) {
            db.deleteById(p.id);
        }
        map.getOverlays().clear();
        map.invalidate();
        Toast.makeText(this, "Очищено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
    }

    @Override protected void onResume()  { super.onResume();  map.onResume();  }
    @Override protected void onPause()   { super.onPause();   map.onPause();   }
}
