package com.example.taxi; // Замени на свой package

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taxi.R;

public class FirstActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone;
    private Button btnAction;
    private SharedPreferences sharedPref;

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_PHONE = "phone";

    // Тег для логов
    private static final String TAG = "FirstActivity_Lifecycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Log.d(TAG, "onCreate вызван");

        // Инициализация View
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        btnAction = findViewById(R.id.btnRegistration);

        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Загрузка сохраненных данных
        loadSavedData();

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty()) {
                    if (!isValidPhone(phone)) {
                        Toast.makeText(FirstActivity.this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Сохраняем данные
                    saveUserData(firstName, lastName, phone);

                    // Явный Intent для вызова SecondActivity
                    Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                    intent.putExtra("FIRST_NAME", firstName);
                    intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("PHONE", phone);
                    startActivity(intent);
                } else {
                    Toast.makeText(FirstActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidPhone(String phone) {
        return phone.replaceAll("\\D", "").length() >= 7;
    }

    private void loadSavedData() {
        String savedFirstName = sharedPref.getString(KEY_FIRST_NAME, "");
        String savedLastName = sharedPref.getString(KEY_LAST_NAME, "");
        String savedPhone = sharedPref.getString(KEY_PHONE, "");

        if (!savedFirstName.isEmpty()) {
            etFirstName.setText(savedFirstName);
            etLastName.setText(savedLastName);
            etPhone.setText(savedPhone);
            // Меняем название кнопки на Log in
            btnAction.setText("Log in");
        } else {
            btnAction.setText("Registration");
        }
    }

    private void saveUserData(String firstName, String lastName, String phone) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    // Методы жизненного цикла для логирования
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