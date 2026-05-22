package com.example.labb2new;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText etPhone, etName, etSurname;
    Button btnReg;

    SharedPreferences prefs;
    final String PREF_NAME = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("LIFECYCLE", "MainActivity: onCreate");

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        btnReg = findViewById(R.id.btnReg);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Проверяем, есть ли сохранённые данные
        String savedPhone = prefs.getString("phone", "");
        String savedName = prefs.getString("name", "");
        String savedSurname = prefs.getString("surname", "");

        if (!savedPhone.isEmpty()) {
            etPhone.setText(savedPhone);
            etName.setText(savedName);
            etSurname.setText(savedSurname);
            btnReg.setText("Log in");
        }

        btnReg.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String name = etName.getText().toString();
            String surname = etSurname.getText().toString();

            // Сохраняем данные
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("phone", phone);
            editor.putString("name", name);
            editor.putString("surname", surname);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            startActivity(intent);
        });
    }

    @Override protected void onStart() { super.onStart(); Log.d("LIFECYCLE", "MainActivity: onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d("LIFECYCLE", "MainActivity: onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d("LIFECYCLE", "MainActivity: onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d("LIFECYCLE", "MainActivity: onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d("LIFECYCLE", "MainActivity: onDestroy"); }
}
