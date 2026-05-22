package com.example.geo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.geo.data.LocationDatabaseHelper;

public class LocationService extends Service {

    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private LocationDatabaseHelper dbHelper;

    private static final String CHANNEL_ID = "tracking_channel";

    private Location lastSavedLocation = null;
    private long lastSavedTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new LocationDatabaseHelper(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        createNotificationChannel();
        startForeground(1, createNotification());

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        locationListener = location -> handleNewLocation(location);

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,   // получаем часто (каждые 10 сек)
                    5,       // минимум 5 метров
                    locationListener
            );
        }
    }

    private void handleNewLocation(Location location) {

        long currentTime = System.currentTimeMillis();

        if (lastSavedLocation == null) {
            savePoint(location, currentTime);
            return;
        }

        float distance = location.distanceTo(lastSavedLocation); // в метрах
        long timeDiff = currentTime - lastSavedTime;             // в мс

        if (timeDiff <= 0) return;

        double speedMps = distance / (timeDiff / 1000.0);
        double speedKmh = speedMps * 3.6;

        // ---- Определяем тип движения ----

        long requiredInterval;

        if (speedKmh <= 6) {              // пешком
            requiredInterval = 30000;
        } else if (speedKmh <= 25) {      // велосипед
            requiredInterval = 20000;
        } else {                          // машина
            requiredInterval = 15000;
        }

        // ---- Фильтрация шума ----

        if (distance < 5) return; // игнорируем дрожание GPS

        if (timeDiff >= requiredInterval) {
            savePoint(location, currentTime);
        }
    }

    private void savePoint(Location location, long time) {

        dbHelper.insert(
                location.getLatitude(),
                location.getLongitude(),
                time
        );

        lastSavedLocation = location;
        lastSavedTime = time;
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GeoEzepchuk")
                .setContentText("Отслеживание активно")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Location Tracking",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}