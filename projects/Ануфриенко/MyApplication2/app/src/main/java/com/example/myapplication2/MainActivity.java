package com.example.myapplication2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LIFECYCLE";

    EditText etPhone, etName, etSurname;
    Button btnRegistration;

    SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "TaxiPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity -> onCreate");
        setContentView(R.layout.activity_main);

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        btnRegistration = findViewById(R.id.btnRegistration);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String savedPhone = sharedPreferences.getString(KEY_PHONE, null);
        String savedName = sharedPreferences.getString(KEY_NAME, null);
        String savedSurname = sharedPreferences.getString(KEY_SURNAME, null);

        if (savedPhone != null && savedName != null && savedSurname != null) {
            etPhone.setText(savedPhone);
            etName.setText(savedName);
            etSurname.setText(savedSurname);
            btnRegistration.setText("Log in");
        }

        btnRegistration.setOnClickListener(v -> {
            Log.d(TAG, "MainActivity -> Registration button clicked");

            String phone = etPhone.getText().toString();
            String name = etName.getText().toString();
            String surname = etSurname.getText().toString();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_PHONE, phone);
            editor.putString(KEY_NAME, name);
            editor.putString(KEY_SURNAME, surname);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);

            startActivity(intent);
        });
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "MainActivity -> onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "MainActivity -> onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "MainActivity -> onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "MainActivity -> onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "MainActivity -> onDestroy"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "MainActivity -> onRestart"); }
}