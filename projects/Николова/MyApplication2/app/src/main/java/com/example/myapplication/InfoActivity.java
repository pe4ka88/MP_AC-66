package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    private TextView tvUserInfo, tvPath;
    private Button btnSetPath, btnCallTaxi;
    private static final String TAG = "Lifecycle_Second";

      private static final int REQUEST_CODE_PATH = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Log.d(TAG, "onCreate");

        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvPath = findViewById(R.id.tvPath);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String phone = getIntent().getStringExtra("phone");
        tvUserInfo.setText(name + " " + surname + "\n" + phone);

        btnCallTaxi.setEnabled(false);

        btnSetPath.setOnClickListener(v -> {

            Intent intent = new Intent("com.example.taxi.ACTION_SET_PATH");
            startActivityForResult(intent, REQUEST_CODE_PATH);
        });

        btnCallTaxi.setOnClickListener(v ->
                Toast.makeText(this, "Такси успешно отправлено!", Toast.LENGTH_LONG).show());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PATH && resultCode == RESULT_OK && data != null) {
            String fullPath = data.getStringExtra("full_path");

            tvPath.setText(fullPath);

            btnCallTaxi.setEnabled(true);
        }
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}