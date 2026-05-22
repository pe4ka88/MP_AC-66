package com.example.lab7;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

    // Для погоды
    private CardView weatherCard;
    private TextView tvWeatherIcon, tvWeatherTemp, tvWeatherDesc;
    private OkHttpClient client;
    private Handler mainHandler;
    private static final String TAG = "MainActivity";

    // Координаты Бреста
    private static final double BREST_LAT = 52.0976;
    private static final double BREST_LON = 23.7341;

    private final String[] tabTitles = {"🎵 Audio", "🎬 Video", "📷 Camera", "📋 History"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewPager();
        setupWeather();
        loadWeatherForBrest();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }

    private void setupWeather() {
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());
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
}