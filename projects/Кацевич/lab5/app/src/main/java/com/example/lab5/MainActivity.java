package com.example.lab5;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnDatabaseChangedListener {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private FragmentShow fragmentShow;

    // Для погоды
    private CardView weatherCard;
    private TextView tvWeatherIcon, tvWeatherTemp, tvWeatherDesc;
    private OkHttpClient client;
    private Handler mainHandler;
    private static final String TAG = "MainActivity";

    // Координаты Бреста
    private static final double BREST_LAT = 52.0976;
    private static final double BREST_LON = 23.7341;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация ViewPager
        viewPager = findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        // Инициализация погоды
        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);

        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        // Загружаем погоду
        loadWeatherForBrest();

        // Проверяем базу данных
        checkDatabase();
    }

    @Override
    public void onDatabaseChanged() {
        // Получаем текущий фрагмент и обновляем его
        if (adapter != null) {
            fragmentShow = (FragmentShow) adapter.getItem(0);
            if (fragmentShow != null) {
                fragmentShow.refreshNotes();
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
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvWeatherDesc.setText("Ошибка загрузки");
                        tvWeatherIcon.setText("⚠️");
                    }
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

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateWeatherUI(weather);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка парсинга: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Ошибка ответа: " + response.code());
                }
            }
        });
    }

    private void updateWeatherUI(WeatherData weather) {
        tvWeatherIcon.setText(weather.getWeatherIcon());
        tvWeatherTemp.setText(String.format("%.1f°C", weather.getTemperature()));
        tvWeatherDesc.setText(weather.getWeatherDescription());
    }

    private void checkDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int count = dbHelper.getNotesCount();

        if (count >= 20) {
            Toast.makeText(this, " База данных содержит " + count + " заметок (требование выполнено)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, " База данных содержит только " + count + " заметок (нужно 20+)", Toast.LENGTH_LONG).show();
        }
    }
}