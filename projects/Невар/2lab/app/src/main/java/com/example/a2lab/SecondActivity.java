package com.example.a2lab;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private static final int REQUEST_CODE_ROUTE = 1;

    private TextView textViewUserInfo;
    private TextView textViewRoute;
    private Button buttonSetPath;
    private Button buttonCallTaxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "onCreate вызван");

        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewRoute = findViewById(R.id.textViewRoute);
        buttonSetPath = findViewById(R.id.buttonSetPath);
        buttonCallTaxi = findViewById(R.id.buttonCallTaxi);

        // Получение данных из первого Activity
        Intent intent = getIntent();
        String phone = intent.getStringExtra("Номер телефона (Невар В.А.)");
        String firstName = intent.getStringExtra("Имя");
        String lastName = intent.getStringExtra("Фамилия");

        // Проверка на null
        if (phone == null) phone = "";
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";

        // Отображение информации о пользователе (используем строки из ресурсов)
        String userInfo = getString(R.string.first_name) + ": " + firstName + " " + lastName +
                "\n" + getString(R.string.phone) + ": " + phone;
        textViewUserInfo.setText(userInfo);

        // Кнопка Call Taxi изначально недоступна
        buttonCallTaxi.setEnabled(false);

        buttonSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Явный вызов третьего Activity
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ROUTE);
            }
        });

        buttonCallTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this,
                        R.string.taxi_called, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ROUTE && resultCode == RESULT_OK) {
            RouteData route = (RouteData) data.getSerializableExtra("routeData");

            // Формирование строки маршрута (используем строки из ресурсов)
            String routeInfo = getString(R.string.route_params) + ":\n" +
                    getString(R.string.from_where) + ": " + route.getFromPoint() + "\n" +
                    getString(R.string.to_where) + ": " + route.getToPoint() + "\n" +
                    getString(R.string.date) + ": " + route.getDate() + "\n" +
                    getString(R.string.time) + ": " + route.getTime() + "\n" +
                    getString(R.string.passengers) + ": " + route.getPassengers() + "\n" +
                    getString(R.string.comment) + ": " + route.getComment() + "\n\n" +
                    getString(R.string.call_taxi);

            textViewRoute.setText(routeInfo);
            buttonCallTaxi.setEnabled(true);
        }
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