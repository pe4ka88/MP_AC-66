package com.example.geoezepchukac66;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.example.geoezepchukac66.data.LocationDatabaseHelper;
import com.example.geoezepchukac66.utils.TestDataGenerator;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private TextView statusText;
    private MaterialButton startStopBtn;
    private ImageView analyticsIcon;
    private ImageView mapIcon;

    private boolean isTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        startStopBtn = findViewById(R.id.startStopFab);
        analyticsIcon = findViewById(R.id.analyticsIcon);
        mapIcon = findViewById(R.id.mapIcon);

        LocationDatabaseHelper db = new LocationDatabaseHelper(this);

        // Генерация тестовых данных
        new Thread(() -> {
            runOnUiThread(() -> statusText.setText("Генерация тестовых данных..."));
            TestDataGenerator.generate(db);
            runOnUiThread(() -> statusText.setText("Статус: остановлено"));
        }).start();

        // Центральная кнопка Start / Stop
        startStopBtn.setOnClickListener(v -> {
            if (isTracking) {
                stopTracking();
            } else {
                checkPermissionAndStart();
            }
        });

        // Иконка аналитики
        analyticsIcon.setOnClickListener(v ->
                startActivity(new Intent(this, AnalyticsActivity.class)));

        // Иконка карты
        mapIcon.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));
    }

    private void checkPermissionAndStart() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    LOCATION_PERMISSION_CODE
            );

        } else {
            startTracking();
        }
    }

    private void startTracking() {
        if (isTracking) return;

        Intent intent = new Intent(this, LocationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        isTracking = true;
        statusText.setText("Статус: отслеживание активно");
        startStopBtn.setIconResource(R.drawable.ic_stop);
    }

    private void stopTracking() {
        if (!isTracking) return;

        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);

        isTracking = false;
        statusText.setText("Статус: остановлено");
        startStopBtn.setIconResource(R.drawable.ic_play);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "Разрешение получено",
                        Toast.LENGTH_SHORT).show();

                startTracking();

            } else {
                Toast.makeText(this,
                        "Разрешение необходимо для работы GPS",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}