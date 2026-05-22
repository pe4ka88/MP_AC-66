package com.example.lab_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Lifecycle_MainActivity";
    private static final String PREFS_NAME = "TaxiPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_THEME = "current_theme";

    private EditText etPhone, etFirstName, etLastName;
    private Button btnRegistration, btnChangeTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Устанавливаем тему ПЕРЕД super.onCreate и setContentView
        SharedPreferences themePrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isPink = themePrefs.getBoolean(KEY_THEME, true);
        setTheme(isPink ? R.style.Theme_Lab_2_Pink : R.style.Theme_Lab_2_Blue);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        etPhone = findViewById(R.id.et_phone);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        btnRegistration = findViewById(R.id.btn_registration);
        btnChangeTheme = findViewById(R.id.btn_change_theme);

        loadUserData();

        btnRegistration.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();

            saveUserData(phone, firstName, lastName);

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            startActivity(intent);
        });

        btnChangeTheme.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_THEME, !isPink);
            editor.apply();

            // Перезапускаем Activity для применения темы
            recreate();
        });
    }

    private void saveUserData(String phone, String firstName, String lastName) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.apply();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String phone = prefs.getString(KEY_PHONE, "");
        String firstName = prefs.getString(KEY_FIRST_NAME, "");
        String lastName = prefs.getString(KEY_LAST_NAME, "");

        if (!phone.isEmpty() || !firstName.isEmpty() || !lastName.isEmpty()) {
            etPhone.setText(phone);
            etFirstName.setText(firstName);
            etLastName.setText(lastName);
            btnRegistration.setText("Log in");
        }
    }

    // Методы ЖЦ для логов
    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
}