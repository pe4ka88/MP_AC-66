package com.example.geotracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    private Button btnStart, btnStop;
    private boolean isTracking = false;

    private CardView weatherCard;
    private TextView tvWeatherIcon, tvWeatherTemp, tvWeatherDesc;
    private OkHttpClient client;
    private Handler mainHandler;
    private static final String TAG = "MainActivity";

    // Генератор тестовых данных
    private TestDataGenerator testDataGenerator;

    private static final double BREST_LAT = 52.0976;
    private static final double BREST_LON = 23.7341;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final String[] REQUIRED_PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            REQUIRED_PERMISSIONS = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }
    }

    private final String[] tabTitles = {"🗺️ Карта", "📊 Статистика"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewPager();
        setupWeather();
        setupClickListeners();
        checkPermissionsAndInitialize();

        // Инициализация генератора тестовых данных
        testDataGenerator = new TestDataGenerator(this);

        loadWeatherForBrest();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnStart = findViewById(R.id.btnStartTracking);
        btnStop = findViewById(R.id.btnStopTracking);

        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Сохраняем оба фрагмента в памяти
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }

    private void setupWeather() {
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> startTracking());
        btnStop.setOnClickListener(v -> stopTracking());

        // Кнопка генерации тестовых данных
        Button btnGenerateTest = findViewById(R.id.btnGenerateTest);
        btnGenerateTest.setOnClickListener(v -> generateTestData());
    }

    private void generateTestData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Генерация тестовых данных");
        builder.setMessage("Выберите вариант генерации точек по Бресту:");

        String[] options = {
                "📊 30+ точек за 30 дней",
                "🗺️ Маршрут на сегодня",
                "🔄 Очистить все данные",
                "⚡ Сгенерировать 50 точек"
        };

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    Toast.makeText(this, "Генерация 30+ точек...", Toast.LENGTH_SHORT).show();
                    testDataGenerator.generateTestData(2); // 2 точки в день = 60 точек
                    break;
                case 1:
                    Toast.makeText(this, "Генерация маршрута на сегодня...", Toast.LENGTH_SHORT).show();
                    testDataGenerator.generateRealisticRoute();
                    break;
                case 2:
                    new AlertDialog.Builder(this)
                            .setTitle("Подтверждение")
                            .setMessage("Вы уверены, что хотите удалить ВСЕ данные?")
                            .setPositiveButton("Да", (d, w) -> {
                                testDataGenerator.clearOldData();
                                Toast.makeText(this, "Данные очищены", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Нет", null)
                            .show();
                    break;
                case 3:
                    Toast.makeText(this, "Генерация 50 точек...", Toast.LENGTH_SHORT).show();
                    testDataGenerator.generateTestData(5); // Больше точек
                    break;
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void checkPermissionsAndInitialize() {
        if (checkPermissions()) {
            initializeApp();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void initializeApp() {
        Toast.makeText(this, "Разрешения получены, приложение готово", Toast.LENGTH_SHORT).show();
    }

    private void startTracking() {
        Intent intent = new Intent(this, LocationTrackerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        isTracking = true;
        updateButtonState();
        Toast.makeText(this, "Отслеживание запущено", Toast.LENGTH_SHORT).show();
    }

    private void stopTracking() {
        Intent intent = new Intent(this, LocationTrackerService.class);
        stopService(intent);

        isTracking = false;
        updateButtonState();
        Toast.makeText(this, "Отслеживание остановлено", Toast.LENGTH_SHORT).show();
    }

    private void updateButtonState() {
        btnStart.setEnabled(!isTracking);
        btnStop.setEnabled(isTracking);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show();
                initializeApp();
            } else {
                Toast.makeText(this, "Необходимы разрешения для работы", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadWeatherForBrest() {
        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + BREST_LAT +
                "&longitude=" + BREST_LON +
                "&current_weather=true" +
                "&timezone=Europe/Moscow";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка загрузки погоды: " + e.getMessage());
                mainHandler.post(() -> {
                    tvWeatherDesc.setText("Ошибка загрузки");
                    tvWeatherIcon.setText("⚠️");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonObject currentWeather = json.getAsJsonObject("current_weather");

                        double temperature = currentWeather.get("temperature").getAsDouble();
                        int weatherCode = currentWeather.get("weathercode").getAsInt();

                        WeatherData weather = new WeatherData(temperature, weatherCode);

                        mainHandler.post(() -> updateWeatherUI(weather));

                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка парсинга: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void updateWeatherUI(WeatherData weather) {
        tvWeatherIcon.setText(weather.getWeatherIcon());
        tvWeatherTemp.setText(String.format("%.1f°C", weather.getTemperature()));
        tvWeatherDesc.setText(weather.getWeatherDescription());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Проверяем количество точек при каждом возвращении на экран
        if (testDataGenerator != null) {
            testDataGenerator.checkAndGenerateIfNeeded(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}