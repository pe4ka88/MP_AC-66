package com.example.labb2new;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    TextView tvUser, tvPhone, tvRoute;
    Button btnSetPath, btnCallTaxi;

    final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d("LIFECYCLE", "SecondActivity: onCreate");

        tvUser = findViewById(R.id.tvUser);
        tvPhone = findViewById(R.id.tvPhone);
        tvRoute = findViewById(R.id.tvRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        btnCallTaxi.setEnabled(false);

        // Получаем данные
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String phone = intent.getStringExtra("phone");

        tvUser.setText(name + " " + surname);
        tvPhone.setText(phone);

        btnSetPath.setOnClickListener(v -> {
            Intent i = new Intent("com.example.taxi.SET_PATH");
            i.setClass(SecondActivity.this, ThirdActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        });

        btnCallTaxi.setOnClickListener(v ->
                Toast.makeText(this, "Такси успешно вызвано!", Toast.LENGTH_LONG).show()
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String route = data.getStringExtra("route");
            tvRoute.setText("Маршрут:\n" + route + "\n\nМожно вызвать такси.");
            btnCallTaxi.setEnabled(true);
        }
    }

    @Override protected void onStart() { super.onStart(); Log.d("LIFECYCLE", "SecondActivity: onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d("LIFECYCLE", "SecondActivity: onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d("LIFECYCLE", "SecondActivity: onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d("LIFECYCLE", "SecondActivity: onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d("LIFECYCLE", "SecondActivity: onDestroy"); }
}
