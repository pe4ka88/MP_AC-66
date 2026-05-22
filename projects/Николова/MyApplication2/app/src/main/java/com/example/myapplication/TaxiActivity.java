package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class TaxiActivity extends AppCompatActivity {
    private static final String TAG = "Lifecycle_Third";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);
        Log.d(TAG, "onCreate");

        EditText etCityFrom = findViewById(R.id.etCityFrom);
        EditText etStreetFrom = findViewById(R.id.etStreetFrom);
        EditText etHouseFrom = findViewById(R.id.etHouseFrom);
        EditText etCityTo = findViewById(R.id.etCityTo);
        EditText etStreetTo = findViewById(R.id.etStreetTo);
        EditText etHouseTo = findViewById(R.id.etHouseTo);

        findViewById(R.id.btnOk).setOnClickListener(v -> {
            String locationInfo = getCurrentLocation();

            String resultPath = "From: " + etCityFrom.getText() + " " + etStreetFrom.getText() + " " + etHouseFrom.getText() + "\n" +
                    "To: " + etCityTo.getText() + " " + etStreetTo.getText() + " " + etHouseTo.getText() + "\n" +
                    "Coordinates: " + locationInfo;

            Intent resultIntent = new Intent();
            resultIntent.putExtra("full_path", resultPath);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private String getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return "Нет разрешения на GPS";
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            return lat + ", " + lon;
        } else {
            return "Не удалось определить координаты";
        }
    }



    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}