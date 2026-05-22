package com.example.laba2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "L1_Main";
    private static final String PREFS = "taxi_prefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST = "first";
    private static final String KEY_LAST  = "last";

    private EditText etPhone, etFirst, etLast;
    private Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        etPhone = findViewById(R.id.etPhone);
        etFirst = findViewById(R.id.etFirstName);
        etLast  = findViewById(R.id.etLastName);

        Button btnPress = findViewById(R.id.btnPress);
        Button btnTask  = findViewById(R.id.btnTask);
        btnReg = findViewById(R.id.btnRegistration);

// TEMP_DISABLE_CRASH:         btnPress.setOnClickListener(v ->
                Toast.makeText(this, "Нажимает Занько ", Toast.LENGTH_SHORT).show();
        btnTask.setOnClickListener(v -> showTaskDialog());

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String p = sp.getString(KEY_PHONE, "");
        String f = sp.getString(KEY_FIRST, "");
        String l = sp.getString(KEY_LAST, "");

        if (!p.isEmpty() || !f.isEmpty() || !l.isEmpty()) {
            etPhone.setText(p);
            etFirst.setText(f);
            etLast.setText(l);
            btnReg.setText("Log in");
        } else {
            btnReg.setText("Registration");
        }

        btnReg.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String first = etFirst.getText().toString().trim();
            String last  = etLast.getText().toString().trim();

            if (phone.isEmpty() || first.isEmpty() || last.isEmpty()) {
                Toast.makeText(this, "Заполните телефон, имя и фамилию", Toast.LENGTH_SHORT).show();
                return;
            }

            sp.edit()
                    .putString(KEY_PHONE, phone)
                    .putString(KEY_FIRST, first)
                    .putString(KEY_LAST, last)
                    .apply();

            btnReg.setText("Log in");

            Intent i = new Intent(this, SecondActivity.class);
            i.putExtra("phone", phone);
            i.putExtra("first", first);
            i.putExtra("last", last);
            startActivity(i);
        });
    }

    private void showTaskDialog() {
        String task =
                "Практическое задание (Taxi):\n\n" +
                "1) Приложение Taxi из трех Activity.\n" +
                "2) Activity 1: телефон, имя, фамилия + Registration.\n" +
                "3) Registration: явный переход во 2 Activity с передачей данных.\n" +
                "4) Activity 2: вывод данных, маршрут (пусто), Set path, Call Taxi (disabled).\n" +
                "5) Set path: startActivityForResult -> 3 Activity.\n" +
                "6) Activity 3: 6 EditText + OK.\n" +
                "7) OK: вернуть параметры маршрута во 2 Activity.\n" +
                "8) Во 2 Activity: показать маршрут и включить Call Taxi.\n" +
                "9) Call Taxi: Toast об успешной отправке.\n" +
                "10) SharedPreferences: сохранить данные, при запуске кнопка Log in.\n" +
                "11) Логи жизненного цикла 1/2/3 Activity.\n\n" +
                "Выполнил: Занько Я.С., группа АС-66";

        new AlertDialog.Builder(this)
                .setTitle("Задача")
                .setMessage(task)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { Log.d(TAG, "onPause"); super.onPause(); }
    @Override protected void onStop() { Log.d(TAG, "onStop"); super.onStop(); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { Log.d(TAG, "onDestroy"); super.onDestroy(); }
}
