package com.example.labb3new3.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3new3.API.ApiService;
import com.example.labb3new3.R;
import com.example.labb3new3.Retrofit.RetrofitClient;
import com.example.labb3new3.adapter.ItemAdapter;
import com.example.labb3new3.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLoad = view.findViewById(R.id.btnLoad);
        RecyclerView rv = view.findViewById(R.id.rvItems);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ItemAdapter(item -> {
            DetailFragment fragment = DetailFragment.newInstance(item.title, item.description);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        rv.setAdapter(adapter);

        btnLoad.setOnClickListener(v -> loadData());
    }

    private void loadData() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    adapter.setItems(response.body());
                } else {
                    Log.e("SERVER", "Ошибка ответа: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("SERVER", "Ошибка запроса: " + t.getMessage());
            }
        });
    }
}
