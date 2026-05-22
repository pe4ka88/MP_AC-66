package com.example.myapplication3;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static ApiService getClient(String baseUrl) {
        if (baseUrl == null) baseUrl = "https://jsonplaceholder.typicode.com/";
        if (!baseUrl.endsWith("/")) baseUrl += "/";

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}