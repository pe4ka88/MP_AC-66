package com.example.myapplication3;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.util.List;

public interface ApiService {
    @GET
    Call<List<Photo>> getPhotos(@Url String url);
}