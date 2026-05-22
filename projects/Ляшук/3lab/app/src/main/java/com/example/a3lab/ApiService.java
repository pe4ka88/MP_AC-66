package com.example.a3lab;

import java.util.List;
import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<JsonElement> getRawData(@Url String url, @Query("_limit") Integer limit);
}