package com.example.taxi;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    private TextView textViewUserInfo, textViewRoute;
    private Button buttonSetPath, buttonCallTaxi;
    private static final String TAG = "Lifecycle_SecondActivity";

    private ActivityResultLauncher<Intent> routeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String streetFrom = data.getStringExtra("streetFrom");
                    String houseFrom = data.getStringExtra("houseFrom");
                    String apartmentFrom = data.getStringExtra("apartmentFrom");
                    String streetTo = data.getStringExtra("streetTo");
                    String houseTo = data.getStringExtra("houseTo");
                    String apartmentTo = data.getStringExtra("apartmentTo");

                    String routeText = String.format(
                            "Такси будет подано по адресу: %s %s, кв.%s\nНазначение: %s %s, кв.%s",
                            streetFrom, houseFrom, apartmentFrom,
                            streetTo, houseTo, apartmentTo
                    );

                    textViewRoute.setText(routeText);

                    buttonCallTaxi.setEnabled(true);
                    buttonCallTaxi.setBackgroundTintList(ColorStateList.valueOf(
                            getResources().getColor(R.color.pink_accent)));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_second);

        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        textViewRoute = findViewById(R.id.textViewRoute);
        buttonSetPath = findViewById(R.id.buttonSetPath);
        buttonCallTaxi = findViewById(R.id.buttonCallTaxi);

        buttonCallTaxi.setEnabled(false);
        buttonCallTaxi.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.gray_light)));

        String phone = getIntent().getStringExtra("phone");
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");

        String userText = String.format("%s %s\nТелефон: %s", name, surname, phone);
        textViewUserInfo.setText(userText);

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
                Toast.makeText(SecondActivity.this,
                        getString(R.string.taxi_message),
                        Toast.LENGTH_LONG).show();
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