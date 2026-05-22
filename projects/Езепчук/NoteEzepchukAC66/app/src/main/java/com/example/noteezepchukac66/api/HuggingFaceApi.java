package com.example.noteezepchukac66.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import java.util.List;

public interface HuggingFaceApi {
    @POST("hf-inference/models/sentence-transformers/all-MiniLM-L6-v2/pipeline/feature-extraction")
    Call<List<Float>> getEmbedding(
            @Header("Authorization") String token,
            @Body EmbeddingRequest request
    );
}