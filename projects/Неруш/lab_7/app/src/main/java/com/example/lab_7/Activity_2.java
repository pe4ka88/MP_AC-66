package com.example.lab_7;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_7.R;

public class Activity_2 extends AppCompatActivity {

    TextView textUserInfo, textRoute;
    Button btnSetPath, btnCallTaxi;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layer_two_activity);

        textUserInfo = findViewById(R.id.textUserInfo);
        textRoute = findViewById(R.id.textRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        Intent intent = getIntent();
        String phone = intent.getStringExtra("Phone");
        String firstName = intent.getStringExtra("FirstName");
        String lastName = intent.getStringExtra("LastName");

        textUserInfo.setText(firstName + " " + lastName + "\nPhone: " + phone);
        btnSetPath.setOnClickListener(v -> {
            Intent pathIntent = new Intent(Activity_2.this, Activity_3.class);
            startActivityForResult(pathIntent, REQUEST_CODE);
        });

        btnCallTaxi.setOnClickListener(v -> {
            Toast.makeText(Activity_2.this, "Taxi is on the way!", Toast.LENGTH_SHORT).show();
        });
        Log.d("Lifecycle", "onCreate called in SecondActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String route = data.getStringExtra("Route");
                String[] routeParts = route.split(", ");
                StringBuilder formattedRoute = new StringBuilder();
                for (String part : routeParts) {
                    formattedRoute.append(part).append("\n");
                }
                btnCallTaxi.setEnabled(true);
                textRoute.setText(formattedRoute.toString());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle", "onStart called in Activity_2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume called in Activity_2");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause called in Activity_2");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle", "onStop called in Activity_2");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "onDestroy called in Activity_2");
    }
}
