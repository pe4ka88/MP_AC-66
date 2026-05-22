package com.example.taxiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RouteActivity extends AppCompatActivity {

    private EditText etFromCity, etFromStreet, etFromHouse;
    private EditText etToCity, etToStreet, etToHouse;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LIFECYCLE_Route", "onCreate");
        setContentView(R.layout.activity_route);

        etFromCity = findViewById(R.id.etFromCity);
        etFromStreet = findViewById(R.id.etFromStreet);
        etFromHouse = findViewById(R.id.etFromHouse);

        etToCity = findViewById(R.id.etToCity);
        etToStreet = findViewById(R.id.etToStreet);
        etToHouse = findViewById(R.id.etToHouse);

        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            String fromCity = etFromCity.getText().toString().trim();
            String fromStreet = etFromStreet.getText().toString().trim();
            String fromHouse = etFromHouse.getText().toString().trim();

            String toCity = etToCity.getText().toString().trim();
            String toStreet = etToStreet.getText().toString().trim();
            String toHouse = etToHouse.getText().toString().trim();

            if (fromCity.isEmpty() || fromStreet.isEmpty() || fromHouse.isEmpty()
                    || toCity.isEmpty() || toStreet.isEmpty() || toHouse.isEmpty()) {
                Toast.makeText(RouteActivity.this, "Заполните все поля маршрута", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("fromCity", fromCity);
            resultIntent.putExtra("fromStreet", fromStreet);
            resultIntent.putExtra("fromHouse", fromHouse);

            resultIntent.putExtra("toCity", toCity);
            resultIntent.putExtra("toStreet", toStreet);
            resultIntent.putExtra("toHouse", toHouse);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LIFECYCLE_Route", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLE_Route", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LIFECYCLE_Route", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LIFECYCLE_Route", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LIFECYCLE_Route", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LIFECYCLE_Route", "onDestroy");
    }
}