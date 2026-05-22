package com.example.json;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

public class MainActivity extends AppCompatActivity implements ListFragment.OnPostSelectedListener {

    private ListFragment listFragment;
    private DetailFragment detailFragment;
    private boolean isDualPane = false;
    private FrameLayout detailContainer;

    // Для погоды
    private CardView weatherCard;
    private TextView tvWeatherIcon, tvTemperature, tvWeatherDesc, tvWindHumidity, tvUpdateTime;
    private Button btnWeather;
    private OkHttpClient client;
    private Handler mainHandler;

    // Координаты Бреста
    private static final double BREST_LAT = 52.0976;
    private static final double BREST_LON = 23.7341;
    private static final String TAG = "MainActivity";

    // Настройки
    private boolean showImages = false;
    private int itemsCount = 20;
    private String customUrl = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация погоды
        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
        tvWindHumidity = findViewById(R.id.tvWindHumidity);
        tvUpdateTime = findViewById(R.id.tvUpdateTime);
        btnWeather = findViewById(R.id.btnWeather);

        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        // Проверяем, планшет ли это (ширина экрана > 600dp)
        detailContainer = findViewById(R.id.fragment_container_detail);
        if (detailContainer != null) {
            // Проверяем ориентацию и размер экрана
            Configuration config = getResources().getConfiguration();
            if (config.smallestScreenWidthDp >= 600) {
                // Это планшет
                isDualPane = true;
                detailContainer.setVisibility(View.VISIBLE);
            } else {
                // Это телефон
                isDualPane = false;
                detailContainer.setVisibility(View.GONE);
            }
        }

        // Создаем и добавляем ListFragment
        listFragment = new ListFragment();
        listFragment.setListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, listFragment);
        transaction.commit();

        // Кнопка загрузки данных
        Button btnLoad = findViewById(R.id.btnLoadData);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        // Кнопка погоды
        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weatherCard.getVisibility() == View.VISIBLE) {
                    weatherCard.setVisibility(View.GONE);
                } else {
                    weatherCard.setVisibility(View.VISIBLE);
                    loadWeatherForBrest();
                }
            }
        });

        // Кнопка настроек
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });
    }

    /**
     * Загрузка погоды для Бреста
     */
    private void loadWeatherForBrest() {
        tvWeatherDesc.setText("Загрузка...");
        tvTemperature.setText("--°C");
        tvWeatherIcon.setText("⏳");

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

                    try {
                        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonObject currentWeather = json.getAsJsonObject("current_weather");

                        double temperature = currentWeather.get("temperature").getAsDouble();
                        int weatherCode = currentWeather.get("weathercode").getAsInt();
                        double windSpeed = currentWeather.get("windspeed").getAsDouble();

                        int humidity = 0;
                        if (json.has("hourly")) {
                            JsonObject hourly = json.getAsJsonObject("hourly");
                            if (hourly.has("relative_humidity_2m")) {
                                humidity = hourly.getAsJsonArray("relative_humidity_2m").get(0).getAsInt();
                            }
                        }

                        WeatherData weather = new WeatherData(temperature, weatherCode, windSpeed, humidity);

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateWeatherUI(weather);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка парсинга: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Обновление UI с погодой
     */
    private void updateWeatherUI(WeatherData weather) {
        tvWeatherIcon.setText(weather.getWeatherIcon());
        tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", weather.getTemperature()));
        tvWeatherDesc.setText(weather.getWeatherDescription());
        tvWindHumidity.setText(String.format(Locale.getDefault(),
                "💨 %.1f м/с, 💧 %d%%", weather.getWindSpeed(), weather.getHumidity()));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvUpdateTime.setText("Обновлено: " + sdf.format(new Date()));
    }

    private void loadData() {
        listFragment.setShowImages(showImages);
        listFragment.setItemsToShow(itemsCount);

        if (customUrl != null && !customUrl.isEmpty() && !customUrl.equals("https://jsonplaceholder.typicode.com/posts")) {
            listFragment.loadFromCustomUrl(customUrl);
        } else {
            listFragment.loadData();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Настройки (Куган Н. Л.)");

        View view = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        final EditText etUrl = view.findViewById(R.id.etCustomUrl);
        final EditText etCount = view.findViewById(R.id.etItemsCount);
        final Button btnToggleImages = view.findViewById(R.id.btnToggleImages);

        etUrl.setText(customUrl);
        etCount.setText(String.valueOf(itemsCount));
        btnToggleImages.setText(showImages ? "Изображения: ВКЛ" : "Изображения: ВЫКЛ");

        btnToggleImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImages = !showImages;
                btnToggleImages.setText(showImages ? "Изображения: ВКЛ" : "Изображения: ВЫКЛ");
                Toast.makeText(MainActivity.this,
                        "Изображения " + (showImages ? "включены" : "выключены"),
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                String url = etUrl.getText().toString().trim();
                if (!url.isEmpty()) {
                    customUrl = url;
                }

                int count = Integer.parseInt(etCount.getText().toString());
                if (count > 0 && count <= 100) {
                    itemsCount = count;
                }

                Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Неверное число", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onPostSelected(Post post) {
        if (isDualPane) {
            // Для планшетов - показываем справа
            if (detailFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(detailFragment)
                        .commit();
            }
            detailFragment = DetailFragment.newInstance(post);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_detail, detailFragment)
                    .commit();
        } else {
            // Для телефонов - новый экран
            detailFragment = DetailFragment.newInstance(post);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}