package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private TextView tvUserInfo, tvRoute;
    private Button btnSetPath, btnCallTaxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Log.d("LIFECYCLE", "UserActivity onCreate");

        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvRoute = findViewById(R.id.tvRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        // Получаем данные от MainActivity
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String first = intent.getStringExtra("first");
        String last = intent.getStringExtra("last");

        tvUserInfo.setText(first + " " + last + "\nТелефон: " + phone);

        // Кнопка Set path — запускает RouteActivity
        btnSetPath.setOnClickListener(v -> {
            Intent routeIntent = new Intent(UserActivity.this, RouteActivity.class);
            startActivityForResult(routeIntent, 1);
        });

        // Кнопка Call Taxi — станет активной после маршрута
        btnCallTaxi.setOnClickListener(v ->
                Toast.makeText(this, "Такси успешно вызвано!", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String route = data.getStringExtra("route");
            tvRoute.setText("Маршрут: " + route + "\nГотовы вызвать такси");
            btnCallTaxi.setEnabled(true);
        }
    }

    @Override protected void onStart() { super.onStart(); Log.d("LIFECYCLE", "UserActivity onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d("LIFECYCLE", "UserActivity onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d("LIFECYCLE", "UserActivity onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d("LIFECYCLE", "UserActivity onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d("LIFECYCLE", "UserActivity onDestroy"); }
}
