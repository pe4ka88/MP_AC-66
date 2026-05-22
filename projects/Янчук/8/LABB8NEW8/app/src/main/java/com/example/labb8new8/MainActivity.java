package com.example.labb8new8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final int BACKGROUND_PERMISSION_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Кнопки
        Button btnExport = findViewById(R.id.btnExport);
        Button btnStats = findViewById(R.id.btnStats);
        Button btnMap = findViewById(R.id.btnMap);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnStartTracking = findViewById(R.id.btnStartTracking);

        btnMap.setOnClickListener(v -> startActivity(new Intent(this, MapsActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnStats.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        btnExport.setOnClickListener(v -> startActivity(new Intent(this, ExportActivity.class)));

        btnStartTracking.setOnClickListener(v -> {
            if (checkPermissions()) {
                startLocationService();
            } else {
                requestForegroundPermissions();
            }
        });

        // Проверяем разрешения при запуске
        if (checkPermissions()) {
            startLocationService();
        }
    }

    // Проверяем ВСЕ разрешения
    private boolean checkPermissions() {
        boolean fine = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean coarse = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean background = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        return fine && coarse && background;
    }

    // Запрашиваем только foreground‑разрешения
    private void requestForegroundPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST
        );
    }

    // Запрашиваем background‑разрешение отдельно
    private void requestBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    BACKGROUND_PERMISSION_REQUEST
            );
        }
    }

    private void startLocationService() {
        startService(new Intent(this, LocationService.class));
        Toast.makeText(this, "Сбор геоданных запущен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            boolean fine = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            boolean coarse = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (fine && coarse) {
                // Теперь просим background
                requestBackgroundPermission();
            } else {
                Toast.makeText(this, "Разрешения не предоставлены", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == BACKGROUND_PERMISSION_REQUEST) {
            if (checkPermissions()) {
                startLocationService();
            } else {
                Toast.makeText(this,
                        "Для работы в фоне нужно разрешение \"Всегда\"",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
