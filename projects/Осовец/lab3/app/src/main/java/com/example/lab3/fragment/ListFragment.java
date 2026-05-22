package com.example.lab3.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab3.R;
import com.example.lab3.adapter.DataAdapter;
import com.example.lab3.api.ApiService;
import com.example.lab3.api.RetrofitClient;
import com.example.lab3.model.Comment;
import com.example.lab3.model.Displayable;
import com.example.lab3.model.Photo;
import com.example.lab3.model.Post;
import com.example.lab3.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private Spinner spinnerDataType;
    private Button btnLoad;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty, tvPageInfo;
    private Button btnPrev, btnNext, btnExport, btnShare;

    private DataAdapter adapter;
    private final List<Displayable> allItems = new ArrayList<>();
    private int currentPage = 0;
    private int pageSize = 20;
    private String serverUrl = "https://jsonplaceholder.typicode.com";
    private boolean showImages = true;
    private String currentDataType = "Posts";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        initViews(view);
        loadPreferences();
        setupSpinner();
        setupRecyclerView();
        setupClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
        if (adapter != null) {
            adapter.setShowImages(showImages);
        }
    }

    private void initViews(View view) {
        spinnerDataType = view.findViewById(R.id.spinner_data_type);
        btnLoad = view.findViewById(R.id.btn_load);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        tvPageInfo = view.findViewById(R.id.tv_page_info);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);
        btnExport = view.findViewById(R.id.btn_export);
        btnShare = view.findViewById(R.id.btn_share);
    }

    private void loadPreferences() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("lab3_settings", Context.MODE_PRIVATE);
        serverUrl = prefs.getString("server_url", "https://jsonplaceholder.typicode.com");
        pageSize = prefs.getInt("page_size", 20);
        showImages = prefs.getBoolean("show_images", true);
    }

    private void setupSpinner() {
        String[] dataTypes = {"Posts", "Users", "Photos", "Comments"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, dataTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDataType.setAdapter(spinnerAdapter);

        spinnerDataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentDataType = dataTypes[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new DataAdapter(requireContext());
        adapter.setShowImages(showImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            DetailFragment detailFragment = DetailFragment.newInstance(item);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupClickListeners() {
        btnLoad.setOnClickListener(v -> loadData());

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                displayPage();
            }
        });

        btnNext.setOnClickListener(v -> {
            int totalPages = getTotalPages();
            if (currentPage < totalPages - 1) {
                currentPage++;
                displayPage();
            }
        });

        btnExport.setOnClickListener(v -> exportToCsv());
        btnShare.setOnClickListener(v -> shareData());
    }

    private void loadData() {
        loadPreferences();
        adapter.setShowImages(showImages);

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        btnLoad.setEnabled(false);

        try {
            RetrofitClient.resetClient();
            ApiService apiService = RetrofitClient.getClient(serverUrl).create(ApiService.class);

            switch (currentDataType) {
                case "Posts":
                    loadPosts(apiService);
                    break;
                case "Users":
                    loadUsers(apiService);
                    break;
                case "Photos":
                    loadPhotos(apiService);
                    break;
                case "Comments":
                    loadComments(apiService);
                    break;
                default:
                    handleError("Неизвестный тип данных");
                    break;
            }
        } catch (Exception e) {
            handleError("Ошибка создания запроса: " + e.getMessage());
        }
    }

    private void loadPosts(ApiService apiService) {
        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                handleError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void loadUsers(ApiService apiService) {
        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                handleError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void loadPhotos(ApiService apiService) {
        apiService.getPhotos().enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                handleError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void loadComments(ApiService apiService) {
        apiService.getComments().enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                handleError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private <T extends Displayable> void handleResponse(Response<List<T>> response) {
        if (getActivity() == null) return;
        if (response.isSuccessful() && response.body() != null) {
            allItems.clear();
            allItems.addAll(response.body());
            currentPage = 0;
            displayPage();
            Toast.makeText(requireContext(),
                    "Загружено " + allItems.size() + " элементов (Осовец)", Toast.LENGTH_SHORT).show();
        } else {
            handleError("Ошибка сервера: код " + response.code());
        }
    }

    private void displayPage() {
        if (getActivity() == null) return;
        progressBar.setVisibility(View.GONE);
        btnLoad.setEnabled(true);

        if (allItems.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.no_data);
            recyclerView.setVisibility(View.GONE);
            tvPageInfo.setText("Нет данных");
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        int totalPages = getTotalPages();
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allItems.size());

        List<Displayable> pageItems = new ArrayList<>(allItems.subList(start, end));
        adapter.setItems(pageItems);
        recyclerView.scrollToPosition(0);

        tvPageInfo.setText("Стр. " + (currentPage + 1) + " / " + totalPages +
                " (всего: " + allItems.size() + ")");
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
    }

    private int getTotalPages() {
        if (pageSize <= 0) pageSize = 20;
        return Math.max(1, (int) Math.ceil((double) allItems.size() / pageSize));
    }

    private void handleError(String message) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            btnLoad.setEnabled(true);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(message);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
    }

    private void exportToCsv() {
        if (allItems.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных для экспорта", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            String fileName = "export_" + currentDataType.toLowerCase() + ".csv";
            File file = new File(dir, fileName);

            FileWriter writer = new FileWriter(file);
            writer.append(getCsvHeader()).append("\n");
            for (Displayable item : allItems) {
                writer.append(item.toCsvRow()).append("\n");
            }
            writer.flush();
            writer.close();

            Toast.makeText(requireContext(),
                    "CSV сохранён: " + file.getName() + " (Осовец)", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getCsvHeader() {
        switch (currentDataType) {
            case "Posts":
                return "id,userId,title,body";
            case "Users":
                return "id,name,username,email,phone,website";
            case "Photos":
                return "id,albumId,title,url,thumbnailUrl";
            case "Comments":
                return "id,postId,name,email,body";
            default:
                return "data";
        }
    }

    private void shareData() {
        if (allItems.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных для отправки", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(currentDataType).append(" — данные из ").append(serverUrl).append("\n\n");

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allItems.size());
        for (int i = start; i < end; i++) {
            sb.append("--- #").append(allItems.get(i).getId()).append(" ---\n");
            sb.append(allItems.get(i).getDetailInfo()).append("\n\n");
        }
        sb.append("Отправлено из Lab3 (Осовец А.О.)");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentDataType + " — Lab3 Осовец");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Поделиться данными"));
    }
}
