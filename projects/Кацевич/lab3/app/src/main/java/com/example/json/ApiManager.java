package com.example.json;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiManager {
    private static final String TAG = "ApiManager";
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private OkHttpClient client;
    private Handler mainHandler;
    private Gson gson;

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public ApiManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        mainHandler = new Handler(Looper.getMainLooper());
        gson = new Gson();
    }

    /**
     * Загрузка постов с сервера
     */
    public void fetchPosts(final ApiCallback<List<Post>> callback) {
        String url = BASE_URL + "/posts";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                postError(callback, "Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    postError(callback, "Ошибка сервера: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    Log.d(TAG, "JSON received: " + jsonData.length() + " chars");

                    // Парсим JSON в список постов
                    Type listType = new TypeToken<List<Post>>(){}.getType();
                    List<Post> posts = gson.fromJson(jsonData, listType);

                    // Добавляем изображения для бонуса
                    addRandomImages(posts);

                    postSuccess(callback, posts);

                } catch (Exception e) {
                    Log.e(TAG, "Parse error: " + e.getMessage());
                    postError(callback, "Ошибка парсинга данных: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }

    /**
     * Загрузка с кастомным URL (бонус - настройка пути)
     */
    public void fetchFromCustomUrl(String customUrl, final ApiCallback<List<Post>> callback) {
        Request request = new Request.Builder()
                .url(customUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postError(callback, "Ошибка сети: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    postError(callback, "Ошибка сервера: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();

                    // Пытаемся распарсить как массив
                    try {
                        Type listType = new TypeToken<List<Post>>(){}.getType();
                        List<Post> posts = gson.fromJson(jsonData, listType);
                        addRandomImages(posts);
                        postSuccess(callback, posts);
                    } catch (Exception e) {
                        // Если не массив, пробуем как один объект
                        Post post = gson.fromJson(jsonData, Post.class);
                        List<Post> posts = new ArrayList<>();
                        posts.add(post);
                        addRandomImages(posts);
                        postSuccess(callback, posts);
                    }

                } catch (Exception e) {
                    postError(callback, "Ошибка парсинга: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }

    /**
     * Добавление случайных изображений для бонуса
     */
    private void addRandomImages(List<Post> posts) {
        String[] imageUrls = {
                "https://picsum.photos/400/300?random=1",
                "https://picsum.photos/400/300?random=2",
                "https://picsum.photos/400/300?random=3",
                "https://picsum.photos/400/300?random=4",
                "https://picsum.photos/400/300?random=5"
        };

        for (int i = 0; i < posts.size(); i++) {
            posts.get(i).setImageUrl(imageUrls[i % imageUrls.length]);
        }
    }

    private void postSuccess(final ApiCallback callback, final Object result) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(result);
            }
        });
    }

    private void postError(final ApiCallback callback, final String error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(error);
            }
        });
    }
}