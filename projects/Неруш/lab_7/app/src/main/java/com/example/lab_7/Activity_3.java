package com.example.lab_7;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Activity_3 extends AppCompatActivity {

    EditText editStartPoint, editEndPoint, editDepartureTime, editArriveTime, editPrice, editDistanse;
    Button btnOk, btnGetLocation, btnShowMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private Calendar departureCalendar = Calendar.getInstance();

    private final ActivityResultLauncher<Intent> mapActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String address = result.getData().getStringExtra("address");
                    if (address != null) {
                        editEndPoint.setText(address);
                        calculateRouteDetails();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layer_three_activity);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editStartPoint = findViewById(R.id.editStartPoint);
        editEndPoint = findViewById(R.id.editEndPoint);
        editDepartureTime = findViewById(R.id.editDepartureTime);
        editArriveTime = findViewById(R.id.editArriveTime);
        editPrice = findViewById(R.id.editPrice);
        editDistanse = findViewById(R.id.editDistanse);

        btnOk = findViewById(R.id.btnOk);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnShowMap = findViewById(R.id.btnShowMap);

        btnOk.setEnabled(false);

        editStartPoint.setInputType(InputType.TYPE_CLASS_TEXT);
        editEndPoint.setInputType(InputType.TYPE_CLASS_TEXT);
        
        // Make DepartureTime read-only so it can only be changed via picker
        editDepartureTime.setInputType(InputType.TYPE_NULL);
        editDepartureTime.setFocusable(false);
        editDepartureTime.setOnClickListener(v -> showDateTimePicker());

        editArriveTime.setInputType(InputType.TYPE_CLASS_TEXT);
        editPrice.setInputType(InputType.TYPE_CLASS_TEXT);
        editDistanse.setInputType(InputType.TYPE_CLASS_TEXT);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        editStartPoint.addTextChangedListener(textWatcher);
        editEndPoint.addTextChangedListener(textWatcher);
        editDepartureTime.addTextChangedListener(textWatcher);
        editArriveTime.addTextChangedListener(textWatcher);
        editPrice.addTextChangedListener(textWatcher);
        editDistanse.addTextChangedListener(textWatcher);

        btnGetLocation.setOnClickListener(v -> getLastLocation());

        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_3.this, MapsActivity.class);
            mapActivityResultLauncher.launch(intent);
        });

        btnOk.setOnClickListener(v -> {
            String route = "Start: " + editStartPoint.getText().toString() +
                    ", End: " + editEndPoint.getText().toString() +
                    ", Departure time: " + editDepartureTime.getText().toString() +
                    ", Arrive time: " + editArriveTime.getText().toString() +
                    ", Price: " + editPrice.getText().toString() +
                    ", Distance: " + editDistanse.getText().toString();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("Route", route);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        Log.d("Lifecycle", "onCreate called in Activity_3");
    }

    private void showDateTimePicker() {
        Calendar current = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            departureCalendar.set(Calendar.YEAR, year);
            departureCalendar.set(Calendar.MONTH, month);
            departureCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                departureCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                departureCalendar.set(Calendar.MINUTE, minute);
                departureCalendar.set(Calendar.SECOND, 0);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
                editDepartureTime.setText(sdf.format(departureCalendar.getTime()));
                calculateRouteDetails();
            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true).show();

        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void calculateRouteDetails() {
        String startStr = editStartPoint.getText().toString();
        String endStr = editEndPoint.getText().toString();
        String depTimeStr = editDepartureTime.getText().toString();

        if (startStr.isEmpty() || endStr.isEmpty() || depTimeStr.isEmpty()) return;

        new Thread(() -> {
            Location startLoc = getLocationFromAddress(startStr);
            Location endLoc = getLocationFromAddress(endStr);

            if (startLoc != null && endLoc != null) {
                float distanceMeters = startLoc.distanceTo(endLoc);
                float distanceKm = distanceMeters / 1000;

                double pricePerKm = 1.2;
                double avgSpeedKmH = 40.0;

                double totalPrice = distanceKm * pricePerKm;
                double travelTimeHours = distanceKm / avgSpeedKmH;

                runOnUiThread(() -> {
                    editDistanse.setText(String.format(Locale.US, "%.2f km", distanceKm));
                    editPrice.setText(String.format(Locale.US, "%.2f BYN", totalPrice));

                    Calendar arrivalCalendar = (Calendar) departureCalendar.clone();
                    int travelTotalMinutes = (int) Math.round(travelTimeHours * 60);
                    arrivalCalendar.add(Calendar.MINUTE, travelTotalMinutes);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
                    editArriveTime.setText(sdf.format(arrivalCalendar.getTime()));
                });
            }
        }).start();
    }

    private Location getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Location location = new Location("");

        try {
            if (strAddress.contains(",")) {
                String[] parts = strAddress.split(",");
                if (parts.length >= 2) {
                    try {
                        location.setLatitude(Double.parseDouble(parts[0].trim()));
                        location.setLongitude(Double.parseDouble(parts[1].trim()));
                        return location;
                    } catch (NumberFormatException ignored) {}
                }
            }

            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address locationAddress = address.get(0);
            location.setLatitude(locationAddress.getLatitude());
            location.setLongitude(locationAddress.getLongitude());
            return location;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            getAddressFromLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(Activity_3.this, "Unable to find location. Make sure GPS is ON.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "Geocoder not available", Toast.LENGTH_SHORT).show();
            editStartPoint.setText(latitude + ", " + longitude);
            calculateRouteDetails();
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                editStartPoint.setText(addressText);
            } else {
                editStartPoint.setText(latitude + ", " + longitude);
            }
            calculateRouteDetails();
        } catch (IOException e) {
            e.printStackTrace();
            editStartPoint.setText(latitude + ", " + longitude);
            calculateRouteDetails();
            Toast.makeText(this, "Geocoder error, using coordinates", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkFieldsForEmptyValues() {
        String startPoint = editStartPoint.getText().toString();
        String endPoint = editEndPoint.getText().toString();
        String departureTime = editDepartureTime.getText().toString();
        String arriveTime = editArriveTime.getText().toString();
        String price = editPrice.getText().toString();
        String distance = editDistanse.getText().toString();

        btnOk.setEnabled(!startPoint.isEmpty() && !endPoint.isEmpty() &&
                !departureTime.isEmpty() && !arriveTime.isEmpty() &&
                !price.isEmpty() && !distance.isEmpty());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle", "onStart called in Activity_3");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume called in Activity_3");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause called in Activity_3");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle", "onStop called in Activity_3");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "onDestroy called in Activity_3");
    }
}
