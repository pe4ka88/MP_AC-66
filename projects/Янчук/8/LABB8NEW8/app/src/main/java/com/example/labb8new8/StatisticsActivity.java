package com.example.labb8new8;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView txtDistance = findViewById(R.id.txtDistance);
        TextView txtSpeed = findViewById(R.id.txtSpeed);
        TextView txtPoints = findViewById(R.id.txtPoints);

        GeoManager manager = new GeoManager(this);
        ArrayList<LocationPoint> points = manager.getAllPoints();

        double distance = manager.calculateTotalDistance(points) / 1000.0; // км
        double speed = manager.calculateAverageSpeed(points);
        int count = points.size();

        txtDistance.setText("Расстояние: " + String.format("%.2f км", distance));
        txtSpeed.setText("Средняя скорость: " + String.format("%.2f км/ч", speed));
        txtPoints.setText("Количество точек: " + count);
    }
}
