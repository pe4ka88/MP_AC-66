package com.example.taxiezepchukac66;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteActivity extends AppCompatActivity {

    private TextView tvUser, tvRoute;
    private Button btnCallTaxi, btnSetRoute, btnProfile;

    private String phone, name, surname;
    private double pickupLat, pickupLon, dropLat, dropLon;

    private ActivityResultLauncher<Intent> mapLauncher;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        tvUser = findViewById(R.id.tvUser);
        tvRoute = findViewById(R.id.tvRoute);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);
        btnSetRoute = findViewById(R.id.btnSetRoute);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        preferences = getSharedPreferences("user_data", MODE_PRIVATE);

        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        surname = getIntent().getStringExtra("surname");

        if (name == null || surname == null) {
            phone = preferences.getString("phone", "");
            name = preferences.getString("name", "");
            surname = preferences.getString("surname", "");
        }

        tvUser.setText("Пассажир: " + name + " " + surname + "\nТелефон: " + phone);

        // ===== Получение маршрута с карты =====
        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        pickupLat = result.getData().getDoubleExtra("pickup_lat", 0);
                        pickupLon = result.getData().getDoubleExtra("pickup_lon", 0);
                        dropLat = result.getData().getDoubleExtra("dropoff_lat", 0);
                        dropLon = result.getData().getDoubleExtra("dropoff_lon", 0);

                        tvRoute.setText(
                                "Откуда: " + pickupLat + ", " + pickupLon +
                                        "\nКуда: " + dropLat + ", " + dropLon
                        );
                    }
                }
        );

        btnSetRoute.setOnClickListener(v -> mapLauncher.launch(new Intent(this, MapActivity.class)));

        btnCallTaxi.setOnClickListener(v -> {
            if (pickupLat == 0 || dropLat == 0) {
                Toast.makeText(this, "Сначала укажите маршрут", Toast.LENGTH_SHORT).show();
                return;
            }
            showDriverDialog();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(RouteActivity.this, ProfileActivity.class));
        });
    }

    private void showDriverDialog() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        List<Driver> allDrivers = Arrays.asList(
                new Driver("Чумабой", "Toyota Prius", 8,16,
                        R.drawable.driver1,4.8f),
                new Driver("Аркадий", "Kia Rio",16,24,
                        R.drawable.driver2,4.5f),
                new Driver("Мухтар", "Skoda Octavia",0,8,
                        R.drawable.driver3,4.9f),
                new Driver("Одай","Hyundai Solaris",0,12,
                        R.drawable.driver4,4.6f),
                new Driver("Амлет","Lada Granta",12,24,
                        R.drawable.driver5,4.3f)
        );

        List<Driver> availableDrivers = new ArrayList<>();
        for (Driver d : allDrivers) {
            if (d.isAvailable(hour)) availableDrivers.add(d);
        }

        if (availableDrivers.isEmpty()) {
            Toast.makeText(this,
                    "Сейчас нет доступных водителей",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DriverAdapter adapter = new DriverAdapter(this, availableDrivers);

        SharedPreferences pref = getSharedPreferences("user_data", MODE_PRIVATE);
        String userPhone = pref.getString("phone", "+375000000000");

        new AlertDialog.Builder(this)
                .setTitle("Выберите водителя")
                .setAdapter(adapter, (dialog, which) -> {

                    Driver selected = availableDrivers.get(which);

                    saveTrip("Маршрут: " + pickupLat + "," + pickupLon +
                                    " -> " + dropLat + "," + dropLon +
                                    " | Водитель: " +
                                    selected.getDisplayText(),
                            userPhone); // ✅ передаём телефон

                    Toast.makeText(this,
                            "Таксист назначен: " +
                                    selected.getDisplayText() +
                                    "\nРейтинг: " +
                                    selected.getRating(),
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }



    private void saveTrip(String tripInfo, String userPhone) {
        SharedPreferences pref = getSharedPreferences("user_data", MODE_PRIVATE);
        String userKey = "trips_" + userPhone;

        Set<String> tripsSet = pref.getStringSet(userKey, null);
        if (tripsSet == null) tripsSet = new HashSet<>();
        tripsSet.add(tripInfo);

        pref.edit().putStringSet(userKey, tripsSet).apply();

        // ----- Уведомление об успешном заказе -----
        Toast.makeText(this,
                "Заказ успешно оформлен!\n" + tripInfo,
                Toast.LENGTH_LONG).show();
    }


    public static class Driver {

        private String name;
        private String car;
        private int startHour;
        private int endHour;
        private int photoRes;     // ресурс фото
        private float rating;     // 0.0 - 5.0

        public Driver(String name, String car,
                      int startHour, int endHour,
                      int photoRes, float rating) {

            this.name = name;
            this.car = car;
            this.startHour = startHour;
            this.endHour = endHour;
            this.photoRes = photoRes;
            this.rating = rating;
        }

        public boolean isAvailable(int hour) {
            return hour >= startHour && hour < endHour;
        }

        public String getName() { return name; }
        public String getCar() { return car; }
        public int getPhotoRes() { return photoRes; }
        public float getRating() { return rating; }

        public String getDisplayText() {
            return name + " (" + car + ")";
        }
    }

}
