package com.example.lab8;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private MapView mapView;
    private List<LocationFix> visitedPoints;
    private List<LocationFix> allRoutes;  // Новый список для хранения всех маршрутов.

    private final String author = "Цеван Константин";
    private final String group = "АС-66";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());

        checkLocationPermission();

        visitedPoints = new ArrayList<>();
        allRoutes = new ArrayList<>();  // Инициализация нового списка маршрутов.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LinearLayout rootLayout = createMainLayout();
        setContentView(rootLayout);

        setupMap();
        addRouteToMap();
        addMarkersToMap();

        Toast.makeText(
                this,
                "ЛР8. Геолокация. Разработал " + author + ", " + group,
                Toast.LENGTH_LONG
        ).show();
    }

    private LinearLayout createMainLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Лабораторная работа №8\nГеолокационные возможности");
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setBackgroundColor(Color.rgb(21, 101, 192));
        title.setPadding(12, 18, 12, 18);

        TextView authorText = new TextView(this);
        authorText.setText("Разработал: " + author + ", группа " + group);
        authorText.setTextSize(16);
        authorText.setTextColor(Color.WHITE);
        authorText.setGravity(Gravity.CENTER);
        authorText.setBackgroundColor(Color.rgb(13, 71, 161));
        authorText.setPadding(8, 10, 8, 10);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(8, 8, 8, 8);
        buttonLayout.setGravity(Gravity.CENTER);

        Button addButton = new Button(this);
        addButton.setText("Отметить позицию");
        addButton.setAllCaps(false);
        addButton.setOnClickListener(v -> markCurrentPosition());

        Button viewButton = new Button(this);
        viewButton.setText("Просмотр фиксаций");
        viewButton.setAllCaps(false);
        viewButton.setOnClickListener(v -> showPointsInfo());

        Button routesButton = new Button(this);
        routesButton.setText("Просмотр маршрутов");
        routesButton.setAllCaps(false);
        routesButton.setOnClickListener(v -> showAllRoutesInfo());

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        buttonParams.setMargins(4, 0, 4, 0);

        buttonLayout.addView(addButton, buttonParams);
        buttonLayout.addView(viewButton, buttonParams);
        buttonLayout.addView(routesButton, buttonParams);

        mapView = new MapView(this);
        LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        );

        root.addView(title);
        root.addView(authorText);
        root.addView(buttonLayout);
        root.addView(mapView, mapParams);

        return root;
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getZoomController().setVisibility(
                CustomZoomButtonsController.Visibility.ALWAYS
        );

        GeoPoint homePoint = new GeoPoint(52.09390, 23.76199);  // Точка дома

        mapView.getController().setZoom(13.0);
        mapView.getController().setCenter(homePoint);
    }

    private void markCurrentPosition() {
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        locationListener
                );
            }
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String place = "Местоположение: " + latitude + ", " + longitude;

            visitedPoints.add(new LocationFix(latitude, longitude, place, timestamp));
            allRoutes.add(new LocationFix(latitude, longitude, place, timestamp)); // Добавляем в список всех маршрутов.

            Toast.makeText(MainActivity.this, "Точка добавлена!", Toast.LENGTH_SHORT).show();
            addRouteToMap();
            addMarkersToMap();

            locationManager.removeUpdates(this);  // Останавливаем получение координат
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void addRouteToMap() {
        Polyline routeLine = new Polyline();

        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        for (LocationFix location : visitedPoints) {
            routePoints.add(new GeoPoint(location.latitude, location.longitude));
        }

        routeLine.setPoints(routePoints);
        routeLine.setColor(Color.rgb(25, 118, 210));
        routeLine.setWidth(6.0f);
        routeLine.setTitle("Маршрут движения за неделю. Разработал: " + author + ", " + group);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();
    }

    private void addMarkersToMap() {
        for (int i = 0; i < visitedPoints.size(); i++) {
            LocationFix location = visitedPoints.get(i);

            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(location.latitude, location.longitude));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setTitle("Фиксация №" + (i + 1) + " — " + location.place);
            marker.setSubDescription(
                    "Время фиксации: " + location.timestamp + "\n" +
                            "Место: " + location.place + "\n" +
                            "Автор: " + author + ", " + group + "\n" +
                            "Широта: " + location.latitude + "\n" +
                            "Долгота: " + location.longitude
            );

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();
    }

    private void showPointsInfo() {
        StringBuilder builder = new StringBuilder();

        builder.append("Количество фиксаций: ")
                .append(visitedPoints.size())
                .append("\n\n");

        for (int i = 0; i < visitedPoints.size(); i++) {
            LocationFix location = visitedPoints.get(i);

            builder.append(i + 1)
                    .append(") ")
                    .append(location.timestamp)
                    .append("\n")
                    .append("Место: ")
                    .append(location.place)
                    .append("\n")
                    .append("Координаты: ")
                    .append(location.latitude)
                    .append(", ")
                    .append(location.longitude)
                    .append("\n\n");
        }

        builder.append("Разработал: ")
                .append(author)
                .append(", ")
                .append(group);

        new AlertDialog.Builder(this)
                .setTitle("30 фиксаций за неделю")
                .setMessage(builder.toString())
                .setPositiveButton("Понятно", null)
                .show();
    }

    private void showAllRoutesInfo() {
        StringBuilder builder = new StringBuilder();

        builder.append("Общий маршрут: \n");

        for (int i = 0; i < allRoutes.size(); i++) {
            LocationFix location = allRoutes.get(i);

            builder.append(i + 1)
                    .append(") ")
                    .append(location.timestamp)
                    .append("\n")
                    .append("Место: ")
                    .append(location.place)
                    .append("\n")
                    .append("Координаты: ")
                    .append(location.latitude)
                    .append(", ")
                    .append(location.longitude)
                    .append("\n\n");
        }

        builder.append("Разработал: ")
                .append(author)
                .append(", ")
                .append(group);

        new AlertDialog.Builder(this)
                .setTitle("Просмотр маршрутов")
                .setMessage(builder.toString())
                .setPositiveButton("Понятно", null)
                .show();
    }

    private void showTaskDialog() {
        String taskText =
                "Лабораторная работа №8\n\n" +
                        "Тема: Геолокационные возможности.\n\n" +
                        "Задача приложения:\n" +
                        "1. Отобразить карту с выбранными локациями за некоторый период времени.\n" +
                        "2. Отобразить маршрут движения пользователя.\n" +
                        "3. Отобразить посещённые места за некоторый период времени, не менее 30 фиксаций.\n\n" +
                        "В приложении используются 30 фиксаций координат по Бресту " +
                        "с указанием даты, времени и места посещения.\n\n" +
                        "Выполнил: " + author + "\n" +
                        "Группа: " + group + "\n\n" +
                        "Средства реализации:\n" +
                        "Android Studio, язык Java, библиотека osmdroid, карты OpenStreetMap.";

        new AlertDialog.Builder(this)
                .setTitle("Формулировка задачи")
                .setMessage(taskText)
                .setPositiveButton("Закрыть", null)
                .show();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mapView != null) {
            mapView.onPause();
        }
    }

    public static class LocationFix {
        double latitude;
        double longitude;
        String place;
        String timestamp;

        public LocationFix(double latitude, double longitude, String place, String timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.place = place;
            this.timestamp = timestamp;
        }
    }
}