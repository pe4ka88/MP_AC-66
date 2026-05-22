package com.example.taxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ListView listTrips;
    private TextView tvName, tvPhone;
    private Spinner spinnerPayment;
    private Button btnSave;
    private ImageView imgAvatar;
    private ImageButton btnChangeAvatar;

    private SharedPreferences pref;

    private String phone;

    // выбор изображения
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listTrips = findViewById(R.id.listTrips);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        btnSave = findViewById(R.id.btnSave);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        ImageButton btnBack = findViewById(R.id.btnBack);

        pref = getSharedPreferences("user_data", MODE_PRIVATE);

        // ---------- Информация о пользователе ----------
        String name = pref.getString("name", "Гость");
        phone = pref.getString("phone", "+375000000000");
        String payment = pref.getString("payment", "Наличные");

        tvName.setText(name);
        tvPhone.setText(phone);

        Log.d(TAG, "User info loaded: name=" + name + ", phone=" + phone);

        // ---------- Загрузка аватара ----------
        String avatarKey = "avatar_" + phone;
        String avatarUri = pref.getString(avatarKey, null);

        if (avatarUri != null) {
            imgAvatar.setImageURI(Uri.parse(avatarUri));
        }

        // ---------- Галерея ----------
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {

                        // Даём приложению постоянное разрешение на этот URI
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        imgAvatar.setImageURI(uri);

                        pref.edit()
                                .putString("avatar_" + phone, uri.toString())
                                .apply();

                        Log.d(TAG, "Avatar saved for user: " + phone);
                    }
                });

        btnChangeAvatar.setOnClickListener(v -> {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
        });

        // ---------- Способы оплаты ----------
        List<String> payments = new ArrayList<>();
        payments.add("Наличные");
        payments.add("Карта");

        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                payments
        );

        paymentAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPayment.setAdapter(paymentAdapter);

        int selected = payments.indexOf(payment);
        if (selected >= 0) spinnerPayment.setSelection(selected);

        btnSave.setOnClickListener(v -> {
            String selectedPayment = spinnerPayment.getSelectedItem().toString();
            pref.edit().putString("payment", selectedPayment).apply();
            Toast.makeText(this, "Способ оплаты сохранён", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, RouteActivity.class));
            finish();
        });

        // ---------- История поездок ----------
        loadTripsWithGeocoding(phone);
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            pickImageLauncher.launch("image/*");
                        } else {
                            Toast.makeText(this,
                                    "Разрешение на доступ к галерее отклонено",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
    private void loadTripsWithGeocoding(String phone) {

        String userKey = "trips_" + phone;
        Set<String> tripsSet = pref.getStringSet(userKey, null);

        if (tripsSet == null || tripsSet.isEmpty()) {
            listTrips.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    new ArrayList<>()));
            return;
        }

        List<String> trips = new ArrayList<>(tripsSet);
        List<String> tripsDecoded = new ArrayList<>();

        for (String trip : trips) {

            String[] parts = trip.split("\\|");
            String routePart = parts[0].trim();
            String driverPart = parts.length > 1 ? parts[1].trim() : "";

            String cleanedRoute = routePart.replace("Маршрут: ", "")
                    .replace("Откуда: ", "")
                    .replace("Куда: ", "");

            String[] locations = cleanedRoute.split("->");

            if (locations.length != 2) {
                tripsDecoded.add(trip);
                continue;
            }

            String from = locations[0].trim();
            String to = locations[1].trim();

            double fromLat, fromLon, toLat, toLon;

            try {

                String[] fromCoords = from.split(",");
                String[] toCoords = to.split(",");

                fromLat = Double.parseDouble(fromCoords[0].trim());
                fromLon = Double.parseDouble(fromCoords[1].trim());

                toLat = Double.parseDouble(toCoords[0].trim());
                toLon = Double.parseDouble(toCoords[1].trim());

            } catch (Exception e) {
                tripsDecoded.add(trip);
                continue;
            }

            tripsDecoded.add("Откуда: ...\nКуда: ...");
            int index = tripsDecoded.size() - 1;

            GeoUtils.reverseGeocode(fromLat, fromLon, new GeoUtils.GeocodeCallback() {
                @Override
                public void onResult(String fromAddress) {

                    GeoUtils.reverseGeocode(toLat, toLon, new GeoUtils.GeocodeCallback() {
                        @Override
                        public void onResult(String toAddress) {

                            tripsDecoded.set(index,
                                    "Откуда: " + fromAddress +
                                            "\nКуда: " + toAddress +
                                            (driverPart.isEmpty() ? "" : " | " + driverPart));

                            runOnUiThread(() -> listTrips.setAdapter(
                                    new ArrayAdapter<>(ProfileActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            tripsDecoded)));
                        }

                        @Override
                        public void onError(String error) {
                            tripsDecoded.set(index, trip);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    tripsDecoded.set(index, trip);
                }
            });
        }

        listTrips.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tripsDecoded));
    }
}