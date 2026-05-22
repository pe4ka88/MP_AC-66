package com.example.taxiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends AppCompatActivity {

    private TextView tvUserInfo, tvPhone, tvRoute, tvAuthorInfo;
    private Button btnSetPath, btnCallTaxi;

    public static final int REQUEST_CODE_ROUTE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LIFECYCLE_Order", "onCreate");
        setContentView(R.layout.activity_order);

        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvPhone = findViewById(R.id.tvPhone);
        tvRoute = findViewById(R.id.tvRoute);
        tvAuthorInfo = findViewById(R.id.tvAuthorInfo);

        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        btnCallTaxi.setEnabled(false);

        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");

        tvUserInfo.setText("Пользователь: " + name + " " + surname);
        tvPhone.setText("Телефон: " + phone);
        tvRoute.setText("Маршрут пока не задан");
        tvAuthorInfo.setText("Лысюк Ростислав АС-66");

        btnSetPath.setOnClickListener(v -> {
            Intent routeIntent = new Intent(OrderActivity.this, RouteActivity.class);
            startActivityForResult(routeIntent, REQUEST_CODE_ROUTE);
        });

        btnCallTaxi.setOnClickListener(v ->
                Toast.makeText(OrderActivity.this, "Такси вызвано!", Toast.LENGTH_LONG).show()
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ROUTE && resultCode == RESULT_OK && data != null) {
            String fromCity = data.getStringExtra("fromCity");
            String fromStreet = data.getStringExtra("fromStreet");
            String fromHouse = data.getStringExtra("fromHouse");

            String toCity = data.getStringExtra("toCity");
            String toStreet = data.getStringExtra("toStreet");
            String toHouse = data.getStringExtra("toHouse");

            String routeText =
                    "Маршрут:\n" +
                            "Откуда: " + fromCity + ", " + fromStreet + ", дом " + fromHouse + "\n" +
                            "Куда: " + toCity + ", " + toStreet + ", дом " + toHouse + "\n\n" +
                            "Можно вызвать такси.";

            tvRoute.setText(routeText);
            btnCallTaxi.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LIFECYCLE_Order", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLE_Order", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LIFECYCLE_Order", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LIFECYCLE_Order", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LIFECYCLE_Order", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LIFECYCLE_Order", "onDestroy");
    }
}