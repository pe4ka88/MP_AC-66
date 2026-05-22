package com.example.lab4;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemSelectedListener {

    private ListView listViewProducts;
    private TextView tvSelectedCount;
    private Button btnShowCart;
    private ProductAdapter adapter;
    private List<Product> productList;
    private LayoutInflater inflater;

    // Для погоды
    private CardView weatherCard;
    private TextView tvWeatherIcon, tvWeatherTemp, tvWeatherDesc;
    private OkHttpClient client;
    private Handler mainHandler;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = LayoutInflater.from(this);
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        // Инициализация погоды
        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);

        // Загружаем погоду
        loadWeatherForMinsk();

        // Инициализация списка
        listViewProducts = findViewById(R.id.listViewProducts);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        btnShowCart = findViewById(R.id.btnShowCart);

        // Создаем тестовые данные
        createTestProducts();

        // Создаем адаптер
        adapter = new ProductAdapter(productList, inflater, this);
        listViewProducts.setAdapter(adapter);

        // Добавляем Header
        TextView header = new TextView(this);
        header.setText("🛍️ Наши товары (Головкина В.Д.)");
        header.setPadding(16, 16, 16, 16);
        header.setTextSize(18);
        header.setTextColor(getResources().getColor(android.R.color.white));
        header.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        listViewProducts.addHeaderView(header);

        // Добавляем Footer уже есть в разметке

        btnShowCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Собираем выбранные товары
                List<Product> selectedProducts = new ArrayList<>();
                for (Product p : productList) {
                    if (p.isSelected()) {
                        selectedProducts.add(p);
                    }
                }

                if (selectedProducts.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Выберите хотя бы один товар!", Toast.LENGTH_SHORT).show();
                } else {
                    // Переход в корзину
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    intent.putExtra("selected_products", new ArrayList<>(selectedProducts));
                    startActivity(intent);
                }
            }
        });
    }

    private void createTestProducts() {
        productList = new ArrayList<>();
        productList.add(new Product(1, "Смартфон Samsung", 29999.99));
        productList.add(new Product(2, "Ноутбук Lenovo", 45999.99));
        productList.add(new Product(3, "Наушники Sony", 5999.99));
        productList.add(new Product(4, "Клавиатура Logitech", 2499.99));
        productList.add(new Product(5, "Мышь беспроводная", 1299.99));
        productList.add(new Product(6, "Монитор LG", 18999.99));
        productList.add(new Product(7, "Внешний диск 1TB", 3999.99));
        productList.add(new Product(8, "Планшет iPad", 39999.99));
        productList.add(new Product(9, "Зарядное устройство", 999.99));
        productList.add(new Product(10, "Чехол для телефона", 499.99));
    }

    private void loadWeatherForMinsk() {
        // Координаты Минска
        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=53.9045" +
                "&longitude=27.5615" +
                "&current_weather=true" +
                "&timezone=Europe/Moscow";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка погоды: " + e.getMessage());
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
    public void onItemSelectedChanged() {
        // Подсчитываем количество выбранных товаров
        int count = 0;
        for (Product p : productList) {
            if (p.isSelected()) {
                count++;
            }
        }
        tvSelectedCount.setText("Выбрано товаров: " + count);
    }
}