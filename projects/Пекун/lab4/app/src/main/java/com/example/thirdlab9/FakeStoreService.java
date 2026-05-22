package com.example.thirdlab9;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FakeStoreService {

    private static final String TAG = "FakeStoreService";
    private static final String BASE_URL = "https://fakestoreapi.com/products?limit=25";

    public interface OnProductsLoadedListener {
        void onSuccess(List<Product> products);
        void onError(String message);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler  = new Handler(Looper.getMainLooper());

    /** Загружает 25 товаров асинхронно, возвращает результат в main thread */
    public void loadProducts(OnProductsLoadedListener listener) {
        executor.execute(() -> {
            try {
                String json = fetchJson(BASE_URL);
                List<Product> products = parseProducts(json);
                mainHandler.post(() -> listener.onSuccess(products));
            } catch (Exception e) {
                Log.e(TAG, "Load failed: " + e.getMessage());
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        });
    }

    private String fetchJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("Accept", "application/json");

        int code = conn.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        conn.disconnect();
        return sb.toString();
    }

    private List<Product> parseProducts(String json) throws Exception {
        List<Product> list = new ArrayList<>();
        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            int    id       = obj.getInt("id");
            String title    = obj.getString("title");
            double price    = obj.getDouble("price");
            String imageUrl = obj.getString("image");
            String category = obj.getString("category");

            // Обрезаем длинное название до 28 символов
            String shortName = title.length() > 28
                    ? title.substring(0, 28).trim() + "…"
                    : title;

            Product p = new Product(id, shortName, price);
            p.setImageUrl(imageUrl);
            p.setCategory(category);
            list.add(p);
        }
        return list;
    }
}
