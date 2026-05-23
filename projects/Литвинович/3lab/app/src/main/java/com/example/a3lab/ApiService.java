package com.example.a3lab.network;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private OkHttpClient client;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ApiPrefs";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String DEFAULT_URL = "https://jsonplaceholder.typicode.com/posts?_limit=20";

    public ApiService(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String getServerUrl() {
        return sharedPreferences.getString(KEY_SERVER_URL, DEFAULT_URL);
    }

    public void setServerUrl(String url) {
        sharedPreferences.edit().putString(KEY_SERVER_URL, url).apply();
    }

    public void fetchItems(Callback callback) {
        String url = getServerUrl();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
}