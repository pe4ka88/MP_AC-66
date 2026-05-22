package com.example.a3lab.network;

import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.a3lab.model.Item;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiService {
    private final OkHttpClient client;
    private final Gson gson;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface ApiCallback {
        void onSuccess(List<Item> items);
        void onError(String error);
    }

    public ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchItems(String baseUrl, String endpoint, ApiCallback callback) {
        String url = baseUrl + endpoint;
        executorService.execute(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    List<Item> items = parseJsonToItems(json);
                    mainHandler.post(() -> callback.onSuccess(items));
                } else {
                    mainHandler.post(() -> callback.onError("Server error: " + response.code()));
                }
            } catch (IOException e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        });
    }

    private List<Item> parseJsonToItems(String json) {
        List<Item> items = new ArrayList<>();

        try {
            JsonElement element = JsonParser.parseString(json);

            if (element.isJsonArray()) {
                // Если пришел массив
                JsonArray jsonArray = element.getAsJsonArray();
                Type listType = new TypeToken<List<Item>>(){}.getType();
                items = gson.fromJson(jsonArray, listType);
            } else if (element.isJsonObject()) {
                // Если пришел один объект
                JsonObject jsonObject = element.getAsJsonObject();
                Item item = gson.fromJson(jsonObject, Item.class);
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}