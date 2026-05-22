package com.example.geo;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geo.data.LocationDatabaseHelper;
import com.example.geo.data.LocationPoint;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private LocationDatabaseHelper dbHelper;
    private MapObjectCollection mapObjects;
    private Button btnRouteDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MapKitFactory.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        btnRouteDay = findViewById(R.id.btnRouteDay);

        dbHelper = new LocationDatabaseHelper(this);
        mapObjects = mapView.getMap().getMapObjects();

        // при старте не показываем ничего
        mapObjects.clear();

        // Клик по кнопке выбора дня
        btnRouteDay.setOnClickListener(v -> showDatePicker());
    }

    // ===============================
    // Выбор даты маршрута
    // ===============================
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar start = Calendar.getInstance();
                    start.set(year, month, dayOfMonth, 0, 0, 0);
                    start.set(Calendar.MILLISECOND, 0);

                    Calendar end = Calendar.getInstance();
                    end.set(year, month, dayOfMonth, 23, 59, 59);
                    end.set(Calendar.MILLISECOND, 999);

                    loadRouteForPeriod(start.getTimeInMillis(), end.getTimeInMillis());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ===============================
    // Загрузка маршрута
    // ===============================
    private void loadRouteForPeriod(long from, long to) {
        List<LocationPoint> points = dbHelper.getByPeriod(from, to);

        if (points.size() < 2) {
            Toast.makeText(this,
                    "Недостаточно данных за выбранный день",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        points.sort(Comparator.comparingLong(p -> p.timestamp));

        // очищаем старые объекты
        mapObjects.clear();

        drawRoute(points);
        drawVisitPoints(points); // только точки выбранного дня
    }

    // ===============================
    // Сглаживание маршрута
    // ===============================
    private List<Point> smoothRoute(List<LocationPoint> points) {
        List<Point> result = new ArrayList<>();
        if (points.size() < 2) return result;

        LocationPoint first = points.get(0);
        result.add(new Point(first.latitude, first.longitude));

        double lastLat = first.latitude;
        double lastLon = first.longitude;
        float[] results = new float[1];

        for (int i = 1; i < points.size(); i++) {
            LocationPoint current = points.get(i);
            android.location.Location.distanceBetween(
                    lastLat, lastLon,
                    current.latitude, current.longitude,
                    results
            );
            if (results[0] >= 15f) {
                result.add(new Point(current.latitude, current.longitude));
                lastLat = current.latitude;
                lastLon = current.longitude;
            }
        }
        return result;
    }

    // ===============================
    // Отрисовка маршрута
    // ===============================
    private void drawRoute(List<LocationPoint> points) {
        List<Point> polyPoints = smoothRoute(points);
        if (polyPoints.size() < 2) return;

        Polyline polylineObj = new Polyline(polyPoints);
        PolylineMapObject polyline = mapObjects.addPolyline(polylineObj);
        polyline.setStrokeColor(Color.BLUE);
        polyline.setStrokeWidth(6f);

        // старт
        PlacemarkMapObject start = mapObjects.addPlacemark(polyPoints.get(0));
        start.setIconStyle(new IconStyle().setScale(1.8f));

        // финиш
        PlacemarkMapObject end = mapObjects.addPlacemark(polyPoints.get(polyPoints.size() - 1));
        end.setIconStyle(new IconStyle().setScale(1.6f));

        // автоцентрирование по маршруту
        mapView.getMap().move(
                new CameraPosition(polyPoints.get(0), 14f, 0, 0)
        );
    }

    // ===============================
    // Отображение точек посещений выбранного дня
    // ===============================
    private void drawVisitPoints(List<LocationPoint> points) {
        List<LocationPoint> filteredPoints = new ArrayList<>();
        float[] results = new float[1];

        for (LocationPoint point : points) {
            boolean tooClose = false;
            for (LocationPoint existing : filteredPoints) {
                android.location.Location.distanceBetween(
                        existing.latitude, existing.longitude,
                        point.latitude, point.longitude,
                        results
                );
                if (results[0] < 15f) {
                    tooClose = true;
                    break;
                }
            }
            if (!tooClose) filteredPoints.add(point);
        }

        for (LocationPoint lp : filteredPoints) {
            PlacemarkMapObject placemark = mapObjects.addPlacemark(new Point(lp.latitude, lp.longitude));
            placemark.setIconStyle(new IconStyle().setScale(1f));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}