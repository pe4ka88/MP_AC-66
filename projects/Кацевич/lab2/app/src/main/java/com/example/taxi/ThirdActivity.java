package com.example.taxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ThirdActivity extends AppCompatActivity {

    private EditText etFromStreet, etFromHouse, etFromEntrance;
    private EditText etToStreet, etToHouse, etToEntrance;
    private Button btnDetectLocation, btnSelectOnMap, btnConfirmMapSelection, btnOk;
    private FrameLayout mapContainer;
    private MapView mapView;

    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    private Marker selectedMarker;
    private GeoPoint selectedGeoPoint;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "ThirdActivity_Lifecycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Важно: инициализация osmdroid конфигурации
        Configuration.getInstance().load(getApplicationContext(),
                android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_third);

        Log.d(TAG, "onCreate вызван");

        // Инициализация View
        etFromStreet = findViewById(R.id.etFromStreet);
        etFromHouse = findViewById(R.id.etFromHouse);
        etFromEntrance = findViewById(R.id.etFromEntrance);
        etToStreet = findViewById(R.id.etToStreet);
        etToHouse = findViewById(R.id.etToHouse);
        etToEntrance = findViewById(R.id.etToEntrance);
        btnDetectLocation = findViewById(R.id.btnDetectLocation);
        btnSelectOnMap = findViewById(R.id.btnSelectOnMap);
        btnConfirmMapSelection = findViewById(R.id.btnConfirmMapSelection);
        btnOk = findViewById(R.id.btnOk);
        mapContainer = findViewById(R.id.mapContainer);
        mapView = findViewById(R.id.map);

        // Инициализация клиента геолокации
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        // Настройка карты OSM
        setupMap();

        // Кнопка определения местоположения (откуда ехать)
        btnDetectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermissionAndGetLocation(false);
            }
        });

        // Кнопка выбора на карте (куда ехать)
        btnSelectOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapContainer.setVisibility(View.VISIBLE);
                btnSelectOnMap.setEnabled(false);
            }
        });

        // Кнопка подтверждения выбора на карте
        btnConfirmMapSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGeoPoint != null) {
                    getAddressFromLocation(selectedGeoPoint.getLatitude(),
                            selectedGeoPoint.getLongitude(), true);
                    mapContainer.setVisibility(View.GONE);
                    btnSelectOnMap.setEnabled(true);
                    selectedMarker = null;
                    selectedGeoPoint = null;
                } else {
                    Toast.makeText(ThirdActivity.this,
                            "👆 Нажмите на карте, чтобы выбрать точку",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Кнопка ОК
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromStreet = etFromStreet.getText().toString().trim();
                String fromHouse = etFromHouse.getText().toString().trim();
                String fromEntrance = etFromEntrance.getText().toString().trim();
                String toStreet = etToStreet.getText().toString().trim();
                String toHouse = etToHouse.getText().toString().trim();
                String toEntrance = etToEntrance.getText().toString().trim();

                if (fromStreet.isEmpty() || fromHouse.isEmpty() ||
                        toStreet.isEmpty() || toHouse.isEmpty()) {

                    Toast.makeText(ThirdActivity.this,
                            "Заполните обязательные поля: улица и дом (откуда и куда)",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String route = "Откуда: " + fromStreet + ", д. " + fromHouse;
                if (!fromEntrance.isEmpty()) {
                    route += ", п. " + fromEntrance;
                }
                route += " → Куда: " + toStreet + ", д. " + toHouse;
                if (!toEntrance.isEmpty()) {
                    route += ", п. " + toEntrance;
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("ROUTE_POINTS", route);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    /**
     * Настройка карты OSM
     */
    private void setupMap() {
        // Источник карт (используем стандартный MAPNIK)
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Включение мультитач (масштабирование двумя пальцами)
        mapView.setMultiTouchControls(true);

        // Включение встроенных кнопок масштабирования
        mapView.setBuiltInZoomControls(true);

        // Установка начальной позиции (центр Минска, БГУИР)
        GeoPoint startPoint = new GeoPoint(53.9096, 27.5933); // пр-т Независимости, 4
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);

        // Обработка касаний карты [citation:8]
        mapView.getOverlayManager().add(new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(android.view.MotionEvent e, MapView mapView) {
                // Получаем координаты точки касания
                GeoPoint point = (GeoPoint) mapView.getProjection().fromPixels(
                        (int) e.getX(), (int) e.getY());

                // Удаляем предыдущий маркер
                if (selectedMarker != null) {
                    mapView.getOverlays().remove(selectedMarker);
                }

                // Создаем новый маркер
                selectedMarker = new Marker(mapView);
                selectedMarker.setPosition(point);
                selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                selectedMarker.setTitle("Выбранная точка");

                // Кастомная иконка (можно заменить на свою)
                selectedMarker.setIcon(getResources().getDrawable(
                        org.osmdroid.library.R.drawable.osm_ic_follow_me_on));

                mapView.getOverlays().add(selectedMarker);
                mapView.invalidate();

                selectedGeoPoint = point;

                Log.d(TAG, "Выбрана точка: " + point.getLatitude() + ", " + point.getLongitude());

                return true;
            }
        });

        // Добавляем слой с текущим местоположением [citation:8]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(this), mapView);
            locationOverlay.enableMyLocation();
            mapView.getOverlays().add(locationOverlay);
        }
    }

    /**
     * Проверка разрешений и получение местоположения
     * @param isDestination true - для поля "Куда", false - для поля "Откуда"
     */
    private void checkLocationPermissionAndGetLocation(boolean isDestination) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation(isDestination);
        }
    }

    /**
     * Получение последнего известного местоположения
     */
    private void getLastLocation(boolean isDestination) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        btnDetectLocation.setEnabled(false);
        btnDetectLocation.setText("⏳ Определение...");

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        btnDetectLocation.setEnabled(true);
                        btnDetectLocation.setText("📍 Определить мое местоположение");

                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            getAddressFromLocation(location.getLatitude(),
                                    location.getLongitude(), false);
                        } else {
                            Toast.makeText(ThirdActivity.this,
                                    "❌ Не удалось получить местоположение. Включите GPS и попробуйте снова.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Получение адреса по координатам
     */
    private void getAddressFromLocation(double latitude, double longitude, boolean isDestination) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String street = address.getThoroughfare();
                String house = address.getSubThoroughfare();

                if (isDestination) {
                    if (street != null) etToStreet.setText(street);
                    if (house != null) etToHouse.setText(house);
                    else etToHouse.setText("?");
                } else {
                    if (street != null) etFromStreet.setText(street);
                    if (house != null) etFromHouse.setText(house);
                    else etFromHouse.setText("?");
                }

                Toast.makeText(this,
                        "✅ Адрес: " + address.getAddressLine(0),
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "⚠️ Ошибка геокодирования", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(false);
                // Обновляем слой с местоположением на карте
                MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(
                        new GpsMyLocationProvider(this), mapView);
                locationOverlay.enableMyLocation();
                mapView.getOverlays().add(locationOverlay);
            } else {
                Toast.makeText(this,
                        "❌ Разрешение на геолокацию не получено",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart вызван");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume вызван");
        if (mapView != null) {
            mapView.onResume(); // Важно для работы OSM
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause вызван");
        if (mapView != null) {
            mapView.onPause(); // Важно для работы OSM
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop вызван");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy вызван");
    }
}