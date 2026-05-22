package com.example.labb3new3.API;

import com.example.labb3new3.model.Item;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("items.json")
    Call<List<Item>> getItems();
}
