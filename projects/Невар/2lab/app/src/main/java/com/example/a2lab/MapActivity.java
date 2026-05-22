package com.example.a2lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MapActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private Button buttonConfirmLocation;
    private boolean isForFromPoint;
    private Marker selectedMarker;
    private GeoPoint selectedPoint;
    private MyLocationNewOverlay locationOverlay;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация OSMDroid
        Configuration.getInstance().load(getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_map);

        isForFromPoint = getIntent().getBooleanExtra("isForFromPoint", true);
        buttonConfirmLocation = findViewById(R.id.buttonConfirmLocation);
        mapView = findViewById(R.id.mapView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupMap();

        buttonConfirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLocation();
            }
        });

        checkLocationPermission();
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        // Устанавливаем начальную позицию (Москва)
        GeoPoint startPoint = new GeoPoint(55.7558, 37.6176);
        mapView.getController().setCenter(startPoint);

        // Добавляем слой геолокации
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        locationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(locationOverlay);

        // Обработчик клика по карте (долгое нажатие)
        mapView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Получаем координаты центра карты
                GeoPoint centerPoint = (GeoPoint) mapView.getMapCenter();
                addMarker(centerPoint);
                return true;
            }
        });
    }

    private void addMarker(GeoPoint point) {
        // Удаляем предыдущую метку
        if (selectedMarker != null) {
            mapView.getOverlays().remove(selectedMarker);
        }

        // Добавляем новую метку
        selectedMarker = new Marker(mapView);
        selectedMarker.setPosition(point);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        selectedMarker.setTitle(isForFromPoint ? getString(R.string.from_point) : getString(R.string.to_point));
        selectedMarker.setDraggable(true);

        mapView.getOverlays().add(selectedMarker);
        mapView.invalidate();

        selectedPoint = point;

        // Показываем всплывающую подсказку
        String message = isForFromPoint ? "Точка отправления выбрана" : "Точка назначения выбрана";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void confirmLocation() {
        if (selectedPoint != null) {
            String address = String.format("%.6f, %.6f", selectedPoint.getLatitude(), selectedPoint.getLongitude());

            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", address);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, R.string.error_select_location, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        mapView.getController().setCenter(currentLocation);
                        addMarker(currentLocation);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, R.string.error_location_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}