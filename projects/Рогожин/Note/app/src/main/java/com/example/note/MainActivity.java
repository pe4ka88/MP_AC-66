package com.example.note;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import com.example.note.BuildConfig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.note.api.ApiClient;
import com.example.note.api.EmbeddingRequest;
import com.example.note.api.HuggingFaceApi;
import com.example.note.fragments.FragmentShow;
import com.example.note.fragments.FragmentAdd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        setContentView(R.layout.activity_main);

        // ---- Поиск ----
        EditText etSearch = findViewById(R.id.etSearch);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {

            String query = etSearch.getText().toString().trim();

            if (!query.isEmpty()) {
                performSemanticSearch(query);
            } else {
                Toast.makeText(this,
                        "Введите запрос",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ---- Toolbar ----
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ---- Первый запуск фрагмента ----
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FragmentShow())
                    .commit();
        }

        // ---- FAB ----
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new FragmentAdd())
                        .addToBackStack(null)
                        .commit()
        );
    }
    private void performSemanticSearch(String query) {

        HuggingFaceApi api = ApiClient.getApi();
        EmbeddingRequest request = new EmbeddingRequest(query);

        api.getEmbedding("Bearer " + BuildConfig.HF_TOKEN, request)
                .enqueue(new retrofit2.Callback<List<Float>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<Float>> call, retrofit2.Response<List<Float>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Float> queryEmbedding = response.body();
                            FragmentShow fragment = (FragmentShow) getSupportFragmentManager()
                                    .findFragmentById(R.id.fragment_container);
                            if (fragment != null) {
                                fragment.semanticSearch(queryEmbedding);
                                fragment.logAllEmbeddings(queryEmbedding);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка поиска", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<Float>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_about) {
            Toast.makeText(this,
                    "MyNotes v1.0\nАвтор: Рогожин А.С. АС-66",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.menu_clear) {
            Toast.makeText(this,
                    "Функция очистки будет добавлена",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.menu_theme) {

            int currentMode = AppCompatDelegate.getDefaultNightMode();

            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}