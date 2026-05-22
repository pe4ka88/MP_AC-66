package com.example.taxiezepchukac66;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etPhone, etName, etSurname;
    private Button btnRegister;

    private SharedPreferences preferences;

    private static final String TAG = "LC_MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        btnRegister = findViewById(R.id.btnRegister);

        preferences = getSharedPreferences("user_data", MODE_PRIVATE);

        loadUserData();

        btnRegister.setOnClickListener(v -> saveAndGoNext());
    }

    private void loadUserData() {
        if (preferences.contains("phone")) {
            etPhone.setText(preferences.getString("phone", ""));
            etName.setText(preferences.getString("name", ""));
            etSurname.setText(preferences.getString("surname", ""));
            btnRegister.setText("Log in");
        } else {
            btnRegister.setText("Registration");
        }
    }

    private void saveAndGoNext() {
        String phone = etPhone.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone", phone);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, RouteActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("name", name);
        intent.putExtra("surname", surname);

        startActivity(intent);
    }

    // ===== Жизненный цикл =====

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
}
