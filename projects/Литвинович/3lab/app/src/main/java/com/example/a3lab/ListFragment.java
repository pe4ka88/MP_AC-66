package com.example.a3lab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.a3lab.R;
import com.example.a3lab.adapters.ItemAdapter;
import com.example.a3lab.models.ItemModel;
import com.example.a3lab.network.ApiService;
import com.example.a3lab.utils.DataStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListFragment extends Fragment implements ItemAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ItemAdapter adapter;
    private ApiService apiService;
    private DataStorage dataStorage;
    private List<ItemModel> currentItems = new ArrayList<>();
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelected(ItemModel item);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        apiService = new ApiService(getContext());
        dataStorage = new DataStorage(getContext());

        setupRecyclerView();
        setupSwipeRefresh();

        loadCachedData();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new ItemAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadDataFromServer();
        });
    }

    private void loadCachedData() {
        List<ItemModel> cachedItems = dataStorage.loadItems();
        if (cachedItems != null && !cachedItems.isEmpty()) {
            currentItems = cachedItems;
            adapter.setItems(currentItems);
            Toast.makeText(getContext(), "Загружены кэшированные данные", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.fetchItems(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ItemModel>>(){}.getType();
                    List<ItemModel> items = gson.fromJson(jsonData, type);

                    if (items != null && !items.isEmpty()) {
                        dataStorage.saveItems(items);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                currentItems = items;
                                adapter.setItems(currentItems);
                                progressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getContext(), "Загружено " + items.size() + " элементов", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(), "Ошибка сервера", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(ItemModel item) {
        if (listener != null) {
            listener.onItemSelected(item);
        }
    }
}