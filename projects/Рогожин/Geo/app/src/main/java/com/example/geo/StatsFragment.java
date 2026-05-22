package com.example.geo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.geo.analytics.MovementSegment;
import com.example.geo.analytics.RouteAnalyzer;
import com.example.geo.data.LocationDatabaseHelper;
import com.example.geo.views.SimpleColumnChartView;
import com.example.geo.views.SimplePieChartView;

import java.util.Calendar;
import java.util.List;

public class StatsFragment extends Fragment {

    private TextView tvMonth, tvStats, tvPopularRoutes;
    private SimplePieChartView pieChart;
    private SimpleColumnChartView columnChart;

    public StatsFragment() {
        super(R.layout.fragment_stats);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        tvMonth = view.findViewById(R.id.tvMonth);
        tvStats = view.findViewById(R.id.tvStats);
        tvPopularRoutes = view.findViewById(R.id.tvPopularRoutes);

        pieChart = view.findViewById(R.id.pieChart);
        columnChart = view.findViewById(R.id.columnChart);

        loadStats();
    }

    private void loadStats() {

        LocationDatabaseHelper db = new LocationDatabaseHelper(requireContext());

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        tvMonth.setText("Месяц: " + (month + 1) + " / " + year);

        // Границы месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long start = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH,1);
        long end = calendar.getTimeInMillis();

        List<com.example.geo.data.LocationPoint> points = db.getByPeriod(start,end);

        if(points.size() < 2){
            tvStats.setText("Нет данных за этот месяц");
            return;
        }

        // Используем новую версию RouteAnalyzer с контекстом
        List<MovementSegment> segments = RouteAnalyzer.analyze(requireContext(), points);

        float walkDistance = 0;
        float carDistance = 0;
        long walkTime = 0;
        long carTime = 0;

        for(MovementSegment s : segments){
            if(s.type == MovementSegment.Type.WALK){
                walkDistance += s.distance;
                walkTime += (s.endTime - s.startTime);
            } else if(s.type == MovementSegment.Type.CAR){
                carDistance += s.distance;
                carTime += (s.endTime - s.startTime);
            }
        }

        tvStats.setText(
                "Пешком: " + String.format("%.2f км, %d мин", walkDistance/1000f, walkTime/60000) +
                        "\nНа транспорте: " + String.format("%.2f км, %d мин", carDistance/1000f, carTime/60000)
        );

        // Установка кастомных графиков
        pieChart.setValues(new float[]{walkDistance, carDistance});
        columnChart.setValues(new float[]{walkDistance, walkDistance*0.8f, walkDistance*1.1f,
                carDistance, carDistance*0.9f, carDistance*1.2f});

        // Популярные маршруты
        tvPopularRoutes.setText("Популярные маршруты: \n" + getPopularRoutes(segments));
    }

    private String getPopularRoutes(List<MovementSegment> segments){
        // Выводим начальные и конечные координаты сегментов STOP + название места
        StringBuilder sb = new StringBuilder();
        for(MovementSegment s:segments){
            if(s.type == MovementSegment.Type.STOP){
                sb.append(String.format("От %.5f,%.5f до %.5f,%.5f • %s\n",
                        s.latitude, s.longitude,
                        s.endLatitude != 0 ? s.endLatitude : s.latitude,
                        s.endLongitude != 0 ? s.endLongitude : s.longitude,
                        s.getPlace()));
            }
        }
        return sb.toString();
    }
}