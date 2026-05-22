package com.example.lab3mp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListFragment extends Fragment {

    RecyclerView recyclerView;
    ItemAdapter adapter;
    Button loadButton;

    OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadButton = view.findViewById(R.id.loadButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ItemAdapter(new ArrayList<>(), item -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, DetailFragment.newInstance(item))
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        loadButton.setOnClickListener(v -> loadData());
    }

    private void loadData() {
        String url = "https://jsonplaceholder.typicode.com/photos";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String json = response.body().string();

                Type listType = new TypeToken<List<Item>>() {}.getType();
                List<Item> items = new Gson().fromJson(json, listType);

                if (items.size() > 100) {
                    items = items.subList(0, 100);
                }

                List<Item> finalItems = items;

                requireActivity().runOnUiThread(() -> {
                    adapter.updateData(finalItems);
                    Toast.makeText(getContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
                });
            }

        });
    }
}
