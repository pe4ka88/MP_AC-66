package com.example.myapplication8;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Фрагмент карты на базе OSMDroid (OpenStreetMap) — без API-ключа.
 *
 * Режимы:
 *   За неделю — маркеры + маршрут за 7 дней
 *   Маршрут   — последние 2-10 точек в пределах 1 дня
 *   Все точки — вся история из БД
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private MapView mapView;
    private TextView tvStats;
    private Button btnWeek, btnRoute, btnAll;

    private enum Mode { WEEK, ROUTE, ALL }
    private Mode currentMode = Mode.ALL;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    // ── Lifecycle ──────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Инициализация OSMDroid — обязательно до inflate
        Configuration.getInstance().setUserAgentValue(
                requireContext().getPackageName());

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStats  = view.findViewById(R.id.tvMapStats);
        btnWeek  = view.findViewById(R.id.btnModeWeek);
        btnRoute = view.findViewById(R.id.btnModeRoute);
        btnAll   = view.findViewById(R.id.btnModeAll);

        // Настройка карты
        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);  // OpenStreetMap тайлы
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.0);
        // Начальная позиция
        mapView.getController().setCenter(new GeoPoint(50.4501, 30.5234));

        btnWeek.setOnClickListener(v  -> { currentMode = Mode.WEEK;  refreshMap(); });
        btnRoute.setOnClickListener(v -> { currentMode = Mode.ROUTE; refreshMap(); });
        btnAll.setOnClickListener(v   -> { currentMode = Mode.ALL;   refreshMap(); });

        refreshMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    // ── Отрисовка ──────────────────────────────────────────────────────

    public void refreshMap() {
        if (mapView == null) return;

        // Очищаем старые оверлеи
        mapView.getOverlays().clear();

        List<LocationEntry> entries = loadEntries();
        Log.d(TAG, "refreshMap: mode=" + currentMode + ", entries=" + entries.size());

        if (entries.isEmpty()) {
            tvStats.setText("Нет данных для отображения");
            mapView.invalidate();
            return;
        }

        List<GeoPoint> points = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            LocationEntry e = entries.get(i);
            GeoPoint gp = new GeoPoint(e.getLatitude(), e.getLongitude());
            points.add(gp);

            // Маркер
            Marker marker = new Marker(mapView);
            marker.setPosition(gp);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            String title = (e.getPlaceName() != null && !e.getPlaceName().isEmpty())
                    ? e.getPlaceName()
                    : "Точка #" + (i + 1);
            marker.setTitle(title);
            marker.setSnippet(formatTime(e.getTimestamp()));

            // Цвет иконки: зелёная — первая, красная — последняя, синяя — остальные
            if (i == 0) {
                marker.setIcon(tintedMarker(Color.rgb(46, 125, 50)));   // зелёный
            } else if (i == entries.size() - 1) {
                marker.setIcon(tintedMarker(Color.rgb(198, 40, 40)));   // красный
            } else {
                marker.setIcon(tintedMarker(Color.rgb(21, 101, 192)));  // синий
            }

            mapView.getOverlays().add(marker);
        }

        // Маршрут (полилиния)
        if (points.size() >= 2) {
            Polyline polyline = new Polyline();
            polyline.setPoints(points);
            polyline.getOutlinePaint().setColor(Color.parseColor("#1565C0"));
            polyline.getOutlinePaint().setStrokeWidth(6f);
            mapView.getOverlays().add(0, polyline); // под маркерами
        }

        // Статистика
        tvStats.setText(buildStats(entries));

        // Переместить камеру так, чтобы все точки влезли
        zoomToBounds(points);

        mapView.invalidate();
    }

    // ── Цветной маркер через ColorFilter ──────────────────────────────

    private android.graphics.drawable.Drawable tintedMarker(int color) {
        android.graphics.drawable.Drawable d = androidx.core.content.ContextCompat
                .getDrawable(requireContext(),
                        org.osmdroid.library.R.drawable.marker_default);
        if (d == null) return null;
        d = d.mutate();
        d.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
        return d;
    }

    // ── Автозум ────────────────────────────────────────────────────────

    private void zoomToBounds(List<GeoPoint> points) {
        if (points.isEmpty()) return;
        if (points.size() == 1) {
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(points.get(0));
            return;
        }

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
        for (GeoPoint p : points) {
            minLat = Math.min(minLat, p.getLatitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            minLon = Math.min(minLon, p.getLongitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }

        // Небольшой отступ
        double pad = 0.01;
        BoundingBox box = new BoundingBox(maxLat + pad, maxLon + pad,
                minLat - pad, minLon - pad);
        mapView.post(() -> {
            try {
                mapView.zoomToBoundingBox(box, true, 80);
            } catch (Exception ex) {
                Log.e(TAG, "zoomToBoundingBox: " + ex.getMessage());
                mapView.getController().setZoom(12.0);
                mapView.getController().setCenter(points.get(0));
            }
        });
    }

    // ── Загрузка данных ────────────────────────────────────────────────

    private List<LocationEntry> loadEntries() {
        DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
        long now     = System.currentTimeMillis();
        long weekAgo = now - 7L * 24 * 3600 * 1000;

        List<LocationEntry> result;
        switch (currentMode) {
            case WEEK:
                result = db.getLocationsBetween(weekAgo, now);
                if (result.isEmpty()) {
                    Log.w(TAG, "WEEK пусто → fallback ALL");
                    result = db.getAllLocations();
                }
                return result;
            case ROUTE:
                return db.getRouteForLatestDay();
            case ALL:
            default:
                return db.getAllLocations();
        }
    }

    // ── Форматирование ─────────────────────────────────────────────────

    private String formatTime(long ms) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(new Date(ms));
    }

    private String buildStats(List<LocationEntry> entries) {
        if (entries.isEmpty()) return "Нет данных";
        long first = entries.get(0).getTimestamp();
        long last  = entries.get(entries.size() - 1).getTimestamp();
        return String.format(Locale.getDefault(),
                "Точек: %d  |  %s — %s",
                entries.size(),
                new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date(first)),
                new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date(last)));
    }
}