package com.example.a3lab.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import com.example.a3lab.R;
import com.example.a3lab.adapter.ItemAdapter;
import com.example.a3lab.model.Item;
import com.example.a3lab.network.ApiService;
import com.example.a3lab.utils.SharedPrefsManager;

public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button loadButton;
    private EditText customEndpointInput;
    private ItemAdapter adapter;
    private ApiService apiService;
    private SharedPrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        loadButton = view.findViewById(R.id.loadButton);
        customEndpointInput = view.findViewById(R.id.customEndpointInput);

        // Используем строки из resources
        customEndpointInput.setHint(R.string.custom_endpoint_hint);
        loadButton.setText(R.string.load);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter();
        recyclerView.setAdapter(adapter);

        apiService = new ApiService();
        prefsManager = new SharedPrefsManager(requireContext());

        adapter.setOnItemClickListener(item -> {
            Bundle bundle = new Bundle();
            bundle.putInt("item_id", item.getId());
            bundle.putString("item_title", item.getTitle());
            bundle.putString("item_description", item.getDescription());
            bundle.putString("item_category", item.getCategory());
            bundle.putDouble("item_rating", item.getRating());
            bundle.putString("item_imageUrl", item.getImageUrl());

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_list_to_detail, bundle);
        });

        loadButton.setOnClickListener(v -> loadData());

        loadData();

        return view;
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        loadButton.setEnabled(false);

        String serverUrl = prefsManager.getServerUrl();
        String endpoint = customEndpointInput.getText().toString().trim();
        if (endpoint.isEmpty()) {
            endpoint = prefsManager.getSelectedEndpoint();
        }

        apiService.fetchItems(serverUrl, endpoint, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(List<Item> items) {
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                    loadButton.setEnabled(true);
                    if (items.isEmpty()) {
                        Snackbar.make(recyclerView, R.string.no_data, Snackbar.LENGTH_SHORT).show();
                    } else {
                        adapter.setItems(items);
                        Toast.makeText(getContext(),
                                getString(R.string.loaded_items, items.size()),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                    loadButton.setEnabled(true);
                    Snackbar.make(recyclerView,
                            getString(R.string.error_loading) + ": " + error,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}