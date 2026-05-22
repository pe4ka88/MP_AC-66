package com.example.taxi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.location.Purpose;
import com.yandex.mapkit.location.SubscriptionSettings;
import com.yandex.mapkit.location.UseInBackground;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.SuggestItem;
import com.yandex.mapkit.search.SuggestOptions;
import com.yandex.mapkit.search.SuggestResponse;
import com.yandex.mapkit.search.SuggestSession;
import com.yandex.mapkit.search.SuggestSession.SuggestListener;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.mapkit.search.SearchOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MAP_LIFECYCLE";
    private final BoundingBox BREST_BBOX = new BoundingBox(
            new Point(51.5, 23.0),
            new Point(52.8, 24.5)
    );

    private MapView mapView;
    private Map map;
    private MapObjectCollection mapObjects;
    private TextView tvPickup;
    private TextView tvDropoff;
    private PlacemarkMapObject pickupMarker;
    private PlacemarkMapObject dropoffMarker;
    private PolylineMapObject routeLine;

    private enum Mode { PICKUP, DROPOFF }
    private Mode mode = Mode.PICKUP;

    private Button btnPickup, btnDropoff;
    private AutoCompleteTextView etAddress;
    private SearchManager searchManager;
    private SuggestSession suggestSession;
    private Point pickupPoint;
    private static final int REQUEST_LOCATION = 1001;
    private Point dropoffPoint;
    private LocationManager locationManager;
    private enum SelectMode { PICKUP, DROPOFF }
    private SelectMode selectMode = SelectMode.PICKUP;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        btnPickup = findViewById(R.id.btnPickup);
        btnDropoff = findViewById(R.id.btnDropoff);
        etAddress = findViewById(R.id.etAddress);
        Button btnDone = findViewById(R.id.btnDone);

        map = mapView.getMap();
        mapObjects = map.getMapObjects();

        map.move(new CameraPosition(new Point(52.0976, 23.7341), 12f,0f,0f));

        // Создаём сессию подсказок
        searchManager = SearchFactory.getInstance()
                .createSearchManager(SearchManagerType.ONLINE);
        suggestSession = searchManager.createSuggestSession();
        tvPickup = findViewById(R.id.tvPickup);
        tvDropoff = findViewById(R.id.tvDropoff);
        locationManager = MapKitFactory.getInstance().createLocationManager();

        // Обработка ввода в текстовое поле
        etAddress.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 2){
                    requestSuggest(s.toString());
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        btnDone.setOnClickListener(v -> {

            if (pickupPoint == null || dropoffPoint == null) {
                Toast.makeText(this,
                        "Выберите точки ОТКУДА и КУДА",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent data = new Intent();
            data.putExtra("pickup_lat", pickupPoint.getLatitude());
            data.putExtra("pickup_lon", pickupPoint.getLongitude());
            data.putExtra("dropoff_lat", dropoffPoint.getLatitude());
            data.putExtra("dropoff_lon", dropoffPoint.getLongitude());

            setResult(RESULT_OK, data);
            finish();
        });

        btnPickup.setOnClickListener(v -> {
            mode = Mode.PICKUP;
            Toast.makeText(this,"Выбор: ОТКУДА",Toast.LENGTH_SHORT).show();
        });

        btnDropoff.setOnClickListener(v -> {
            mode = Mode.DROPOFF;
            Toast.makeText(this,"Выбор: КУДА",Toast.LENGTH_SHORT).show();
        });

        ImageButton btnGps = findViewById(R.id.btnGps);

        btnGps.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                requestMyLocation();
                Toast.makeText(this, "Определение местоположения...", Toast.LENGTH_SHORT).show();

            } else {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION
                );

            }
        });

        map.addInputListener(inputListener);
    }
    private String shortAddress(String address) {
        int max = 35; // максимальная длина

        if (address == null) return "";

        if (address.length() <= max)
            return address;

        return address.substring(0, max) + "...";
    }
    // ===================== InputListener =====================
    private final InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(Map map, Point point) {
            Log.d(TAG,"tap " + point.getLatitude());

            if (mode == Mode.PICKUP) placePickup(point);
            else placeDropoff(point);

            if (pickupPoint != null && dropoffPoint != null)
                drawRealRoute(pickupPoint, dropoffPoint);
        }

        @Override
        public void onMapLongTap(Map map, Point point) {}
    };

    private void placePickup(Point p) {
        pickupPoint = p;
        ImageProvider icon = createPinIcon("A", 0xFF00AA00);
        if (pickupMarker == null)
            pickupMarker = mapObjects.addPlacemark(p, icon);
        else
            pickupMarker.setGeometry(p);

        reverseGeocode(p, true); // вызываем геокодирование
    }

    private void placeDropoff(Point p) {
        dropoffPoint = p;
        ImageProvider icon = createPinIcon("B", 0xFFCC0000);
        if (dropoffMarker == null)
            dropoffMarker = mapObjects.addPlacemark(p, icon);
        else
            dropoffMarker.setGeometry(p);

        reverseGeocode(p, false); // вызываем геокодирование
    }

    private ImageProvider createPinIcon(String letter, int color) {
        int width = 80;
        int height = 180; // высота для хвостика
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        float cx = width / 2f;
        float headRadius = width / 2f - 4;

        // Хвост (кончик на нижней точке bitmap)
        float tailHeight = height - headRadius * 2;
        float tailTopY = headRadius; // верхняя граница хвоста
        float tailBottomY = tailTopY + tailHeight;

        android.graphics.Path tailPath = new android.graphics.Path();
        tailPath.moveTo(cx - headRadius / 2f, tailTopY);
        tailPath.lineTo(cx + headRadius / 2f, tailTopY);
        tailPath.lineTo(cx, tailBottomY);
        tailPath.close();

        Paint tailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tailPaint.setStyle(Paint.Style.FILL);
        tailPaint.setColor(color);
        canvas.drawPath(tailPath, tailPaint);

        Paint tailStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        tailStroke.setStyle(Paint.Style.STROKE);
        tailStroke.setStrokeWidth(6);
        tailStroke.setColor(0xFFFFFFFF);
        canvas.drawPath(tailPath, tailStroke);

        // Круглая голова над хвостиком
        float headCenterY = headRadius; // верхняя часть круга
        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(color);
        canvas.drawCircle(cx, headCenterY, headRadius, fill);

        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(6);
        stroke.setColor(0xFFFFFFFF);
        canvas.drawCircle(cx, headCenterY, headRadius, stroke);

        // Буква
        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setColor(0xFFFFFFFF);
        text.setTextSize(36f);
        text.setFakeBoldText(true);
        text.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fm = text.getFontMetrics();
        float y = headCenterY - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(letter, cx, y, text);

        return ImageProvider.fromBitmap(bmp);
    }

    // Вспомогательный метод для треугольника
    private android.graphics.Path createTrianglePath(float[] pts) {
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(pts[0], pts[1]);
        path.lineTo(pts[2], pts[3]);
        path.lineTo(pts[4], pts[5]);
        path.close();
        return path;
    }


    // ===================== Иконка маркера =====================
    private ImageProvider createIcon(int color) {
        Bitmap bmp = Bitmap.createBitmap(80,80,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(8);
        stroke.setColor(color);

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(0xFFFFFFFF);

        c.drawCircle(40,40,30,stroke);
        c.drawCircle(40,40,22,fill);

        return ImageProvider.fromBitmap(bmp);
    }

    // ===================== Имитация маршрута =====================
    private void drawRealRoute(Point from, Point to) {
        new Thread(() -> {
            try {
                String urlStr = String.format(
                        "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                        from.getLongitude(), from.getLatitude(),
                        to.getLongitude(), to.getLatitude()
                );

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream is = conn.getInputStream();  // <--- правильно
                Scanner sc = new Scanner(is);
                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) sb.append(sc.nextLine());
                sc.close();
                is.close();

                JSONObject json = new JSONObject(sb.toString());
                JSONArray coordinates = json
                        .getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                List<Point> routePoints = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = coordinates.getJSONArray(i);
                    double lon = point.getDouble(0);
                    double lat = point.getDouble(1);
                    routePoints.add(new Point(lat, lon));
                }

                runOnUiThread(() -> {
                    if (routeLine != null) mapObjects.remove(routeLine);

                    Polyline polyline = new Polyline(routePoints);
                    routeLine = mapObjects.addPolyline(polyline);
                    routeLine.setStrokeColor(0xFF0000FF);
                    routeLine.setStrokeWidth(6);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error routing", e);
                runOnUiThread(() ->
                        Toast.makeText(MapActivity.this, "Не удалось построить маршрут", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    private void reverseGeocode(Point point, boolean isPickup) {
        new Thread(() -> {
            try {
                String urlStr = String.format(
                        "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.6f&lon=%.6f&addressdetails=1",
                        point.getLatitude(), point.getLongitude()
                );

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Taxi/1.0 (2peacewxrld@gmail.com)");
                conn.connect();

                InputStream is = conn.getInputStream();
                Scanner sc = new Scanner(is);
                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) sb.append(sc.nextLine());
                sc.close();
                is.close();

                JSONObject json = new JSONObject(sb.toString());
                String displayName = json.optString("display_name", "Неизвестно");

                runOnUiThread(() -> {

                    String shortAddr = shortAddress(displayName);

                    if (isPickup) {
                        tvPickup.setText("Откуда: " + shortAddr);
                    } else {
                        tvDropoff.setText("Куда: " + shortAddr);
                    }

                });

            } catch (Exception e) {
                Log.e(TAG, "Reverse geocode error", e);
                runOnUiThread(() ->
                        Toast.makeText(MapActivity.this, "Не удалось определить адрес", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    private LocationListener locationListener;

    private void requestMyLocation() {
        SubscriptionSettings settings = new SubscriptionSettings(
                UseInBackground.ALLOW,
                Purpose.GENERAL
        );

        // Создаем слушатель один раз
        locationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                Point p = location.getPosition();

                // Ставим точку в зависимости от режима
                if (mode == Mode.PICKUP) {
                    placePickup(p);
                } else {
                    placeDropoff(p);
                }

                // Двигаем камеру
                map.move(new CameraPosition(p, 14f, 0f, 0f));

                if (pickupPoint != null && dropoffPoint != null) {
                    drawRealRoute(pickupPoint, dropoffPoint);
                }

                // Отписываемся после первого обновления
                locationManager.unsubscribe(locationListener);
            }


            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus status) {
                Log.d(TAG, "Location status: " + status);
            }
        };

        // Подписка на обновления
        locationManager.subscribeForLocationUpdates(settings, locationListener);
    }


    // ===================== Suggest =====================
    private void requestSuggest(String q) {
        suggestSession.suggest(q, BREST_BBOX, new SuggestOptions(), new SuggestListener() {
            @Override
            public void onResponse(SuggestResponse r) {
                List<String> list = new ArrayList<>();
                for (SuggestItem i : r.getItems()) {
                    list.add(i.getDisplayText());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        MapActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        list
                );
                etAddress.setAdapter(adapter);
                etAddress.showDropDown();

                // Обработка выбора подсказки
                etAddress.setOnItemClickListener((parent, view, position, id) -> {
                    SuggestItem item = r.getItems().get(position);

                    if (item.getCenter() != null) { // берем координату центра подсказки
                        Point point = item.getCenter();

                        if (mode == Mode.PICKUP) placePickup(point);
                        else placeDropoff(point);

                        map.move(new CameraPosition(point, 14f, 0f, 0f));

                        if (pickupPoint != null && dropoffPoint != null)
                            drawRealRoute(pickupPoint, dropoffPoint);
                    }
                });
            }

            @Override
            public void onError(com.yandex.runtime.Error e) {
                Log.e(TAG, "Suggest error: " + e.toString());
            }
        });
    }




    // ===================== Жизненный цикл =====================
    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}
