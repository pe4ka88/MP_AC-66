package com.example.thirdlab8.api;

import com.example.thirdlab8.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API интерфейс для работы с удаленным сервером
 * Использует Retrofit для HTTP запросов
 */
public interface ApiService {
    
    /**
     * Загрузка списка пользователей
     * @param limit количество элементов для загрузки
     * @return Call с списком пользователей
     */
    @GET("users")
    Call<List<User>> getUsers(@Query("_limit") int limit);
    
    /**
     * Альтернативный endpoint (для демонстрации выбора варианта)
     */
    @GET("posts")
    Call<List<User>> getPosts(@Query("_limit") int limit);
}
