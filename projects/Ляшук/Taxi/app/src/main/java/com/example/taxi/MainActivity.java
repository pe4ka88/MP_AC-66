package com.example.taxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLifecycle";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";

    private EditText etPhone, etFirstName, etLastName;
    private Button btnRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        etPhone = findViewById(R.id.etPhone);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnRegistration = findViewById(R.id.btnRegistration);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedPhone = prefs.getString(KEY_PHONE, "");
        String savedFirstName = prefs.getString(KEY_FIRST_NAME, "");
        String savedLastName = prefs.getString(KEY_LAST_NAME, "");

        if (!savedPhone.isEmpty() && !savedFirstName.isEmpty() && !savedLastName.isEmpty()) {
            etPhone.setText(savedPhone);
            etFirstName.setText(savedFirstName);
            etLastName.setText(savedLastName);
            btnRegistration.setText(R.string.login_button);
        }

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_PHONE, phone);
                editor.putString(KEY_FIRST_NAME, firstName);
                editor.putString(KEY_LAST_NAME, lastName);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });
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
