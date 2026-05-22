package com.example.taxiezepchukac66;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private ListView listTrips;
    private TextView tvName, tvPhone;
    private Spinner spinnerPayment;
    private Button btnSave;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listTrips = findViewById(R.id.listTrips);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        pref = getSharedPreferences("user_data", MODE_PRIVATE);

        // ---------- Информация о пользователе ----------
        String name = pref.getString("name", "Гость");
        String phone = pref.getString("phone", "+375000000000");
        String payment = pref.getString("payment", "Наличные");

        tvName.setText("Имя: " + name);
        tvPhone.setText("Телефон: " + phone);

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

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, RouteActivity.class);
            startActivity(intent);
            finish();
        });
        // ---------- Сохранение оплаты ----------
        btnSave.setOnClickListener(v -> {
            String selectedPayment =
                    spinnerPayment.getSelectedItem().toString();

            pref.edit()
                    .putString("payment", selectedPayment)
                    .apply();

            Toast.makeText(this,
                    "Способ оплаты сохранён",
                    Toast.LENGTH_SHORT).show();
        });

        // ---------- История поездок ----------
        String userKey = "trips_" + phone; // уникальный ключ для текущего пользователя
        Set<String> tripsSet = pref.getStringSet(userKey, null);
        List<String> trips = tripsSet != null ? new ArrayList<>(tripsSet) : new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                trips
        );
        listTrips.setAdapter(adapter);
    }
}
