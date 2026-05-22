package com.example.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {
    private EditText editTextStreetFrom, editTextHouseFrom, editTextApartmentFrom;
    private EditText editTextStreetTo, editTextHouseTo, editTextApartmentTo;
    private Button buttonOk;
    private static final String TAG = "Lifecycle_ThirdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_third);

        editTextStreetFrom = findViewById(R.id.editTextStreetFrom);
        editTextHouseFrom = findViewById(R.id.editTextHouseFrom);
        editTextApartmentFrom = findViewById(R.id.editTextApartmentFrom);
        editTextStreetTo = findViewById(R.id.editTextStreetTo);
        editTextHouseTo = findViewById(R.id.editTextHouseTo);
        editTextApartmentTo = findViewById(R.id.editTextApartmentTo);
        buttonOk = findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("streetFrom", editTextStreetFrom.getText().toString());
                resultIntent.putExtra("houseFrom", editTextHouseFrom.getText().toString());
                resultIntent.putExtra("apartmentFrom", editTextApartmentFrom.getText().toString());
                resultIntent.putExtra("streetTo", editTextStreetTo.getText().toString());
                resultIntent.putExtra("houseTo", editTextHouseTo.getText().toString());
                resultIntent.putExtra("apartmentTo", editTextApartmentTo.getText().toString());
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