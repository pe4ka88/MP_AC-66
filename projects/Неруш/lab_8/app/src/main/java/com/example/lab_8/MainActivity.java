package com.example.lab_8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    private List<Item> allDataList = new ArrayList<>();

    private int pageSize = 10;
    private int currentPage = 0;
    private boolean isListView = true;

    private TextView textViewPageInfo;
    private EditText editTextPageSize;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        textViewPageInfo = findViewById(R.id.textViewPageInfo);
        editTextPageSize = findViewById(R.id.editTextPageSize);
        fragmentContainer = findViewById(R.id.fragment_container);

        adapter = new ItemAdapter(itemList, this::showItemDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.buttonLoadData).setOnClickListener(v -> loadDataFromServer());
        findViewById(R.id.buttonExportCsv).setOnClickListener(v -> exportToFile("csv"));
        findViewById(R.id.buttonExportTxt).setOnClickListener(v -> exportToFile("txt"));
        findViewById(R.id.buttonToggleView).setOnClickListener(v -> toggleLayoutView());

        findViewById(R.id.buttonApplyPageSize).setOnClickListener(v -> {
            try {
                int newSize = Integer.parseInt(editTextPageSize.getText().toString());
                if (newSize > 0) {
                    pageSize = newSize;
                    currentPage = 0;
                    updatePagination();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.buttonPrevPage).setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePagination();
            }
        });

        findViewById(R.id.buttonNextPage).setOnClickListener(v -> {
            if ((currentPage + 1) * pageSize < allDataList.size()) {
                currentPage++;
                updatePagination();
            }
        });
    }

    private void loadDataFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API apiService = retrofit.create(API.class);
        apiService.getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allDataList.clear();
                    allDataList.addAll(response.body());
                    currentPage = 0;
                    updatePagination();
                    Toast.makeText(MainActivity.this, "Данные обновлены", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePagination() {
        itemList.clear();
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allDataList.size());

        if (start < allDataList.size()) {
            itemList.addAll(allDataList.subList(start, end));
        }

        adapter.notifyDataSetChanged();
        int totalPages = (int) Math.ceil((double) allDataList.size() / pageSize);
        textViewPageInfo.setText("Стр. " + (currentPage + 1) + " / " + Math.max(1, totalPages));
    }

    private void toggleLayoutView() {
        isListView = !isListView;
        recyclerView.setLayoutManager(isListView ? new LinearLayoutManager(this) : new GridLayoutManager(this, 2));
    }

    private void exportToFile(String format) {
        String fileName = "data." + format;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            if (format.equals("csv")) {
                writer.append("ID;Title;Body\n");
                for (Item item : allDataList) {
                    writer.append(item.getId() + ";" + item.getTitle() + ";" + item.getBody().replace("\n", " ") + "\n");
                }
            } else {
                for (Item item : allDataList) {
                    writer.append("ID: ").append(String.valueOf(item.getId())).append("\n");
                    writer.append("Title: ").append(item.getTitle()).append("\n");
                    writer.append("Body: ").append(item.getBody()).append("\n");
                    writer.append("----------------------------\n");
                }
            }
            Toast.makeText(this, "Сохранено: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void showItemDetails(Item item) {
        fragmentContainer.setVisibility(View.VISIBLE);
        Details detailFragment = Details.newInstance(item.getId(), item.getTitle(), item.getBody());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .commit();
    }

    public void hideDetails() {
        fragmentContainer.setVisibility(View.GONE);
    }
}