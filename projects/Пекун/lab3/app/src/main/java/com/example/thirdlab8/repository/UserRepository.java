package com.example.thirdlab8.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.thirdlab8.api.ApiService;
import com.example.thirdlab8.api.RetrofitClient;
import com.example.thirdlab8.model.User;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository для управления данными пользователей
 * ИСПРАВЛЕНО: Получаем свежий ApiService перед каждым запросом
 * 
 * Лабораторная работа №8
 * Пекун Марк Сергеевич
 * Группа АС-66
 */
public class UserRepository {
    
    private static final String TAG = "UserRepository";
    private final Handler mainHandler;
    private final List<Call<?>> activeCalls;
    
    public UserRepository() {
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.activeCalls = new ArrayList<>();
    }
    
    /**
     * Callback интерфейс для передачи результатов
     */
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
    
    /**
     * Загрузка пользователей с сервера
     * @param limit количество элементов
     * @param callback callback для результата
     */
    public void loadUsers(int limit, DataCallback<List<User>> callback) {
        // КРИТИЧЕСКИ ВАЖНО: Получаем СВЕЖИЙ ApiService перед запросом!
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        
        Log.d(TAG, "Загрузка пользователей: limit=" + limit + ", URL=" + 
              RetrofitClient.getInstance().getCurrentBaseUrl());
        
        Call<List<User>> call = apiService.getUsers(limit);
        activeCalls.add(call);
        
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                activeCalls.remove(call);
                
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        List<User> users = response.body();
                        Log.d(TAG, "Успешно загружено: " + users.size() + " элементов");
                        if (users.isEmpty()) {
                            callback.onError("Пустой ответ от сервера");
                        } else {
                            callback.onSuccess(users);
                        }
                    } else {
                        String errorMsg = "Ошибка сервера: " + response.code();
                        if (response.code() == 404) {
                            errorMsg = "Сервер не найден (404)";
                        } else if (response.code() == 500) {
                            errorMsg = "Внутренняя ошибка сервера (500)";
                        }
                        Log.e(TAG, errorMsg);
                        callback.onError(errorMsg);
                    }
                });
            }
            
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                activeCalls.remove(call);
                
                if (call.isCanceled()) {
                    return;
                }
                
                mainHandler.post(() -> {
                    String errorMessage;
                    if (t instanceof UnknownHostException) {
                        errorMessage = "Нет подключения к интернету\nПроверьте сетевое соединение";
                    } else if (t instanceof SocketTimeoutException) {
                        errorMessage = "Превышено время ожидания\nСервер не отвечает";
                    } else if (t instanceof IOException) {
                        errorMessage = "Ошибка сети\n" + t.getMessage();
                    } else {
                        errorMessage = "Ошибка загрузки данных\n" + t.getMessage();
                    }
                    Log.e(TAG, errorMessage, t);
                    callback.onError(errorMessage);
                });
            }
        });
    }
    
    /**
     * Отмена всех активных запросов
     */
    public void cancelRequests() {
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        activeCalls.clear();
    }
}
