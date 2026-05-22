package com.example.note.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface HuggingFaceApi {
    @POST("hf-inference/models/sentence-transformers/all-MiniLM-L6-v2/pipeline/feature-extraction")
    Call<List<Float>> getEmbedding(
            @Header("Authorization") String token,
            @Body EmbeddingRequest request
    );
}