package com.example.myapplication8; // Проверь, чтобы совпадало с названием твоей папки!

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private DBHelper dbHelper;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        Button btnGenerate = findViewById(R.id.btnGenerateData);
        btnGenerate.setOnClickListener(v -> {
            generateMockData();
            loadRouteAndMarkers();
        });

        checkPermissions();
        loadRouteAndMarkers();
    }

    private void loadRouteAndMarkers() {
        List<GeoPoint> points = dbHelper.getAllCoordinates();

        map.getOverlays().clear();

        if (points.isEmpty()) {
            Toast.makeText(this, "База пуста. Нажмите кнопку!", Toast.LENGTH_SHORT).show();
            map.getController().setZoom(13.0);

            map.getController().setCenter(new GeoPoint(52.0976, 23.6877));
            return;
        }

        for (int i = 0; i < points.size(); i++) {
            Marker marker = new Marker(map);
            marker.setPosition(points.get(i));
            marker.setTitle("Точка #" + (i + 1));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(marker);
        }

        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(Color.BLUE);
        line.setWidth(6.0f);
        map.getOverlays().add(line);

        map.getController().setZoom(14.0);
        map.getController().setCenter(points.get(points.size() - 1));

        map.invalidate();
    }

    private void generateMockData() {
        dbHelper.clearDatabase();

        double startLat = 52.0976;
        double startLng = 23.6877;
        Random random = new Random();

        for (int i = 0; i < 35; i++) {

            startLat += (random.nextDouble() - 0.5) * 0.005;
            startLng += (random.nextDouble() - 0.5) * 0.005;
            dbHelper.insertCoordinate(startLat, startLng);
        }
        Toast.makeText(this, "35 точек в Бресте успешно созданы!", Toast.LENGTH_SHORT).show();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }
    }


    static class DBHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "osm_locations.db";
        private static final String TABLE = "locations";

        public DBHelper(Context context) { super(context, DB_NAME, null, 1); }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (id INTEGER PRIMARY KEY, lat REAL, lng REAL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }

        public void insertCoordinate(double lat, double lng) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put("lat", lat); v.put("lng", lng);
            db.insert(TABLE, null, v);
            db.close();
        }

        public List<GeoPoint> getAllCoordinates() {
            List<GeoPoint> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE, null);
            if (c.moveToFirst()) {
                do {
                    list.add(new GeoPoint(c.getDouble(1), c.getDouble(2)));
                } while (c.moveToNext());
            }
            c.close(); db.close();
            return list;
        }

        public void clearDatabase() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE);
            db.close();
        }
    }
}