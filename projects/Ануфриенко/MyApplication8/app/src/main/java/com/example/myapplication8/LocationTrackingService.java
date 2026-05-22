package com.example.myapplication8;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Foreground-сервис, который запрашивает GPS каждые 5 минут
 * и сохраняет координаты в SQLite.
 */
public class LocationTrackingService extends Service {

    private static final String TAG           = "LocationTrackingService";
    private static final String CHANNEL_ID    = "geo_tracking_channel";
    private static final int    NOTIF_ID      = 1001;
    private static final long   INTERVAL_MS   = 5 * 60 * 1000L;   // 5 минут
    private static final long   MIN_DISTANCE  = 50;                // минимум 50 м между точками

    public static final String ACTION_START = "START_TRACKING";
    public static final String ACTION_STOP  = "STOP_TRACKING";

    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;
    private Location lastSavedLocation;

    // ── Lifecycle ──────────────────────────────────────────────────────

    @Override
    public void onCreate() {
        super.onCreate();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();
        buildCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopTracking();
            return START_NOT_STICKY;
        }
        startForeground(NOTIF_ID, buildNotification());
        startLocationUpdates();
        Log.d(TAG, "Tracking started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    // ── Location logic ─────────────────────────────────────────────────

    private void buildCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result == null) return;
                for (Location loc : result.getLocations()) {
                    handleLocation(loc);
                }
            }
        };
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MS)
                .setMinUpdateIntervalMillis(INTERVAL_MS / 2)
                .build();

        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void stopTracking() {
        fusedClient.removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
        Log.d(TAG, "Tracking stopped");
    }

    private void handleLocation(Location location) {
        // Сохраняем, только если переместились достаточно далеко
        if (lastSavedLocation != null &&
                lastSavedLocation.distanceTo(location) < MIN_DISTANCE) {
            return;
        }

        LocationEntry entry = new LocationEntry(
                location.getLatitude(),
                location.getLongitude(),
                location.getTime(),
                null,   // название будет заполнено при geocoding (опционально)
                location.getAccuracy()
        );

        DatabaseHelper.getInstance(this).insertLocation(entry);
        lastSavedLocation = location;
        Log.d(TAG, String.format("Saved: %.5f, %.5f", location.getLatitude(), location.getLongitude()));
    }

    // ── Notification ───────────────────────────────────────────────────

    private Notification buildNotification() {
        Intent stopIntent = new Intent(this, LocationTrackingService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPi = PendingIntent.getService(this, 0, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent openPi = PendingIntent.getActivity(this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Геолокация активна")
                .setContentText("Запись маршрута ведётся в фоне")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentIntent(openPi)
                .addAction(android.R.drawable.ic_delete, "Стоп", stopPi)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Отслеживание геолокации",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Фоновая запись маршрута");
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
}