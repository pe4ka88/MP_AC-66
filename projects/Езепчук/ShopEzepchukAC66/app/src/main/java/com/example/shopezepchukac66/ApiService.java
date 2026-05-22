package com.example.shopezepchukac66;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("products")
    Call<ProductResponse> getProducts();
}
