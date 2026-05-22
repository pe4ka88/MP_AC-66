package com.example.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "ThirdActivityLifecycle";

    private EditText etStreet, etHouse, etFlat, etDestStreet, etDestHouse, etDestFlat;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Log.d(TAG, "onCreate");

        etStreet = findViewById(R.id.etStreet);
        etHouse = findViewById(R.id.etHouse);
        etFlat = findViewById(R.id.etFlat);
        etDestStreet = findViewById(R.id.etDestStreet);
        etDestHouse = findViewById(R.id.etDestHouse);
        etDestFlat = findViewById(R.id.etDestFlat);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String street = etStreet.getText().toString();
                String house = etHouse.getText().toString();
                String flat = etFlat.getText().toString();
                String dStreet = etDestStreet.getText().toString();
                String dHouse = etDestHouse.getText().toString();
                String dFlat = etDestFlat.getText().toString();

                String route = "From: " + street + " " + house + "-" + flat + 
                               " To: " + dStreet + " " + dHouse + "-" + dFlat;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("route", route);
                setResult(RESULT_OK, resultIntent);
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
