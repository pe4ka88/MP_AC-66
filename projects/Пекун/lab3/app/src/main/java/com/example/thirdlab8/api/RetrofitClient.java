package com.example.thirdlab8.api;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton класс для инициализации Retrofit
 * Управляет HTTP клиентом и настройками соединения
 */
public class RetrofitClient {
    
    private static RetrofitClient instance;
    private Retrofit retrofit;
    private String currentBaseUrl;
    private static final String DEFAULT_URL = "https://jsonplaceholder.typicode.com/";
    
    private RetrofitClient() {
        this.currentBaseUrl = DEFAULT_URL;
        buildRetrofit();
    }
    
    private void buildRetrofit() {
        // Логирование запросов (для отладки)
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Настройка OkHttpClient с таймаутами
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        
        // Инициализация Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    /**
     * Получение singleton экземпляра
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    /**
     * КРИТИЧЕСКИ ВАЖНО: Обновление базового URL (для настроек)
     * Теперь ПЕРЕСОЗДАЁТ Retrofit полностью
     */
    public void updateBaseUrl(String newBaseUrl) {
        if (newBaseUrl == null || newBaseUrl.isEmpty()) {
            newBaseUrl = DEFAULT_URL;
        }
        
        // Добавляем / в конце если нет
        if (!newBaseUrl.endsWith("/")) {
            newBaseUrl += "/";
        }
        
        // Добавляем https:// если нет протокола
        if (!newBaseUrl.startsWith("http://") && !newBaseUrl.startsWith("https://")) {
            newBaseUrl = "https://" + newBaseUrl;
        }
        
        // ТОЛЬКО если URL изменился - пересоздаём
        if (!this.currentBaseUrl.equals(newBaseUrl)) {
            this.currentBaseUrl = newBaseUrl;
            buildRetrofit(); // ПЕРЕСОЗДАНИЕ!
        }
    }
    
    /**
     * Инициализация с сохранённым URL из SharedPreferences
     */
    public void initFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String savedUrl = prefs.getString("server_url", DEFAULT_URL);
        updateBaseUrl(savedUrl);
    }
    
    /**
     * Получение API сервиса
     */
    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
    
    public String getCurrentBaseUrl() {
        return currentBaseUrl;
    }
}
