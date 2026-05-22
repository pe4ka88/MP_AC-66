package com.example.lab_8;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface API {
    @GET("posts")
    Call<List<Item>> getItems();
}