package com.example.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserPhone, tvRoute;
    private Button btnSetPath, btnCallTaxi;

    // Для погоды
    private CardView weatherCard;
    private TextView tvWeatherIcon, tvTemperature, tvWeatherDesc, tvWindHumidity, tvUpdateTime;
    private OkHttpClient client;
    private Handler mainHandler;

    // Координаты Бреста
    private static final double BREST_LAT = 52.0976;
    private static final double BREST_LON = 23.7341;

    private static final String TAG = "SecondActivity_Lifecycle";
    private static final int REQUEST_CODE_ROUTE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Log.d(TAG, "onCreate вызван");

        // Инициализация View
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvRoute = findViewById(R.id.tvRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        // Инициализация погоды
        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
        tvWindHumidity = findViewById(R.id.tvWindHumidity);
        tvUpdateTime = findViewById(R.id.tvUpdateTime);

        // HTTP клиент и Handler для обновления UI
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        // Получаем данные из Intent
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("FIRST_NAME");
        String lastName = intent.getStringExtra("LAST_NAME");
        String phone = intent.getStringExtra("PHONE");

        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";
        if (phone == null) phone = "";

        // Отображаем данные
        tvUserName.setText("Пассажир: " + firstName + " " + lastName);
        tvUserPhone.setText("Телефон: " + phone);

        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ROUTE);
            }
        });

        btnCallTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "🚖 Такси успешно отправлено! Савинец М. Д.", Toast.LENGTH_LONG).show();
            }
        });

        // Загружаем погоду для Бреста
        loadWeatherForBrest();
    }

    /**
     * Загрузка погоды для Бреста через Open-Meteo API
     */
    private void loadWeatherForBrest() {
        // URL для Open-Meteo API (без ключа!)
        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + BREST_LAT +
                "&longitude=" + BREST_LON +
                "&current_weather=true" +
                "&hourly=relative_humidity_2m" +
                "&timezone=Europe/Moscow" +
                "&forecast_days=1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка загрузки погоды: " + e.getMessage());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvWeatherDesc.setText("Ошибка загрузки");
                        tvWeatherIcon.setText("⚠️");
                        tvUpdateTime.setText("Проверьте интернет");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Погода: " + responseBody);

                    // Парсим JSON
                    try {
                        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                        // Текущая погода
                        JsonObject currentWeather = json.getAsJsonObject("current_weather");
                        double temperature = currentWeather.get("temperature").getAsDouble();
                        int weatherCode = currentWeather.get("weathercode").getAsInt();
                        double windSpeed = currentWeather.get("windspeed").getAsDouble();

                        // Влажность (берем первый час из hourly)
                        int humidity = 0;
                        if (json.has("hourly")) {
                            JsonObject hourly = json.getAsJsonObject("hourly");
                            if (hourly.has("relative_humidity_2m")) {
                                humidity = hourly.getAsJsonArray("relative_humidity_2m").get(0).getAsInt();
                            }
                        }

                        // Создаем объект погоды
                        WeatherData weather = new WeatherData(temperature, weatherCode, windSpeed, humidity);

                        // Обновляем UI
                        final int finalHumidity = humidity;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateWeatherUI(weather, finalHumidity);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка парсинга JSON: " + e.getMessage());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvWeatherDesc.setText("Ошибка данных");
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Ошибка HTTP: " + response.code());
                }
            }
        });
    }

    /**
     * Обновление UI с данными погоды
     */
    private void updateWeatherUI(WeatherData weather, int humidity) {
        // Иконка в зависимости от погоды
        String icon = getWeatherIcon(weather.getWeatherCode());
        tvWeatherIcon.setText(icon);

        // Температура
        tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", weather.getTemperature()));

        // Описание погоды
        tvWeatherDesc.setText(weather.getWeatherDescription());

        // Ветер и влажность
        tvWindHumidity.setText(String.format(Locale.getDefault(),
                "💨 %.1f м/с, 💧 %d%%", weather.getWindSpeed(), humidity));

        // Время обновления
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvUpdateTime.setText("Обновлено: " + sdf.format(new Date()));
    }

    /**
     * Получение иконки по коду погоды WMO
     */
    private String getWeatherIcon(int code) {
        switch (code) {
            case 0: return "☀️";
            case 1: return "🌤";
            case 2: return "⛅";
            case 3: return "☁️";
            case 45: case 48: return "🌫";
            case 51: case 53: case 55: return "🌧";
            case 61: case 63: case 65: return "🌧";
            case 71: case 73: case 75: return "🌨";
            case 80: case 81: case 82: return "🌦";
            case 95: return "⛈";
            case 96: case 99: return "⛈";
            default: return "🌡";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ROUTE && resultCode == RESULT_OK && data != null) {
            String routePoints = data.getStringExtra("ROUTE_POINTS");
            if (routePoints != null && !routePoints.isEmpty()) {
                tvRoute.setText("Маршрут: " + routePoints);
                btnCallTaxi.setEnabled(true);
                Toast.makeText(this, "Маршрут получен! Можно вызвать такси.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Логирование жизненного цикла
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart вызван");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume вызван");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause вызван");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop вызван");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy вызван");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart вызван");
    }
}