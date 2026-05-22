package com.example.lab_2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ThirdActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "Lifecycle_ThirdActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText etStreet, etHouse, etFlat, etDestStreet, etDestHouse, etDestFlat;
    private Button btnOk, btnGetLocation;
    private MapView map;
    private Marker destMarker;
    private LocationManager locationManager;
    private MyLocationNewOverlay mLocationOverlay;
    private final Locale russianLocale = new Locale("ru");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Настройка osmdroid
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        
        setContentView(R.layout.activity_third);
        Log.d(TAG, "onCreate");

        etStreet = findViewById(R.id.et_street);
        etHouse = findViewById(R.id.et_house);
        etFlat = findViewById(R.id.et_flat);
        etDestStreet = findViewById(R.id.et_dest_street);
        etDestHouse = findViewById(R.id.et_dest_house);
        etDestFlat = findViewById(R.id.et_dest_flat);
        btnOk = findViewById(R.id.btn_ok);
        btnGetLocation = findViewById(R.id.btn_get_location);
        map = findViewById(R.id.map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setupMap();

        btnGetLocation.setOnClickListener(v -> requestLocation());

        btnOk.setOnClickListener(v -> {
            String route = "От: " + etStreet.getText() + ", " + etHouse.getText() + ", кв. " + etFlat.getText() +
                    " До: " + etDestStreet.getText() + ", " + etDestHouse.getText() + ", кв. " + etDestFlat.getText();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("route", route);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(17.0);
        GeoPoint startPoint = new GeoPoint(53.9, 27.56); // Минск по умолчанию
        mapController.setCenter(startPoint);

        // Слой текущего местоположения
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation(); // Чтобы карта следовала за вами
        map.getOverlays().add(mLocationOverlay);

        // Обработка нажатий на карту (неявный слой должен быть ПОВЕРХ карты, но до маркеров)
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.d(TAG, "Map tapped at: " + p.getLatitude() + ", " + p.getLongitude());
                setDestinationFromMap(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        // ВАЖНО: Добавляем оверлей событий первым или в правильном порядке
        map.getOverlays().add(0, new MapEventsOverlay(mReceive));
    }

    private void setDestinationFromMap(GeoPoint p) {
        if (destMarker == null) {
            destMarker = new Marker(map);
            destMarker.setTitle("Пункт назначения");
            // Устанавливаем иконку явно, если стандартная не видна
            destMarker.setIcon(getResources().getDrawable(org.osmdroid.library.R.drawable.marker_default));
            map.getOverlays().add(destMarker);
        }
        destMarker.setPosition(p);
        destMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.invalidate();

        Geocoder geocoder = new Geocoder(this, russianLocale);
        try {
            List<Address> addresses = geocoder.getFromLocation(p.getLatitude(), p.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                etDestStreet.setText(addr.getThoroughfare() != null ? addr.getThoroughfare() : "Неизвестная ул.");
                etDestHouse.setText(addr.getSubThoroughfare() != null ? addr.getSubThoroughfare() : "");
            } else {
                etDestStreet.setText("Координаты: " + String.format("%.5f", p.getLatitude()));
                etDestHouse.setText(String.format("%.5f", p.getLongitude()));
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
            etDestStreet.setText("Ошибка карты");
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);
            }
            
            // Если точка уже известна оверлею - прыгаем к ней
            GeoPoint myLoc = mLocationOverlay.getMyLocation();
            if (myLoc != null) {
                map.getController().animateTo(myLoc);
                updateStartLocationFieldsFromPoint(myLoc);
            }
            
            Toast.makeText(this, "Поиск спутников...", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException", e);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        GeoPoint p = new GeoPoint(location.getLatitude(), location.getLongitude());
        updateStartLocationFieldsFromPoint(p);
        if (location.getAccuracy() < 20) { // Очень высокая точность
            locationManager.removeUpdates(this);
        }
    }

    private void updateStartLocationFieldsFromPoint(GeoPoint p) {
        map.getController().animateTo(p);
        
        Geocoder geocoder = new Geocoder(this, russianLocale);
        try {
            List<Address> addresses = geocoder.getFromLocation(p.getLatitude(), p.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                etStreet.setText(addr.getThoroughfare() != null ? addr.getThoroughfare() : "Ул. не найдена");
                etHouse.setText(addr.getSubThoroughfare() != null ? addr.getSubThoroughfare() : "");
            } else {
                etStreet.setText("Широта: " + p.getLatitude());
                etHouse.setText("Долгота: " + p.getLongitude());
            }
        } catch (IOException e) {
            etStreet.setText("Координаты: " + p.getLatitude());
            etHouse.setText("" + p.getLongitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        mLocationOverlay.enableMyLocation();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        mLocationOverlay.disableMyLocation();
        locationManager.removeUpdates(this);
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
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

    @Override
    public void onProviderEnabled(@NonNull String provider) {}
    @Override
    public void onProviderDisabled(@NonNull String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}