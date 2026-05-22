package com.example.jsonlistapp.network

import com.example.jsonlistapp.model.Post
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    fun getPosts(): Call<List<Post>>
}