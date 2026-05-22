package com.example.lr3.data;

import com.example.lr3.model.PostsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("posts")
    Call<PostsResponse> getPosts();
}
