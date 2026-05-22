package com.example.shop.api;

import com.example.shop.product.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("products")
    Call<ProductResponse> getProducts();
}