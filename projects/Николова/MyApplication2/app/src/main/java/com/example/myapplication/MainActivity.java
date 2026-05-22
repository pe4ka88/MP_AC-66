package com.example.myapplication;

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
    private SharedPreferences prefs;
    private static final String TAG = "Lifecycle_Main";


    private static final long TWO_MINUTES_MS = 2 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        btnRegister = findViewById(R.id.btnRegistration);

        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);


        restoreUserData();

        btnRegister.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String name = etName.getText().toString();
            String surname = etSurname.getText().toString();


            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("phone", phone);
            editor.putString("name", name);
            editor.putString("surname", surname);
            editor.putLong("lastSavedTime", System.currentTimeMillis());
            editor.apply();


            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            startActivity(intent);
        });
    }

    private void restoreUserData() {
        long lastSavedTime = prefs.getLong("lastSavedTime", 0);
        long currentTime = System.currentTimeMillis();


        if (prefs.contains("phone") && (currentTime - lastSavedTime < TWO_MINUTES_MS)) {

            etPhone.setText(prefs.getString("phone", ""));
            etName.setText(prefs.getString("name", ""));
            etSurname.setText(prefs.getString("surname", ""));


            btnRegister.setText("Log in");
            Log.d(TAG, "Данные восстановлены, кнопка изменена на Log in");
        } else {

            etPhone.setText("");
            etName.setText("");
            etSurname.setText("");


            btnRegister.setText("Registration");
            Log.d(TAG, "Время истекло или данных нет. Поля очищены.");
        }
    }


    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        restoreUserData();
    }

    @Override protected void onPause() { super.onPause(); Log.d(TAG, "onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}