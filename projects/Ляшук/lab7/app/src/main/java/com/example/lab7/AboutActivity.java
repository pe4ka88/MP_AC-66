package com.example.lab7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class AboutActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        dbHelper = new DatabaseHelper(this);
        dbHelper.addRecord("Просмотрен раздел 'О приложении' (Ляшук В.И.)");

        locationText = findViewById(R.id.locationText);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnBack = findViewById(R.id.btnBackAbout);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnGetLocation.setOnClickListener(v -> getLastLocation());
        btnBack.setOnClickListener(v -> finish());
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                String locStr = "Координаты: " + location.getLatitude() + ", " + location.getLongitude();
                locationText.setText(locStr);
                dbHelper.addRecord("Местоположение определено (Ляшук В.И.): " + locStr);
            } else {
                Toast.makeText(this, "Не удалось получить координаты", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }
}
