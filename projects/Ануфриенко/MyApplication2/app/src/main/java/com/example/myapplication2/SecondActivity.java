package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "LIFECYCLE";
    private static final int REQUEST_CODE = 1;

    TextView tvUser, tvPhone, tvPath;
    Button btnSetPath, btnCallTaxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SecondActivity -> onCreate");
        setContentView(R.layout.activity_second);

        tvUser = findViewById(R.id.tvUser);
        tvPhone = findViewById(R.id.tvPhone);
        tvPath = findViewById(R.id.tvPath);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String phone = intent.getStringExtra("phone");

        tvUser.setText("User: " + name + " " + surname);
        tvPhone.setText("Phone: " + phone);

        btnSetPath.setOnClickListener(v -> {
            Log.d(TAG, "SecondActivity -> SetPath clicked");
            Intent pathIntent = new Intent(this, ThirdActivity.class);
            startActivityForResult(pathIntent, REQUEST_CODE);
        });

        btnCallTaxi.setOnClickListener(v -> {
            Log.d(TAG, "SecondActivity -> CallTaxi clicked");
            Toast.makeText(this, "Taxi successfully sent!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "SecondActivity -> onActivityResult");

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra("path");
            tvPath.setText("Route: " + path + "\nCall taxi?");
            btnCallTaxi.setEnabled(true);
        }
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "SecondActivity -> onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "SecondActivity -> onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "SecondActivity -> onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "SecondActivity -> onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "SecondActivity -> onDestroy"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "SecondActivity -> onRestart"); }
}