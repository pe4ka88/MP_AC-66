package com.example.a2lab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText editTextPhone;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private Button buttonRegistration;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TaxiPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_IS_REGISTERED = "isRegistered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        editTextPhone = findViewById(R.id.editTextPhone);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        buttonRegistration = findViewById(R.id.buttonRegistration);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadUserData();

        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editTextPhone.getText().toString().trim();
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();

                if (phone.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveUserData(phone, firstName, lastName);

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                startActivity(intent);
            }
        });
    }

    private void loadUserData() {
        boolean isRegistered = sharedPreferences.getBoolean(KEY_IS_REGISTERED, false);
        if (isRegistered) {
            String phone = sharedPreferences.getString(KEY_PHONE, "");
            String firstName = sharedPreferences.getString(KEY_FIRST_NAME, "");
            String lastName = sharedPreferences.getString(KEY_LAST_NAME, "");

            editTextPhone.setText(phone);
            editTextFirstName.setText(firstName);
            editTextLastName.setText(lastName);
            buttonRegistration.setText("Log in");
        } else {
            buttonRegistration.setText("Registration");
        }
    }

    private void saveUserData(String phone, String firstName, String lastName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putBoolean(KEY_IS_REGISTERED, true);
        editor.apply();
        buttonRegistration.setText("Log in");
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