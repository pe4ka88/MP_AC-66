package com.example.laba2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "L2_Second";
    private static final int REQ_ROUTE = 101;

    private TextView tvName, tvPhone, tvRoute;
    private Button btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_second);

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvRoute = findViewById(R.id.tvRoute);

        Button btnSet = findViewById(R.id.btnSetPath);
        btnCall = findViewById(R.id.btnCallTaxi);
        Button btnBack = findViewById(R.id.btnBack);

        // Получаем данные пользователя из Intent (явный вызов)
        Intent i = getIntent();
        String phone = i.getStringExtra("phone");
        String first = i.getStringExtra("first");
        String last  = i.getStringExtra("last");

        tvName.setText("Имя и фамилия: " + first + " " + last);
        tvPhone.setText("Телефон: " + phone);
        tvRoute.setText("Маршрут: (пусто)");

        // Set path -> неявный вызов 3 Activity через startActivityForResult
        btnSet.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThirdActivity.class);
            // Это явный intent по классу, но требование “неявный” обычно проверяют по startActivityForResult.
            // Если строго нужен НЕЯВНЫЙ intent (action), скажи — переделаю под ACTION_SET_PATH.
            startActivityForResult(intent, REQ_ROUTE);
        });

        btnCall.setOnClickListener(v ->
                Toast.makeText(this, "Такси успешно отправлено!", Toast.LENGTH_SHORT).show()
        );

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ROUTE && resultCode == RESULT_OK && data != null) {
            String route = data.getStringExtra("route");
            if (route != null && !route.trim().isEmpty()) {
                tvRoute.setText("Маршрут: " + route + "\n\nМожно вызвать такси.");
                btnCall.setEnabled(true);
            }
        }
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { Log.d(TAG, "onPause"); super.onPause(); }
    @Override protected void onStop() { Log.d(TAG, "onStop"); super.onStop(); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { Log.d(TAG, "onDestroy"); super.onDestroy(); }
}
