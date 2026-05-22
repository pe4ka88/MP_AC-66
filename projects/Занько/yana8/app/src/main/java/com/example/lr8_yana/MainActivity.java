package com.example.lr8_yana;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Context ctx = getApplicationContext();
        IConfigurationProvider provider = Configuration.getInstance();
        provider.load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        provider.setUserAgentValue(getPackageName());

        File basePath = new File(getCacheDir().getAbsolutePath(), "osmdroid");
        basePath.mkdirs();
        provider.setOsmdroidBasePath(basePath);
        provider.setOsmdroidTileCache(basePath);

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setUseDataConnection(true); 
        map.setMultiTouchControls(true);
        map.getController().setZoom(17.5); 

        // --- НОВЫЙ РЕАЛИСТИЧНЫЙ МАРШРУТ (ЗИГЗАГИ ПО БрГТУ) ---
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(52.09388, 23.75775)); // 1. Старт: Главный вход
        points.add(new GeoPoint(52.09400, 23.75790)); // 2. Идем внутрь
        points.add(new GeoPoint(52.09420, 23.75780)); // 3. Зигзаг к корпусу 3
        points.add(new GeoPoint(52.09440, 23.75760)); // 4.
        points.add(new GeoPoint(52.09450, 23.75800)); // 5. Резко к Галерее
        points.add(new GeoPoint(52.09440, 23.75850)); // 6. Возле Галереи
        points.add(new GeoPoint(52.09460, 23.75870)); // 7. В сторону библиотеки
        points.add(new GeoPoint(52.09480, 23.75850)); // 8. Свернули назад (петля)
        points.add(new GeoPoint(52.09500, 23.75880)); // 9. Зашли в сквер
        points.add(new GeoPoint(52.09520, 23.75860)); // 10. Прогулка по скверу
        points.add(new GeoPoint(52.09540, 23.75890)); // 11.
        points.add(new GeoPoint(52.09530, 23.75940)); // 12. Поворот к стадиону
        points.add(new GeoPoint(52.09510, 23.75980)); // 13.
        points.add(new GeoPoint(52.09490, 23.76000)); // 14. Возле стадиона
        points.add(new GeoPoint(52.09470, 23.76020)); // 15.
        points.add(new GeoPoint(52.09450, 23.75990)); // 16. Возврат ближе к центру
        points.add(new GeoPoint(52.09430, 23.76010)); // 17. Возле Здравпункта
        points.add(new GeoPoint(52.09410, 23.76040)); // 18.
        points.add(new GeoPoint(52.09390, 23.76030)); // 19. Идем южнее
        points.add(new GeoPoint(52.09380, 23.75990)); // 20. Срез через парковку
        points.add(new GeoPoint(52.09360, 23.75970)); // 21.
        points.add(new GeoPoint(52.09340, 23.75950)); // 22. К южной дороге
        points.add(new GeoPoint(52.09330, 23.75900)); // 23.
        points.add(new GeoPoint(52.09340, 23.75850)); // 24. Зигзаг во дворы
        points.add(new GeoPoint(52.09320, 23.75820)); // 25. Ближе к Васнецова
        points.add(new GeoPoint(52.09330, 23.75780)); // 26.
        points.add(new GeoPoint(52.09310, 23.75750)); // 27.
        points.add(new GeoPoint(52.09340, 23.75720)); // 28. Резкий поворот
        points.add(new GeoPoint(52.09360, 23.75740)); // 29.
        points.add(new GeoPoint(52.09380, 23.75760)); // 30. Финиш: неподалеку от старта

        map.getController().setCenter(points.get(8)); // Центрируем на сквере для лучшего вида

        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(0xFF000000); // Оставляем строгий черно-белый стиль
        line.setWidth(7.0f);
        map.getOverlays().add(line);

        for (int i = 0; i < points.size(); i++) {
            Marker marker = new Marker(map);
            marker.setPosition(points.get(i));
            marker.setTitle("Точка №" + (i + 1));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(marker);
        }

        Button btnTask = findViewById(R.id.btnTask);
        btnTask.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Задание ЛР №20")
                    .setMessage("Тема 6. ГЕОЛОКАЦИЯ\n" +
                            "Отображено:\n" +
                            "1. Карта с локациями.\n" +
                            "2. Маршрут движения.\n" +
                            "3. Посещенные места (30 фиксаций).\n\n" +
                            "Выполнила: Занько Яна Сергеевна\n" +
                            "Группа: АС-66")
                    .setPositiveButton("ОК", null)
                    .show();
        });

        Button btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Возврат в центр", Toast.LENGTH_SHORT).show();
            map.getController().animateTo(points.get(8));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
