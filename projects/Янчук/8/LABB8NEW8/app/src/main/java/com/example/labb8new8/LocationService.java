package com.example.labb8new8;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService extends Service {

    private FusedLocationProviderClient client;
    private GeoManager geoManager;

    @Override
    public void onCreate() {
        super.onCreate();

        client = LocationServices.getFusedLocationProviderClient(this);
        geoManager = new GeoManager(this);

        startLocationUpdates();
    }

    private void startLocationUpdates() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {


            return;
        }

        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10_000 // каждые 10 секунд
        ).setMinUpdateIntervalMillis(5_000)
                .build();

        client.requestLocationUpdates(request, callback, Looper.getMainLooper());
    }

    private final LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            for (Location loc : result.getLocations()) {
                geoManager.addLocation(loc.getLatitude(), loc.getLongitude());
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // сервис без привязки
    }
}
