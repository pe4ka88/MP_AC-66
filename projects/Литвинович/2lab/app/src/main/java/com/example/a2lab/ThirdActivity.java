package com.example.a2lab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "ThirdActivity";
    private EditText editTextFromCity;
    private EditText editTextFromStreet;
    private EditText editTextFromHouse;
    private EditText editTextToCity;
    private EditText editTextToStreet;
    private EditText editTextToHouse;
    private Button buttonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Log.d(TAG, "onCreate");

        editTextFromCity = findViewById(R.id.editTextFromCity);
        editTextFromStreet = findViewById(R.id.editTextFromStreet);
        editTextFromHouse = findViewById(R.id.editTextFromHouse);
        editTextToCity = findViewById(R.id.editTextToCity);
        editTextToStreet = findViewById(R.id.editTextToStreet);
        editTextToHouse = findViewById(R.id.editTextToHouse);
        buttonOk = findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromCity = editTextFromCity.getText().toString().trim();
                String fromStreet = editTextFromStreet.getText().toString().trim();
                String fromHouse = editTextFromHouse.getText().toString().trim();
                String toCity = editTextToCity.getText().toString().trim();
                String toStreet = editTextToStreet.getText().toString().trim();
                String toHouse = editTextToHouse.getText().toString().trim();

                if (fromCity.isEmpty() || fromStreet.isEmpty() || fromHouse.isEmpty() ||
                        toCity.isEmpty() || toStreet.isEmpty() || toHouse.isEmpty()) {
                    return;
                }

                String route = "Откуда: " + fromCity + ", ул. " + fromStreet + ", д. " + fromHouse +
                        "\nКуда: " + toCity + ", ул. " + toStreet + ", д. " + toHouse;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("route", route);
                setResult(RESULT_OK, resultIntent);
                Log.d(TAG, "Маршрут передан: " + route);
                finish();
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