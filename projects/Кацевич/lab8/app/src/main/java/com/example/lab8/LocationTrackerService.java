package com.example.geotracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationTrackerService extends Service {

    private static final String TAG = "LocationTrackerService";
    private static final String CHANNEL_ID = "LocationTrackerChannel";
    private static final int NOTIFICATION_ID = 12345;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private AppDatabase database;
    private ExecutorService executorService;

    // Счетчик собранных точек
    private int pointCounter = 0;
    private static final int MIN_POINTS_REQUIRED = 30;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        startLocationUpdates();

        // Проверяем текущее количество точек
        checkPointsCount();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracker",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Сервис отслеживания местоположения");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Получаем текущее количество точек для отображения
        int currentCount = 0;
        try {
            currentCount = database.locationDao().getCountSince(0);
        } catch (Exception e) {
            // Игнорируем
        }

        String contentText = "Собрано точек: " + currentCount +
                (currentCount >= MIN_POINTS_REQUIRED ? " ✅" : " ⏳");

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Geo Tracker")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_dialog_map)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000 // Каждые 30 секунд для более частого сбора точек
        )
                .setMinUpdateIntervalMillis(15000) // Минимум 15 секунд
                .setMaxUpdateDelayMillis(60000)    // Максимум 60 секунд
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    saveLocation(location);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }
    }

    private void saveLocation(Location location) {
        executorService.execute(() -> {
            try {
                // Проверяем, достаточно ли изменилась позиция (минимум 10 метров)
                LocationPoint lastPoint = database.locationDao().getLastLocation();

                if (lastPoint != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(
                            lastPoint.getLatitude(), lastPoint.getLongitude(),
                            location.getLatitude(), location.getLongitude(),
                            results
                    );

                    // Если точка слишком близко к предыдущей, не сохраняем
                    if (results[0] < 10) {
                        return;
                    }
                }

                LocationPoint point = new LocationPoint(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAccuracy(),
                        location.getAltitude(),
                        location.getSpeed(),
                        System.currentTimeMillis()
                );

                database.locationDao().insert(point);
                pointCounter++;

                int totalCount = database.locationDao().getCountSince(0);
                Log.d(TAG, "Локация сохранена #" + totalCount +
                        ": " + point.getLatitude() + ", " + point.getLongitude());

                // Обновляем уведомление с новым счетчиком
                updateNotification(totalCount);

                // Если достигли 30 точек, показываем уведомление
                if (totalCount == MIN_POINTS_REQUIRED) {
                    showPointsCollectedNotification();
                }

            } catch (Exception e) {
                Log.e(TAG, "Ошибка сохранения: " + e.getMessage());
            }
        });
    }

    private void updateNotification(int totalCount) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Geo Tracker")
                .setContentText("Собрано точек: " + totalCount +
                        (totalCount >= MIN_POINTS_REQUIRED ? " ✅ Готово!" : " ⏳"))
                .setSmallIcon(android.R.drawable.ic_dialog_map)
                .setContentIntent(PendingIntent.getActivity(
                        this, 0, new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_IMMUTABLE))
                .setOngoing(true)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, notification);
    }

    private void showPointsCollectedNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🎉 30 точек собрано!")
                .setContentText("Теперь можно строить маршруты в приложении")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID + 1, notification);
    }

    private void checkPointsCount() {
        executorService.execute(() -> {
            int count = database.locationDao().getCountSince(0);
            Log.d(TAG, "Текущее количество точек в БД: " + count);
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}