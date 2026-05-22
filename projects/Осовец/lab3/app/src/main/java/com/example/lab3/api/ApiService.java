package com.example.lab3.api;

import com.example.lab3.model.Comment;
import com.example.lab3.model.Photo;
import com.example.lab3.model.Post;
import com.example.lab3.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("users")
    Call<List<User>> getUsers();

    @GET("photos")
    Call<List<Photo>> getPhotos();

    @GET("comments")
    Call<List<Comment>> getComments();
}
