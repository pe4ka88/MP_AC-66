package com.example.taxiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etPhone, etName, etSurname;
    private Button btnRegistration;

    public static final String PREFS_NAME = "TaxiPrefs";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NAME = "name";
    public static final String KEY_SURNAME = "surname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LIFECYCLE_Main", "onCreate");
        setContentView(R.layout.activity_main);

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        btnRegistration = findViewById(R.id.btnRegistration);

        loadUserData();

        btnRegistration.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();

            if (phone.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            saveUserData(phone, name, surname);

            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            startActivity(intent);
        });
    }

    private void saveUserData(String phone, String name, String surname) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_SURNAME, surname);
        editor.apply();
    }

    private void loadUserData() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String phone = preferences.getString(KEY_PHONE, "");
        String name = preferences.getString(KEY_NAME, "");
        String surname = preferences.getString(KEY_SURNAME, "");

        if (!phone.isEmpty() || !name.isEmpty() || !surname.isEmpty()) {
            etPhone.setText(phone);
            etName.setText(name);
            etSurname.setText(surname);
            btnRegistration.setText("Log in");
        } else {
            btnRegistration.setText("Registration");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LIFECYCLE_Main", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLE_Main", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LIFECYCLE_Main", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LIFECYCLE_Main", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LIFECYCLE_Main", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LIFECYCLE_Main", "onDestroy");
    }
}