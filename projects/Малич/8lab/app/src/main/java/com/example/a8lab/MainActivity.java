package com.example.a8lab;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private MapView mapView;
    private ListView listView;
    private Button btnAddPlace;
    private Button btnSelectRoute;
    private Button btnShowList;
    private Button btnMarkCurrent;
    private Switch switchShowPlaces;
    private TextView tvMode;
    private TextView tvDistance;

    private List<LocationPoint> locationPoints;
    private List<Marker> placeMarkers;
    private Marker currentLocationMarker;
    private Polyline routePolyline;
    private GeoPoint currentGeoPoint;
    private GeoPoint selectedTargetGeoPoint;
    private Marker selectedTargetMarker;
    private boolean showPlaceMarkers = true;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        listView = findViewById(R.id.listView);
        btnAddPlace = findViewById(R.id.btnAddPlace);
        btnSelectRoute = findViewById(R.id.btnSelectRoute);
        btnShowList = findViewById(R.id.btnShowList);
        btnMarkCurrent = findViewById(R.id.btnMarkCurrent);
        switchShowPlaces = findViewById(R.id.switchShowPlaces);
        tvMode = findViewById(R.id.tvMode);
        tvDistance = findViewById(R.id.tvDistance);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMinZoomLevel(5.0);
        mapView.setMaxZoomLevel(19.0);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13.5);
        GeoPoint centerBrest = new GeoPoint(52.0976, 23.7050);
        mapController.setCenter(centerBrest);

        placeMarkers = new ArrayList<>();
        locationPoints = LocationDataManager.loadLocations(this);

        displayPlaceMarkers();
        setupMapClickToSelectPoint();
        setupListView();

        switchShowPlaces.setChecked(true);
        switchShowPlaces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showPlaceMarkers = isChecked;
                if (isChecked) {
                    displayPlaceMarkers();
                } else {
                    hidePlaceMarkers();
                }
            }
        });

        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentLocationAsPlace();
            }
        });

        btnSelectRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRouteSelectionDialog();
            }
        });

        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListView();
            }
        });

        btnMarkCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markCurrentLocationOnMap();
            }
        });

        requestLocationUpdates();
    }

    private void setupMapClickToSelectPoint() {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                selectedTargetGeoPoint = p;
                if (selectedTargetMarker != null) {
                    mapView.getOverlays().remove(selectedTargetMarker);
                }
                Drawable targetIcon = ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_menu_edit);
                selectedTargetMarker = new Marker(mapView);
                selectedTargetMarker.setPosition(p);
                selectedTargetMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                selectedTargetMarker.setTitle("Выбранная точка");
                selectedTargetMarker.setIcon(targetIcon);
                mapView.getOverlays().add(selectedTargetMarker);
                mapView.invalidate();

                if (currentGeoPoint != null) {
                    double distance = calculateDistance(currentGeoPoint, p);
                    tvDistance.setText("Расстояние до выбранной точки: " + String.format("%.2f", distance) + " км");
                    tvDistance.setVisibility(View.VISIBLE);
                } else {
                    tvDistance.setText("Текущее местоположение недоступно");
                    tvDistance.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(0, mapEventsOverlay);
    }

    private void displayPlaceMarkers() {
        Drawable parkIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass);
        Drawable shopIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass);

        for (Marker m : placeMarkers) {
            mapView.getOverlays().remove(m);
        }
        placeMarkers.clear();

        for (LocationPoint lp : locationPoints) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(lp.getLatitude(), lp.getLongitude()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(lp.getTitle());
            String snippet = lp.getType() + "\n" + sdf.format(new Date(lp.getTimestamp()));
            marker.setSnippet(snippet);

            if (lp.getType().equals("Парк")) {
                marker.setIcon(parkIcon);
            } else {
                marker.setIcon(shopIcon);
            }

            placeMarkers.add(marker);
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }

    private void hidePlaceMarkers() {
        for (Marker m : placeMarkers) {
            mapView.getOverlays().remove(m);
        }
        placeMarkers.clear();
        if (routePolyline != null) {
            mapView.getOverlays().remove(routePolyline);
        }
        mapView.invalidate();
    }

    private void setupListView() {
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationPoint lp = locationPoints.get(position);
                GeoPoint geoPoint = new GeoPoint(lp.getLatitude(), lp.getLongitude());
                mapView.getController().animateTo(geoPoint);
                mapView.getController().setZoom(17.0);
                listView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                tvMode.setText("Режим: Карта");
                if (showPlaceMarkers) {
                    displayPlaceMarkers();
                }
                Toast.makeText(MainActivity.this, lp.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showListView() {
        if (mapView.getVisibility() == View.VISIBLE) {
            mapView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            tvMode.setText("Режим: Список");
            updateListView();
        } else {
            mapView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            tvMode.setText("Режим: Карта");
            if (showPlaceMarkers) {
                displayPlaceMarkers();
            }
        }
    }

    private void updateListView() {
        List<String> items = new ArrayList<>();
        for (LocationPoint lp : locationPoints) {
            String dateStr = sdf.format(new Date(lp.getTimestamp()));
            items.add(lp.getTitle() + " (" + lp.getType() + ")\n" + dateStr);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }

    private void showRouteSelectionDialog() {
        if (selectedTargetGeoPoint == null) {
            Toast.makeText(this, "Сначала выберите точку на карте", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentGeoPoint == null) {
            Toast.makeText(this, "Текущее местоположение недоступно", Toast.LENGTH_SHORT).show();
            return;
        }
        buildRoute(currentGeoPoint, selectedTargetGeoPoint);
        double distance = calculateDistance(currentGeoPoint, selectedTargetGeoPoint);
        tvDistance.setText("Расстояние до выбранной точки: " + String.format("%.2f", distance) + " км");
        tvDistance.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "Маршрут построен", Toast.LENGTH_SHORT).show();
    }

    private void addCurrentLocationAsPlace() {
        if (currentGeoPoint == null) {
            Toast.makeText(this, "Текущее местоположение недоступно", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationPoint newPlace = new LocationPoint(
                currentGeoPoint.getLatitude(),
                currentGeoPoint.getLongitude(),
                "Отмеченное место " + (locationPoints.size() + 1),
                "Отмеченное",
                System.currentTimeMillis()
        );
        locationPoints.add(newPlace);
        LocationDataManager.saveLocations(this, locationPoints);
        if (showPlaceMarkers) {
            displayPlaceMarkers();
        }
        Toast.makeText(this, "Место отмечено", Toast.LENGTH_SHORT).show();
    }

    private void markCurrentLocationOnMap() {
        if (currentGeoPoint == null) {
            Toast.makeText(this, "Текущее местоположение недоступно", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationPoint newPlace = new LocationPoint(
                currentGeoPoint.getLatitude(),
                currentGeoPoint.getLongitude(),
                "Текущее место " + (locationPoints.size() + 1),
                "Отмеченное",
                System.currentTimeMillis()
        );
        locationPoints.add(newPlace);
        LocationDataManager.saveLocations(this, locationPoints);
        if (showPlaceMarkers) {
            displayPlaceMarkers();
        }
        mapView.getController().animateTo(currentGeoPoint);
        mapView.getController().setZoom(17.0);
        Toast.makeText(this, "Текущее местоположение отмечено", Toast.LENGTH_SHORT).show();
    }

    private void buildRoute(GeoPoint from, GeoPoint to) {
        if (routePolyline != null) {
            mapView.getOverlays().remove(routePolyline);
        }
        routePolyline = new Polyline();
        routePolyline.setColor(Color.RED);
        routePolyline.setWidth(8.0f);
        List<GeoPoint> points = new ArrayList<>();
        points.add(from);
        points.add(to);
        routePolyline.setPoints(points);
        mapView.getOverlays().add(routePolyline);
        mapView.invalidate();
    }

    private double calculateDistance(GeoPoint from, GeoPoint to) {
        float[] results = new float[1];
        Location.distanceBetween(
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude(),
                results
        );
        return results[0] / 1000.0;
    }

    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                updateCurrentLocationMarker();
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    private void updateCurrentLocationMarker() {
        if (currentLocationMarker != null) {
            mapView.getOverlays().remove(currentLocationMarker);
        }
        Drawable myLocIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mylocation);
        currentLocationMarker = new Marker(mapView);
        currentLocationMarker.setPosition(currentGeoPoint);
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        currentLocationMarker.setTitle("Мое местоположение");
        currentLocationMarker.setIcon(myLocIcon);
        mapView.getOverlays().add(currentLocationMarker);
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationDataManager.saveLocations(this, locationPoints);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}