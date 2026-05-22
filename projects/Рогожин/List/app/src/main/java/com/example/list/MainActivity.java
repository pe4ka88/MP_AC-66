package com.example.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.list.api.Guitar;
import com.example.list.api.GuitarAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton btnLoad, btnSwitchView;
    private EditText etFilter;

    private GuitarAdapter adapter;

    private ArrayList<Guitar> guitarList = new ArrayList<>();
    private ArrayList<Guitar> filteredList = new ArrayList<>();

    private boolean isGrid = false;
    private boolean isLoading = false;

    private static final String API_URL =
            "https://raw.githubusercontent.com/LeChat-s/guitar/main/guitars-api/guitars.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnLoad = findViewById(R.id.btnLoad);
        btnSwitchView = findViewById(R.id.btnSwitchView);
        etFilter = findViewById(R.id.etFilter);

        Button btnSaveCsv = findViewById(R.id.btnSaveCsv);
        Button btnShareCsv = findViewById(R.id.btnShareCsv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuitarAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        btnLoad.setOnClickListener(v -> {
            guitarList.clear();
            filteredList.clear();
            loadData();
        });

        btnSwitchView.setOnClickListener(v -> switchLayout());

        btnSaveCsv.setOnClickListener(v -> saveCsv(filteredList));

        btnShareCsv.setOnClickListener(v -> {
            File file = saveCsv(filteredList);
            if (file != null) shareCsv(file);
        });

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter();
            }
        });
    }

    private void switchLayout() {
        if (isGrid) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        isGrid = !isGrid;
    }

    private void filter() {
        String text = etFilter.getText().toString().toLowerCase();

        filteredList.clear();

        for (Guitar g : guitarList) {
            if (g.getName().toLowerCase().contains(text) ||
                    g.getSummary().toLowerCase().contains(text)) {
                filteredList.add(g);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadData() {

        if (isLoading) return;
        isLoading = true;

        new Thread(() -> {

            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                parseJson(result.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            isLoading = false;

        }).start();
    }

    private void parseJson(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray array = root.getJSONArray("data");

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                Guitar guitar = new Guitar(
                        obj.optString("name"),
                        obj.optString("description"),
                        obj.optString("image"),
                        obj.optString("summary"),
                        (float) obj.optDouble("overall_rating"),
                        (float) obj.optDouble("body_rating"),
                        (float) obj.optDouble("hardware_rating"),
                        (float) obj.optDouble("sound_rating"),
                        (float) obj.optDouble("value_rating")
                );

                guitarList.add(guitar);
            }

            runOnUiThread(this::filter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CSV =================

    private File saveCsv(ArrayList<Guitar> list) {

        File file = new File(getExternalFilesDir(null), "guitars.csv");

        try (FileWriter writer = new FileWriter(file)) {

            writer.append("Name,Summary,Overall\n");

            for (Guitar g : list) {
                writer.append(g.getName()).append(",");
                writer.append(g.getSummary()).append(",");
                writer.append(String.valueOf(g.getOverallRating())).append("\n");
            }

            Toast.makeText(this, "CSV сохранен", Toast.LENGTH_SHORT).show();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void shareCsv(File file) {
        try {
            android.net.Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Поделиться"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}