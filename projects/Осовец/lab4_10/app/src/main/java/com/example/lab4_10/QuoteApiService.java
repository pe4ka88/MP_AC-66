package com.example.lab4_10;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Сервис для получения случайных цитат через ZenQuotes API.
 * API: https://zenquotes.io/api/random
 * Бесплатное публичное API, не требует ключа.
 */
public class QuoteApiService {

    private static final String API_URL = "https://zenquotes.io/api/random";

    public interface QuoteCallback {
        void onSuccess(String quote, String author);
        void onError(String errorMessage);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Выполняет асинхронный запрос к ZenQuotes API
     * и возвращает результат в callback на главном потоке.
     */
    public void fetchRandomQuote(QuoteCallback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    mainHandler.post(() -> callback.onError("Ошибка HTTP: " + responseCode));
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                // Парсим JSON: [{"q":"цитата","a":"автор",...}]
                JSONArray jsonArray = new JSONArray(sb.toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String quote = jsonObject.getString("q");
                String author = jsonObject.getString("a");

                mainHandler.post(() -> callback.onSuccess(quote, author));

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Ошибка: " + e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
