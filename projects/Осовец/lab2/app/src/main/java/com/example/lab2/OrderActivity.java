package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";

    private TextView tvUserName, tvUserPhone, tvRoute;
    private Button btnSetPath, btnCallTaxi;

    private ActivityResultLauncher<Intent> routeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvRoute = findViewById(R.id.tvRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        // Receive data from MainActivity
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");

        tvUserName.setText(getString(R.string.user_name_format, firstName, lastName));
        tvUserPhone.setText(getString(R.string.user_phone_format, phone));

        // Register activity result launcher (replacement for deprecated startActivityForResult)
        routeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String cityFrom = data.getStringExtra("cityFrom");
                        String streetFrom = data.getStringExtra("streetFrom");
                        String buildingFrom = data.getStringExtra("buildingFrom");
                        String cityTo = data.getStringExtra("cityTo");
                        String streetTo = data.getStringExtra("streetTo");
                        String buildingTo = data.getStringExtra("buildingTo");

                        String routeInfo = getString(R.string.route_info_format,
                                cityFrom, streetFrom, buildingFrom,
                                cityTo, streetTo, buildingTo);
                        tvRoute.setText(routeInfo);

                        // Enable Call Taxi button
                        btnCallTaxi.setEnabled(true);
                    }
                }
        );

        // Set path button — implicit intent to RouteActivity
        btnSetPath.setOnClickListener(v -> {
            Intent routeIntent = new Intent("com.example.lab2.ACTION_SET_ROUTE");
            routeIntent.addCategory(Intent.CATEGORY_DEFAULT);
            routeLauncher.launch(routeIntent);
        });

        // Call Taxi button — show toast
        btnCallTaxi.setOnClickListener(v -> {
            Toast.makeText(OrderActivity.this,
                    R.string.taxi_called_message, Toast.LENGTH_LONG).show();
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
