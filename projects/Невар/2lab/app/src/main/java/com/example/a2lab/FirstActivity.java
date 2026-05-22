package com.example.a2lab;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirstActivity extends AppCompatActivity {

    private static final String TAG = "FirstActivity";
    private static final String PREFS_NAME = "TaxiPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";

    private EditText editTextPhone;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Log.d(TAG, "onCreate вызван");

        editTextPhone = findViewById(R.id.editTextPhone);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        buttonRegister = findViewById(R.id.buttonRegister);

        loadSavedData();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();

                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                intent.putExtra("Номер телефона (Невар В.А.)", editTextPhone.getText().toString());
                intent.putExtra("Имя", editTextFirstName.getText().toString());
                intent.putExtra("Фамилия", editTextLastName.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String phone = prefs.getString(KEY_PHONE, "");
        String firstName = prefs.getString(KEY_FIRST_NAME, "");
        String lastName = prefs.getString(KEY_LAST_NAME, "");

        if (!phone.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
            editTextPhone.setText(phone);
            editTextFirstName.setText(firstName);
            editTextLastName.setText(lastName);
            buttonRegister.setText(R.string.register_button);
        }
    }

    private void saveUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PHONE, editTextPhone.getText().toString());
        editor.putString(KEY_FIRST_NAME, editTextFirstName.getText().toString());
        editor.putString(KEY_LAST_NAME, editTextLastName.getText().toString());
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart вызван");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume вызван");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause вызван");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop вызван");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy вызван");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart вызван");
    }
}