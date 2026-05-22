package com.example.taxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivityLifecycle";
    private static final int REQUEST_CODE_SET_PATH = 1;

    private TextView tvUserInfo, tvUserPhone, tvRoute;
    private Button btnSetPath, btnCallTaxi, btnHistory;
    private ImageButton btnProfile;
    private RadioGroup rgPayment;
    private String currentRoute = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "onCreate");

        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvRoute = findViewById(R.id.tvRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);
        btnHistory = findViewById(R.id.btnHistory);
        btnProfile = findViewById(R.id.btnProfile);
        rgPayment = findViewById(R.id.rgPayment);

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String phone = intent.getStringExtra("phone");

        tvUserInfo.setText(firstName + " " + lastName);
        tvUserPhone.setText(phone);

        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setPathIntent = new Intent("com.example.taxi.ACTION_SET_PATH");
                startActivityForResult(setPathIntent, REQUEST_CODE_SET_PATH);
            }
        });

        btnCallTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgPayment.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedId);
                String paymentMethod = radioButton.getText().toString();
                
                String message = getString(R.string.taxi_success) + "\nОплата: " + paymentMethod;
                Toast.makeText(SecondActivity.this, message, Toast.LENGTH_LONG).show();

                // Сохраняем в историю
                saveToHistory(currentRoute, paymentMethod);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(SecondActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(SecondActivity.this, HistoryActivity.class);
                startActivity(historyIntent);
            }
        });
    }

    private void saveToHistory(String route, String payment) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String history = prefs.getString("history", "");
        
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
        String newEntry = date + "\n" + route + "\nОплата: " + payment + "\n\n";
        
        prefs.edit().putString("history", newEntry + history).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_PATH && resultCode == RESULT_OK && data != null) {
            currentRoute = data.getStringExtra("route");
            tvRoute.setText(getString(R.string.route_prefix, currentRoute));
            btnCallTaxi.setEnabled(true);
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
