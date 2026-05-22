package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity {

    private LinearLayout container;
    private Button btnAddPoint, btnOk, btnGeo;
    private int pointCount = 0;
    private final int MAX_POINTS = 10;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        container = findViewById(R.id.container);
        btnAddPoint = findViewById(R.id.btnAddPoint);
        btnOk = findViewById(R.id.btnOk);
        btnGeo = findViewById(R.id.btnGeo);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Добавляем первые две точки
        addPoint();
        addPoint();

        btnAddPoint.setOnClickListener(v -> addPoint());

        btnGeo.setOnClickListener(v -> requestLocation());

        btnOk.setOnClickListener(v -> {

            StringBuilder route = new StringBuilder();
            int filledPoints = 0;

            for (int i = 0; i < container.getChildCount(); i++) {

                LinearLayout block = (LinearLayout) container.getChildAt(i);
                LinearLayout row = (LinearLayout) block.getChildAt(1);

                EditText etCity = (EditText) row.getChildAt(0);
                EditText etStreet = (EditText) row.getChildAt(1);
                EditText etHouse = (EditText) row.getChildAt(2);

                String city = etCity.getText().toString().trim();
                String street = etStreet.getText().toString().trim();
                String house = etHouse.getText().toString().trim();

                if (city.isEmpty() && street.isEmpty() && house.isEmpty()) {
                    continue;
                }

                filledPoints++;

                route.append("Точка ").append(filledPoints).append(": ")
                        .append("Г. ").append(city).append(", ")
                        .append("Ул. ").append(street).append(", ")
                        .append("Д. ").append(house).append("\n");
            }

            if (filledPoints < 2) {
                Toast.makeText(this, "Заполните минимум 2 точки маршрута", Toast.LENGTH_LONG).show();
                return;
            }

            Intent result = new Intent();
            result.putExtra("route", route.toString());
            setResult(RESULT_OK, result);
            finish();
        });
    }

    // ------------------ ДОБАВЛЕННЫЙ КОД ГЕОЛОКАЦИИ ------------------

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        getLocation();
    }

    private void getLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Не удалось получить местоположение", Toast.LENGTH_SHORT).show();
                return;
            }

            fillAddress(location.getLatitude(), location.getLongitude());
        });
    }

    private void fillAddress(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);

            if (list == null || list.isEmpty()) {
                Toast.makeText(this, "Адрес не найден", Toast.LENGTH_SHORT).show();
                return;
            }

            Address a = list.get(0);

            LinearLayout block = (LinearLayout) container.getChildAt(0);
            LinearLayout row = (LinearLayout) block.getChildAt(1);

            EditText etCity = (EditText) row.getChildAt(0);
            EditText etStreet = (EditText) row.getChildAt(1);
            EditText etHouse = (EditText) row.getChildAt(2);

            etCity.setText(a.getLocality());
            etStreet.setText(a.getThoroughfare());
            etHouse.setText(a.getSubThoroughfare());

            Toast.makeText(this, "Местоположение определено", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка геокодера", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    // ------------------ ДОБАВЛЕННЫЙ КОД ГЕОЛОКАЦИИ ------------------


    private void addPoint() {

        if (pointCount >= MAX_POINTS) {
            Toast.makeText(this, "Максимум 10 точек", Toast.LENGTH_SHORT).show();
            return;
        }

        pointCount++;

        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);
        block.setPadding(0, 6, 0, 6);

        TextView title = new TextView(this);
        title.setText("Точка " + pointCount);
        title.setTextSize(15);
        title.setPadding(0, 0, 0, 4);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        // ------------------ ШИРОКИЕ ПОЛЯ ------------------

        EditText etCity = new EditText(this);
        etCity.setHint("Г.");
        etCity.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 3
        ));

        EditText etStreet = new EditText(this);
        etStreet.setHint("Ул.");
        etStreet.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 5
        ));

        EditText etHouse = new EditText(this);
        etHouse.setHint("Д.");
        etHouse.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2
        ));

        // ------------------ КНОПКА УДАЛЕНИЯ ------------------

        Button btnDelete = new Button(this);
        btnDelete.setText("X");
        btnDelete.setPadding(10, 0, 10, 0);

        btnDelete.setOnClickListener(v -> {
            if (container.getChildCount() <= 2) {
                Toast.makeText(this, "Должно быть минимум 2 точки", Toast.LENGTH_SHORT).show();
                return;
            }

            container.removeView(block);
            renumberPoints();
        });

        row.addView(etCity);
        row.addView(etStreet);
        row.addView(etHouse);
        row.addView(btnDelete);

        block.addView(title);
        block.addView(row);

        container.addView(block);
    }


    private void renumberPoints() {
        pointCount = container.getChildCount();

        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout block = (LinearLayout) container.getChildAt(i);
            TextView title = (TextView) block.getChildAt(0);
            title.setText("Точка " + (i + 1));
        }
    }
}
