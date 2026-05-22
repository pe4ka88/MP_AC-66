package com.example.lab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "TaxiPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";

    private EditText editPhone, editFirstName, editLastName;
    private Button btnRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editPhone = findViewById(R.id.editPhone);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        btnRegistration = findViewById(R.id.btnRegistration);

        // Restore saved data from SharedPreferences
        loadUserData();

        btnRegistration.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();

            // Save registration data
            saveUserData(phone, firstName, lastName);

            // Explicit intent to launch OrderActivity
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            startActivity(intent);
        });

        // Task info button
        findViewById(R.id.btnTaskInfo).setOnClickListener(v -> showTaskDialog());

        // Clear data button
        findViewById(R.id.btnClearData).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Очистить данные?")
                    .setMessage("Все сохранённые данные регистрации будут удалены. Вы сможете зарегистрироваться заново.")
                    .setPositiveButton("Очистить", (dialog, which) -> {
                        clearUserData();
                        editPhone.setText("");
                        editFirstName.setText("");
                        editLastName.setText("");
                        btnRegistration.setText(R.string.btn_registration);
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void showTaskDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.task_description_title)
                .setMessage(getString(R.string.task_description))
                .setPositiveButton("OK", null)
                .show();
    }

    private void clearUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
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
            editPhone.setText(phone);
            editFirstName.setText(firstName);
            editLastName.setText(lastName);
            // Change button text to "Log in" if data was previously saved
            btnRegistration.setText(R.string.btn_login);
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