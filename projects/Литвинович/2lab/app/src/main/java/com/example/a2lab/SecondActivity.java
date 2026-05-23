package com.example.a2lab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private TextView textViewUserInfo;
    private TextView textViewRoute;
    private Button buttonSetPath;
    private Button buttonCallTaxi;
    private String routeInfo = "";

    private final ActivityResultLauncher<Intent> routeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    routeInfo = result.getData().getStringExtra("route");
                    textViewRoute.setText(routeInfo + "\n\nТакси готово к вызову");
                    buttonCallTaxi.setEnabled(true);
                    Log.d(TAG, "Маршрут получен: " + routeInfo);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "onCreate");

        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewRoute = findViewById(R.id.textViewRoute);
        buttonSetPath = findViewById(R.id.buttonSetPath);
        buttonCallTaxi = findViewById(R.id.buttonCallTaxi);

        String phone = getIntent().getStringExtra("phone");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");

        textViewUserInfo.setText("Имя и фамилия: " + firstName + " " + lastName + "\nТелефон: " + phone);

        buttonCallTaxi.setEnabled(false);

        buttonSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                routeLauncher.launch(intent);
            }
        });

        buttonCallTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "Такси успешно вызвано!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Такси вызвано");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}