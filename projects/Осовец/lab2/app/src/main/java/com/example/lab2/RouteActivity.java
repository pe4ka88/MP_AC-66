package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RouteActivity extends AppCompatActivity {

    private static final String TAG = "RouteActivity";

    private EditText editCityFrom, editStreetFrom, editBuildingFrom;
    private EditText editCityTo, editStreetTo, editBuildingTo;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_route);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editCityFrom = findViewById(R.id.editCityFrom);
        editStreetFrom = findViewById(R.id.editStreetFrom);
        editBuildingFrom = findViewById(R.id.editBuildingFrom);
        editCityTo = findViewById(R.id.editCityTo);
        editStreetTo = findViewById(R.id.editStreetTo);
        editBuildingTo = findViewById(R.id.editBuildingTo);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("cityFrom", editCityFrom.getText().toString().trim());
            resultIntent.putExtra("streetFrom", editStreetFrom.getText().toString().trim());
            resultIntent.putExtra("buildingFrom", editBuildingFrom.getText().toString().trim());
            resultIntent.putExtra("cityTo", editCityTo.getText().toString().trim());
            resultIntent.putExtra("streetTo", editStreetTo.getText().toString().trim());
            resultIntent.putExtra("buildingTo", editBuildingTo.getText().toString().trim());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Task info button
        findViewById(R.id.btnTaskInfo).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.task_description_title)
                    .setMessage(getString(R.string.task_description))
                    .setPositiveButton("OK", null)
                    .show();
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
