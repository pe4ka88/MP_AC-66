package com.example.lab_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "Lifecycle_SecondActivity";
    private static final int REQUEST_CODE_SET_PATH = 1;

    private TextView tvUserName, tvUserPhone, tvRoute;
    private Button btnSetPath, btnCallTaxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "onCreate");

        tvUserName = findViewById(R.id.tv_user_name);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvRoute = findViewById(R.id.tv_route);
        btnSetPath = findViewById(R.id.btn_set_path);
        btnCallTaxi = findViewById(R.id.btn_call_taxi);

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String phone = intent.getStringExtra("phone");

        tvUserName.setText(firstName + " " + lastName);
        tvUserPhone.setText(phone);

        btnSetPath.setOnClickListener(v -> {
            Intent pathIntent = new Intent("com.example.lab_2.ACTION_SET_PATH");
            startActivityForResult(pathIntent, REQUEST_CODE_SET_PATH);
        });

        btnCallTaxi.setOnClickListener(v -> {
            Toast.makeText(this, "Такси успешно отправлено!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_PATH && resultCode == RESULT_OK && data != null) {
            String route = data.getStringExtra("route");
            tvRoute.setText("Маршрут: " + route);
            btnCallTaxi.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
