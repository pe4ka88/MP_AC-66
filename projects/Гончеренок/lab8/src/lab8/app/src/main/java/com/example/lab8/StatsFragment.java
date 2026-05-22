package com.example.lab8;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsFragment extends Fragment {

    private TextView tvTotalPoints, tvWeekPoints, tvMonthPoints, tvFirstPoint;
    private BarChart chart;
    private ListView listView;

    private AppDatabase database;
    private LocationAdapter adapter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<LocationPoint> cachedPoints = new ArrayList<>();
    private boolean isDataLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        initViews(view);
        database = AppDatabase.getInstance(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загружаем данные только если их еще нет
        if (!isDataLoaded) {
            loadStatsAsync();
        } else {
            // Используем кэшированные данные
            updateUIWithCachedData();
        }
    }

    private void initViews(View view) {
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvWeekPoints = view.findViewById(R.id.tvWeekPoints);
        tvMonthPoints = view.findViewById(R.id.tvMonthPoints);
        tvFirstPoint = view.findViewById(R.id.tvFirstPoint);
        chart = view.findViewById(R.id.chart);
        listView = view.findViewById(R.id.listViewLocations);
    }

    private void loadStatsAsync() {
        executorService.execute(() -> {
            try {
                List<LocationPoint> allPoints = database.locationDao().getAllLocations();
                cachedPoints = allPoints;

                processAndUpdateUI(allPoints);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processAndUpdateUI(List<LocationPoint> allPoints) {
        // Подсчет статистики
        int totalCount = allPoints.size();

        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DAY_OF_YEAR, -7);
        int weekCount = 0;
        for (LocationPoint p : allPoints) {
            if (p.getTimestamp() >= weekAgo.getTimeInMillis()) {
                weekCount++;
            }
        }

        Calendar monthAgo = Calendar.getInstance();
        monthAgo.add(Calendar.DAY_OF_YEAR, -30);
        int monthCount = 0;
        for (LocationPoint p : allPoints) {
            if (p.getTimestamp() >= monthAgo.getTimeInMillis()) {
                monthCount++;
            }
        }

        LocationPoint firstPoint = allPoints.isEmpty() ? null : allPoints.get(allPoints.size() - 1);

        List<LocationPoint> recent = allPoints.size() > 10 ? allPoints.subList(0, 10) : allPoints;

        // Данные для графика
        Map<String, Integer> dailyCounts = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());

        for (LocationPoint point : allPoints) {
            String day = sdf.format(new Date(point.getTimestamp()));
            dailyCounts.put(day, dailyCounts.getOrDefault(day, 0) + 1);
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String day = sdf.format(calendar.getTime());
            int count = dailyCounts.getOrDefault(day, 0);

            entries.add(new BarEntry(6 - i, count));
            labels.add(day);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Количество точек");
        dataSet.setColor(Color.parseColor("#3F51B5"));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        // Сохраняем финальные переменные для использования в UI потоке
        final int finalTotalCount = totalCount;
        final int finalWeekCount = weekCount;
        final int finalMonthCount = monthCount;
        final LocationPoint finalFirstPoint = firstPoint;
        final List<LocationPoint> finalRecent = recent;
        final BarData finalBarData = barData;
        final List<String> finalLabels = labels;

        // Обновляем UI в главном потоке
        mainHandler.post(() -> {
            updateUI(finalTotalCount, finalWeekCount, finalMonthCount,
                    finalFirstPoint, finalRecent, finalBarData, finalLabels);
            isDataLoaded = true;
        });
    }

    private void updateUIWithCachedData() {
        processAndUpdateUI(cachedPoints);
    }

    private void updateUI(int totalCount, int weekCount, int monthCount,
                          LocationPoint firstPoint, List<LocationPoint> recent,
                          BarData barData, List<String> labels) {

        tvTotalPoints.setText("Всего точек: " + totalCount);
        tvWeekPoints.setText("За неделю: " + weekCount);
        tvMonthPoints.setText("За месяц: " + monthCount);

        if (firstPoint != null) {
            tvFirstPoint.setText("Первая точка: " + firstPoint.getFormattedDate());
        } else {
            tvFirstPoint.setText("Первая точка: --");
        }

        if (adapter == null) {
            adapter = new LocationAdapter(recent, LayoutInflater.from(getContext()));
            listView.setAdapter(adapter);
        } else {
            adapter.updateData(recent);
        }

        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Не закрываем executorService здесь, чтобы сохранить кэш
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}