package com.example.geotracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment {

    private MapView mapView;
    private TextView tvStats, tvPointsCount;
    private LinearLayout periodButtonsLayout;
    private Button btnDay, btnWeek, btnMonth, btnAll;

    private AppDatabase database;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final int MIN_POINTS_REQUIRED = 30;
    private List<LocationPoint> allPoints = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Configuration.getInstance().load(getContext(),
                android.preference.PreferenceManager.getDefaultSharedPreferences(getContext()));

        initViews(view);
        setupMap();
        setupClickListeners();

        database = AppDatabase.getInstance(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAllPoints();
    }

    private void initViews(View view) {
        mapView = view.findViewById(R.id.map);
        tvStats = view.findViewById(R.id.tvStats);
        tvPointsCount = view.findViewById(R.id.tvPointsCount);
        periodButtonsLayout = view.findViewById(R.id.periodButtonsLayout);
        btnDay = view.findViewById(R.id.btnDay);
        btnWeek = view.findViewById(R.id.btnWeek);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnAll = view.findViewById(R.id.btnAll);
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        GeoPoint brest = new GeoPoint(52.0976, 23.7341);
        mapView.getController().setZoom(12.0);
        mapView.getController().setCenter(brest);
    }

    private void setupClickListeners() {
        btnDay.setOnClickListener(v -> showRouteForPeriod("day"));
        btnWeek.setOnClickListener(v -> showRouteForPeriod("week"));
        btnMonth.setOnClickListener(v -> showRouteForPeriod("month"));
        btnAll.setOnClickListener(v -> showRouteForPeriod("all"));
    }

    private void loadAllPoints() {
        executorService.execute(() -> {
            try {
                allPoints = database.locationDao().getAllLocations();

                mainHandler.post(() -> {
                    updatePointsCount();

                    // Если точек достаточно, показываем маршрут за неделю по умолчанию
                    if (allPoints.size() >= MIN_POINTS_REQUIRED) {
                        showRouteForPeriod("week");
                        Toast.makeText(getContext(),
                                "✅ " + allPoints.size() + " точек собрано! Маршрут за неделю построен",
                                Toast.LENGTH_LONG).show();
                    } else {
                        tvStats.setText("Нужно еще " + (MIN_POINTS_REQUIRED - allPoints.size()) +
                                " точек для построения маршрутов");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updatePointsCount() {
        String countText = "📊 Собрано точек: " + allPoints.size() +
                (allPoints.size() >= MIN_POINTS_REQUIRED ? " ✅" : " ⏳");
        tvPointsCount.setText(countText);
    }

    private void showRouteForPeriod(String period) {
        if (allPoints.size() < MIN_POINTS_REQUIRED) {
            Toast.makeText(getContext(),
                    "Нужно собрать минимум 30 точек (сейчас " + allPoints.size() + ")",
                    Toast.LENGTH_LONG).show();
            return;
        }

        executorService.execute(() -> {
            try {
                List<LocationPoint> filteredPoints = filterPointsByPeriod(period);

                mainHandler.post(() -> {
                    drawRoute(filteredPoints, period);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<LocationPoint> filterPointsByPeriod(String period) {
        List<LocationPoint> filtered = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        long startTime;

        switch (period) {
            case "day":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startTime = calendar.getTimeInMillis();
                break;

            case "week":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startTime = calendar.getTimeInMillis();
                break;

            case "month":
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                startTime = calendar.getTimeInMillis();
                break;

            case "all":
            default:
                // Все точки
                return allPoints;
        }

        for (LocationPoint point : allPoints) {
            if (point.getTimestamp() >= startTime && point.getTimestamp() <= now) {
                filtered.add(point);
            }
        }

        return filtered;
    }

    private void drawRoute(List<LocationPoint> points, String period) {
        mapView.getOverlays().clear();

        if (points.isEmpty()) {
            Toast.makeText(getContext(), "Нет точек за выбранный период", Toast.LENGTH_SHORT).show();
            return;
        }

        // Сортируем по времени (от старых к новым)
        points.sort((p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));

        List<GeoPoint> geoPoints = new ArrayList<>();

        // Добавляем маркеры для каждой точки
        for (int i = 0; i < points.size(); i++) {
            LocationPoint point = points.get(i);
            GeoPoint geoPoint = new GeoPoint(point.getLatitude(), point.getLongitude());
            geoPoints.add(geoPoint);

            // Создаем маркер
            Marker marker = new Marker(mapView);
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            // Разные цвета для разных периодов
            if (period.equals("day")) {
                marker.setIcon(getResources().getDrawable(R.drawable.ic_marker_day));
            } else if (period.equals("week")) {
                marker.setIcon(getResources().getDrawable(R.drawable.ic_marker_week));
            } else {
                marker.setIcon(getResources().getDrawable(R.drawable.ic_marker_default));
            }

            // Информация о точке
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
            String dateStr = sdf.format(new Date(point.getTimestamp()));
            marker.setTitle("Точка #" + (i + 1));
            marker.setSnippet(dateStr + "\nТочность: " + point.getAccuracy() + " м");
            //marker.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));

            mapView.getOverlays().add(marker);
        }

        // Рисуем маршрут (линию)
        if (geoPoints.size() > 1) {
            Polyline route = new Polyline();
            route.setPoints(geoPoints);

            // Разные цвета для разных периодов
            if (period.equals("day")) {
                route.setColor(Color.parseColor("#FF9800")); // Оранжевый для дня
                route.setWidth(8f);
            } else if (period.equals("week")) {
                route.setColor(Color.parseColor("#2196F3")); // Синий для недели
                route.setWidth(6f);
            } else {
                route.setColor(Color.parseColor("#9C27B0")); // Фиолетовый для остальных
                route.setWidth(4f);
            }

            mapView.getOverlays().add(route);

            // Вычисляем общее расстояние
            double totalDistance = 0;
            for (int i = 0; i < geoPoints.size() - 1; i++) {
                float[] results = new float[1];
                android.location.Location.distanceBetween(
                        geoPoints.get(i).getLatitude(), geoPoints.get(i).getLongitude(),
                        geoPoints.get(i + 1).getLatitude(), geoPoints.get(i + 1).getLongitude(),
                        results
                );
                totalDistance += results[0];
            }

            String distanceText;
            if (totalDistance < 1000) {
                distanceText = String.format(Locale.getDefault(), "%.0f м", totalDistance);
            } else {
                distanceText = String.format(Locale.getDefault(), "%.2f км", totalDistance / 1000);
            }

            tvStats.setText(String.format(Locale.getDefault(),
                    "📅 %s: %d точек, расстояние %s",
                    getPeriodName(period), points.size(), distanceText));

        } else {
            tvStats.setText(String.format(Locale.getDefault(),
                    "📅 %s: всего 1 точка", getPeriodName(period)));
        }

        // Центрируем карту на первой точке
        if (!geoPoints.isEmpty()) {
            mapView.getController().setZoom(14.0);
            mapView.getController().setCenter(geoPoints.get(0));
        }

        mapView.invalidate();
    }

    private String getPeriodName(String period) {
        switch (period) {
            case "day": return "За день";
            case "week": return "За неделю";
            case "month": return "За месяц";
            case "all": return "Все время";
            default: return period;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        // Обновляем данные при возвращении на фрагмент
        loadAllPoints();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }
}