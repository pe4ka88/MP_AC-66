package com.example.a3lab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.a3lab.databinding.FragmentListBinding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PostAdapter(new ArrayList<>(), post -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("post", post);
            Navigation.findNavController(view).navigate(R.id.action_listFragment_to_detailFragment, bundle);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.buttonLoad.setOnClickListener(v -> loadData());
    }

    private void loadData() {
        String baseUrl = binding.editTextUrl.getText().toString();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        String endpoint = binding.editTextEndpoint.getText().toString();
        String limitStr = binding.editTextLimit.getText().toString();
        Integer limit = null;
        if (!limitStr.isEmpty()) {
            limit = Integer.parseInt(limitStr);
        }

        Retrofit retrofit;
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid Base URL", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonElement> call = apiService.getRawData(endpoint, limit);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonElement json = response.body();
                    List<Post> dataList = new ArrayList<>();
                    Gson gson = new Gson();

                    if (json.isJsonArray()) {
                        // Формат JSONPlaceholder: [...]
                        Type listType = new TypeToken<List<Post>>(){}.getType();
                        dataList = gson.fromJson(json, listType);
                    } else if (json.isJsonObject()) {
                        // Формат Rick and Morty: { "results": [...] }
                        ApiResponse apiResponse = gson.fromJson(json, ApiResponse.class);
                        if (apiResponse != null && apiResponse.getResults() != null) {
                            dataList = apiResponse.getResults();
                        }
                    }

                    if (!dataList.isEmpty()) {
                        adapter.setPosts(dataList);
                    } else {
                        Toast.makeText(getContext(), "No data found in expected fields", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}