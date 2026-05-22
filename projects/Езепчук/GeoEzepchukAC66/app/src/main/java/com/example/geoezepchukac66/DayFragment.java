package com.example.geoezepchukac66;

import android.app.DatePickerDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoezepchukac66.data.LocationDatabaseHelper;
import com.example.geoezepchukac66.data.LocationPoint;
import com.example.geoezepchukac66.model.RouteItem;
import com.example.geoezepchukac66.utils.GeocoderHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayFragment extends Fragment {

    private static final String TAG = "DayFragment";

    // UI компоненты
    private Button btnSelectDate;
    private TextView distanceText;
    private TextView timeText;
    private TextView pointsText;
    private RecyclerView recycler;

    // Работа с датой
    private Calendar selectedDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private final SimpleDateFormat logTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    // Константы для алгоритма сегментации
    private static final float MOVE_SPEED_THRESHOLD = 0.7f; // м/с (около 2.5 км/ч)
    private static final long TIME_GAP_THRESHOLD = 5 * 60 * 1000; // 5 минут разрыва

    public DayFragment() {
        super(R.layout.fragment_day);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация View
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        distanceText = view.findViewById(R.id.dayDistance);
        timeText = view.findViewById(R.id.dayTime);
        pointsText = view.findViewById(R.id.dayPoints);
        recycler = view.findViewById(R.id.dayRecycler);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedDate = Calendar.getInstance();
        updateDateButtonText();

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Первичная загрузка за сегодня
        loadDayStats();
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateButtonText();
            loadDayStats();
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButtonText() {
        btnSelectDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void loadDayStats() {
        LocationDatabaseHelper db = new LocationDatabaseHelper(requireContext());

        // Вычисляем границы дня
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = calendar.getTimeInMillis();

        List<LocationPoint> points = db.getByPeriod(startOfDay, endOfDay);

        Log.d(TAG, "=== Processing Day: " + dateFormat.format(new Date(startOfDay)) + " ===");
        Log.d(TAG, "Raw points loaded from DB: " + points.size());

        pointsText.setText("Points: " + points.size());

        if (points.size() < 2) {
            Log.w(TAG, "Not enough data points to build timeline");
            clearUI();
            return;
        }

        // 1. Сортировка по времени
        points.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));

        // 2. Логирование каждой точки (Verbose)
        for (int i = 0; i < points.size(); i++) {
            LocationPoint p = points.get(i);
            Log.v(TAG, String.format("GPS Point [%s] Lat: %.6f, Lon: %.6f",
                    logTimeFormat.format(new Date(p.timestamp)), p.latitude, p.longitude));
        }

        // 3. Общая статистика (дистанция и время)
        calculateGlobalStats(points);

        // 4. Продвинутая сегментация (Алгоритм фильтрации Move/Stay)
        List<RouteItem> segments = segmentPoints(points);

        // 5. Геокодинг имен мест в фоне
        startGeocoding(segments);
    }

    private void calculateGlobalStats(List<LocationPoint> points) {
        float totalDist = 0;
        for (int i = 1; i < points.size(); i++) {
            float[] results = new float[1];
            Location.distanceBetween(
                    points.get(i-1).latitude, points.get(i-1).longitude,
                    points.get(i).latitude, points.get(i).longitude, results);
            totalDist += results[0];
        }
        long duration = points.get(points.size() - 1).timestamp - points.get(0).timestamp;

        distanceText.setText(String.format(Locale.getDefault(), "Distance: %.2f km", totalDist / 1000f));
        timeText.setText("Active duration: " + (duration / 60000) + " min");
    }

    private List<RouteItem> segmentPoints(List<LocationPoint> points) {
        List<RouteItem> segments = new ArrayList<>();
        LocationPoint segmentStartPoint = points.get(0);

        // Определяем начальное состояние по первой паре точек
        boolean isCurrentlyMoving = calculateSpeed(points.get(0), points.get(1)) > MOVE_SPEED_THRESHOLD;

        Log.d(TAG, "--- Segmentation Started ---");
        Log.d(TAG, "Initial State: " + (isCurrentlyMoving ? "MOVING" : "STAYING"));

        for (int i = 1; i < points.size(); i++) {
            LocationPoint prev = points.get(i - 1);
            LocationPoint curr = points.get(i);

            long timeGap = curr.timestamp - prev.timestamp;
            double speed = calculateSpeed(prev, curr);
            boolean pointIsMoving = speed > MOVE_SPEED_THRESHOLD;

            // Разрыв сегмента, если:
            // 1. Изменился тип движения (стоял -> пошел или наоборот)
            // 2. Слишком большая пауза в данных (например, 10 минут не было GPS)
            if (pointIsMoving != isCurrentlyMoving || timeGap > TIME_GAP_THRESHOLD) {

                Log.i(TAG, String.format("Segment ended at %s. New state: %s. Reason: %s",
                        logTimeFormat.format(new Date(prev.timestamp)),
                        (pointIsMoving ? "MOVING" : "STAYING"),
                        (timeGap > TIME_GAP_THRESHOLD ? "GAP" : "STATE_CHANGE")));

                segments.add(new RouteItem(segmentStartPoint, prev, isCurrentlyMoving));
                segmentStartPoint = curr;
                isCurrentlyMoving = pointIsMoving;
            }
        }

        // Добавляем финальный сегмент
        segments.add(new RouteItem(segmentStartPoint, points.get(points.size() - 1), isCurrentlyMoving));
        Log.d(TAG, "Total segments created: " + segments.size());

        return segments;
    }

    private void startGeocoding(List<RouteItem> segments) {
        new Thread(() -> {
            Log.d(TAG, "Background geocoding task started...");
            for (RouteItem r : segments) {
                String addrStart = GeocoderHelper.getPlace(requireContext(), r.start.latitude, r.start.longitude);

                if (!r.isMoving) {
                    r.placeName = " " + addrStart;
                } else {
                    String addrEnd = GeocoderHelper.getPlace(requireContext(), r.end.latitude, r.end.longitude);
                    if (addrStart.equals(addrEnd)) {
                        r.placeName = " " + addrStart;
                    } else {
                        r.placeName = addrStart + " → " + addrEnd;
                    }
                }
                Log.v(TAG, "Resolved segment: " + r.placeName);
            }

            // Возврат в UI поток
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "UI Updated with " + segments.size() + " segments.");
                    recycler.setAdapter(new RoutesAdapter(segments));
                });
            }
        }).start();
    }

    private double calculateSpeed(LocationPoint p1, LocationPoint p2) {
        float[] results = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results);
        long seconds = (p2.timestamp - p1.timestamp) / 1000;
        return seconds > 0 ? (results[0] / (double) seconds) : 0;
    }

    private void clearUI() {
        distanceText.setText("Distance: 0 km");
        timeText.setText("Time: 0 min");
        recycler.setAdapter(new RoutesAdapter(new ArrayList<>()));
    }
}