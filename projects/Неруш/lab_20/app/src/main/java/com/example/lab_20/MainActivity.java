package com.example.lab_20;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "RoutePrefs";
    private static final String ROUTE_KEY = "saved_route";
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private IMapController mapController;
    private List<GeoPoint> geoPoints = new ArrayList<>();
    private Polyline polyline;
    private Marker currentUserMarker;
    private boolean isFirstFix = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);
        
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(18.0);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        FloatingActionButton fab = findViewById(R.id.fab_my_location);
        fab.setOnClickListener(v -> goToMyLocation());

        restoreRoute();
        requestLocationPermission();
    }

    private void goToMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    GeoPoint myLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    if (isFirstFix) {
                        mapController.setCenter(myLocation);
                        if (geoPoints.isEmpty()) {
                            geoPoints.add(myLocation);
                            updatePolyline();
                        }
                        isFirstFix = false;
                    } else {
                        mapController.animateTo(myLocation);
                    }
                    updateCurrentMarker(location);
                }
            });
        }
    }

    private void updateCurrentMarker(Location location) {
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (currentUserMarker == null) {
            currentUserMarker = new Marker(mapView);
            currentUserMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            currentUserMarker.setTitle("Вы здесь");
            Drawable icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default);
            if (icon != null) {
                icon.setTint(0xFF00FF00); 
                currentUserMarker.setIcon(icon);
            }
            mapView.getOverlays().add(currentUserMarker);
        }
        currentUserMarker.setPosition(point);
        currentUserMarker.setSnippet(String.format("Точность: %.1f м", location.getAccuracy()));
        
        mapView.getOverlays().remove(currentUserMarker);
        mapView.getOverlays().add(currentUserMarker);
        mapView.invalidate();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000)
                .setWaitForAccurateLocation(false)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            goToMyLocation();
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                updateCurrentMarker(location);

                if (location.getAccuracy() > 100) continue; 
                
                GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                
                if (!geoPoints.isEmpty()) {
                    GeoPoint lastPoint = geoPoints.get(geoPoints.size() - 1);
                    // Уменьшен порог до 0.5м для мгновенной реакции на движение
                    if (currentLocation.distanceToAsDouble(lastPoint) < 0.5) continue;
                }

                geoPoints.add(currentLocation);

                if (isFirstFix) {
                    mapController.setCenter(currentLocation);
                    isFirstFix = false;
                }

                // Добавляем маркер для каждой новой точки текущего захода
                Marker pathMarker = new Marker(mapView);
                pathMarker.setPosition(currentLocation);
                pathMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                pathMarker.setTitle("Точка");
                pathMarker.setSnippet(String.format("%.6f, %.6f", location.getLatitude(), location.getLongitude()));
                mapView.getOverlays().add(pathMarker);
                
                // Обновляем линию и сохраняем СРАЗУ для каждой точки
                updatePolyline();
                saveRoute();
            }
        }
    };

    private void updatePolyline() {
        if (geoPoints.isEmpty()) return;
        if (polyline == null) {
            polyline = new Polyline(mapView);
            polyline.setWidth(12f);
            polyline.setColor(0xFFFF0000); 
            mapView.getOverlays().add(0, polyline);
        }
        polyline.setPoints(new ArrayList<>(geoPoints));
        mapView.invalidate();
    }

    private void saveRoute() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        StringBuilder sb = new StringBuilder();
        for (GeoPoint point : geoPoints) {
            sb.append(point.getLatitude()).append(",").append(point.getLongitude()).append(";");
        }
        editor.putString(ROUTE_KEY, sb.toString());
        editor.apply();
    }

    private void restoreRoute() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedRoute = preferences.getString(ROUTE_KEY, "");
        if (!savedRoute.isEmpty()) {
            String[] points = savedRoute.split(";");
            for (String point : points) {
                String[] latLng = point.split(",");
                if (latLng.length == 2) {
                    try {
                        geoPoints.add(new GeoPoint(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])));
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (!geoPoints.isEmpty()) {
                updatePolyline();
                mapController.setCenter(geoPoints.get(geoPoints.size() - 1));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
