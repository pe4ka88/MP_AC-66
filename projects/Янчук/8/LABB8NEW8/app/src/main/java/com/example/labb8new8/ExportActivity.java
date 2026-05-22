package com.example.labb8new8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Button btnExport = findViewById(R.id.btnExportGpx);

        btnExport.setOnClickListener(v -> exportGpx());
    }

    private void exportGpx() {
        GeoManager manager = new GeoManager(this);
        ArrayList<LocationPoint> points = manager.getAllPoints();

        if (points.isEmpty()) {
            Toast.makeText(this, "Нет данных для экспорта", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder gpx = new StringBuilder();
        gpx.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        gpx.append("<gpx version=\"1.1\" creator=\"GeoTracker\">\n");
        gpx.append("<trk><name>Маршрут</name><trkseg>\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

        for (LocationPoint p : points) {
            String time = sdf.format(new Date(p.time));

            gpx.append("<trkpt lat=\"")
                    .append(p.lat)
                    .append("\" lon=\"")
                    .append(p.lng)
                    .append("\">");

            gpx.append("<time>").append(time).append("</time>");
            gpx.append("</trkpt>\n");
        }

        gpx.append("</trkseg></trk></gpx>");

        try {
            File dir = new File(getExternalFilesDir(null), "exports");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "route.gpx");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(gpx.toString().getBytes());
            fos.close();

            Toast.makeText(this, "GPX сохранён: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка экспорта: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
