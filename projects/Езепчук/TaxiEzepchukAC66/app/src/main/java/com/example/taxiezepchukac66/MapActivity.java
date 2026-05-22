package com.example.taxiezepchukac66;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MAP_LIFECYCLE";
    private final BoundingBox BREST_BBOX = new BoundingBox(
            new Point(51.5, 23.0),
            new Point(52.8, 24.5)
    );

    private MapView mapView;
    private Map map;
    private MapObjectCollection mapObjects;

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

        Button btnGps = findViewById(R.id.btnGps);
        btnGps.setOnClickListener(v -> {
            requestMyLocation();
            Toast.makeText(this, "Определение местоположения...", Toast.LENGTH_SHORT).show();
        });

        map.addInputListener(inputListener);
    }

    // ===================== InputListener =====================
    private final InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(Map map, Point point) {
            Log.d(TAG,"tap " + point.getLatitude());

            if (mode == Mode.PICKUP) placePickup(point);
            else placeDropoff(point);

            if (pickupPoint != null && dropoffPoint != null)
                drawFakeRoute(pickupPoint, dropoffPoint);
        }

        @Override
        public void onMapLongTap(Map map, Point point) {}
    };

    // ===================== Маркеры =====================
    private void placePickup(Point p) {
        pickupPoint = p;
        ImageProvider icon = createIcon(0xFF00AA00);
        if (pickupMarker == null)
            pickupMarker = mapObjects.addPlacemark(p, icon);
        else
            pickupMarker.setGeometry(p);
    }

    private void placeDropoff(Point p) {
        dropoffPoint = p;
        ImageProvider icon = createIcon(0xFFCC0000);
        if (dropoffMarker == null)
            dropoffMarker = mapObjects.addPlacemark(p, icon);
        else
            dropoffMarker.setGeometry(p);
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
    private void drawFakeRoute(Point from, Point to) {
        if (routeLine != null) mapObjects.remove(routeLine);

        List<Point> routePoints = new ArrayList<>();
        routePoints.add(from);

        double latStep = (to.getLatitude() - from.getLatitude()) / 3;
        double lonStep = (to.getLongitude() - from.getLongitude()) / 3;

        routePoints.add(new Point(from.getLatitude() + latStep, from.getLongitude()));
        routePoints.add(new Point(from.getLatitude() + 2 * latStep, from.getLongitude() + lonStep));
        routePoints.add(to);

        Polyline polyline = new Polyline(routePoints);
        routeLine = mapObjects.addPolyline(polyline);
        routeLine.setStrokeColor(0xFF0000FF);
        routeLine.setStrokeWidth(6);
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
                    drawFakeRoute(pickupPoint, dropoffPoint);
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
                            drawFakeRoute(pickupPoint, dropoffPoint);
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
