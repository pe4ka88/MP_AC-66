package com.example.mangalistezepchukac66;

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
import android.widget.Toast;

import com.example.mangalistapp.Manga;
import com.example.mangalistapp.MangaAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnLoad, btnSwitchView;
    private EditText etFilter;
    private ChipGroup chipGroupGenres;

    private MangaAdapter adapter;

    private ArrayList<Manga> mangaList = new ArrayList<>();
    private ArrayList<Manga> filteredList = new ArrayList<>();

    private boolean isGrid = false;

    // Пагинация
    private boolean isLoading = false;
    private int offset = 0;
    private static final int LIMIT = 20;

    private static final String API_URL =
            "https://api.mangadex.org/manga?limit=%d&offset=%d&contentRating[]=safe&includes[]=cover_art";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnLoad = findViewById(R.id.btnLoad);
        btnSwitchView = findViewById(R.id.btnSwitchView);
        etFilter = findViewById(R.id.etFilter);
        chipGroupGenres = findViewById(R.id.chipGroupGenres);

        Button btnSaveCsv = findViewById(R.id.btnSaveCsv);
        Button btnShareCsv = findViewById(R.id.btnShareCsv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MangaAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        // Загрузка данных
        btnLoad.setOnClickListener(v -> {
            offset = 0;
            mangaList.clear();
            filteredList.clear();
            chipGroupGenres.removeAllViews();
            loadData();
        });

        // Переключение сетка/список
        btnSwitchView.setOnClickListener(v -> switchLayout());

        // Сохранить CSV
        btnSaveCsv.setOnClickListener(v -> saveCsv(filteredList));

        // Поделиться CSV
        btnShareCsv.setOnClickListener(v -> {
            File csv = saveCsv(filteredList);
            if (csv != null) shareCsv(csv);
        });

        // Поиск по названию
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter();
            }
        });

        // Подгрузка при прокрутке
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm == null) return;

                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int first = lm.findFirstVisibleItemPosition();

                if (!isLoading && visible + first >= total - 4) {
                    loadData();
                }
            }
        });
    }

    // Переключение сетка/список
    private void switchLayout() {

        if (isGrid) {
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(this));
            btnSwitchView.setText("Сетка");
        } else {
            recyclerView.setLayoutManager(
                    new GridLayoutManager(this, 2));
            btnSwitchView.setText("Список");
        }

        isGrid = !isGrid;
    }

    // Фильтр по названию + выбранным жанрам
    private void filter() {

        String text =
                etFilter.getText()
                        .toString()
                        .toLowerCase();

        // выбранные жанры
        Set<String> selectedGenres =
                new HashSet<>();

        for (int id : chipGroupGenres.getCheckedChipIds()) {

            Chip chip =
                    chipGroupGenres.findViewById(id);

            if (chip != null) {
                selectedGenres.add(
                        chip.getText()
                                .toString()
                                .toLowerCase()
                );
            }
        }

        filteredList.clear();

        for (Manga m : mangaList) {

            boolean titleMatch =
                    m.getTitle()
                            .toLowerCase()
                            .contains(text);

            boolean genreMatch = true;

            if (!selectedGenres.isEmpty()) {

                genreMatch = false;

                for (String g : selectedGenres) {

                    if (m.getGenres()
                            .toLowerCase()
                            .contains(g)) {
                        genreMatch = true;
                        break;
                    }
                }
            }

            if (titleMatch && genreMatch) {
                filteredList.add(m);
            }
        }

        adapter.notifyDataSetChanged();
    }


    // Загрузка данных
    private void loadData() {

        if (isLoading) return;
        isLoading = true;

        new Thread(() -> {

            try {

                String urlStr =
                        String.format(API_URL, LIMIT, offset);

                URL url = new URL(urlStr);

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        conn.getInputStream()
                                )
                        );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                parseJson(result.toString());

                offset += LIMIT;

            } catch (Exception e) {
                e.printStackTrace();
            }

            isLoading = false;

        }).start();
    }

    // Парсинг JSON
    private void parseJson(String json) {

        try {

            JSONObject root = new JSONObject(json);
            JSONArray dataArray = root.getJSONArray("data");

            Set<String> genreSet = new HashSet<>();

            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject mangaObj =
                        dataArray.getJSONObject(i);

                JSONObject attributes =
                        mangaObj.getJSONObject("attributes");

                JSONObject titleObj =
                        attributes.getJSONObject("title");

                String title =
                        titleObj.optString("en");

                if (title.isEmpty()) {

                    Iterator<String> keys =
                            titleObj.keys();

                    if (keys.hasNext()) {
                        title = titleObj.optString(
                                keys.next()
                        );
                    }
                }

                if (title.isEmpty())
                    title = "No title";

                String description =
                        attributes.getJSONObject("description")
                                .optString("en", "");

                String rating =
                        attributes.optString(
                                "contentRating",
                                "unknown");

                JSONArray tagsArray =
                        attributes.getJSONArray("tags");

                StringBuilder genresBuilder =
                        new StringBuilder();

                for (int t = 0; t < tagsArray.length(); t++) {

                    JSONObject tag =
                            tagsArray.getJSONObject(t);

                    JSONObject tagAttr =
                            tag.getJSONObject("attributes");

                    String genre =
                            tagAttr.getJSONObject("name")
                                    .optString("en", "");

                    if (!genre.isEmpty()) {

                        genresBuilder
                                .append(genre)
                                .append(", ");

                        genreSet.add(genre);
                    }
                }

                String genres =
                        genresBuilder.toString();

                if (genres.endsWith(", "))
                    genres = genres.substring(
                            0, genres.length() - 2);

                // Обложка
                String mangaId =
                        mangaObj.getString("id");

                String coverFile = "";

                JSONArray relations =
                        mangaObj.getJSONArray(
                                "relationships");

                for (int j = 0; j < relations.length(); j++) {

                    JSONObject rel =
                            relations.getJSONObject(j);

                    if (rel.getString("type")
                            .equals("cover_art")) {

                        coverFile =
                                rel.getJSONObject("attributes")
                                        .getString("fileName");
                        break;
                    }
                }

                String coverUrl =
                        "https://uploads.mangadex.org/covers/"
                                + mangaId + "/" + coverFile;

                mangaList.add(
                        new Manga(
                                title,
                                description,
                                coverUrl,
                                genres,
                                rating
                        )
                );
            }

            runOnUiThread(() -> {

                for (String g : genreSet) {

                    Chip chip = new Chip(this);
                    chip.setText(g);
                    chip.setCheckable(true);
                    chip.setOnCheckedChangeListener(
                            (buttonView, isChecked) -> filter()
                    );
                    chipGroupGenres.addView(chip);
                }

                filter();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ==================== CSV ====================
    private File saveCsv(ArrayList<Manga> listToSave) {
        File csvFile = new File(getExternalFilesDir(null), "manga_list.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Title,Description,Genres,Rating,CoverUrl\n");
            for (Manga m : listToSave) {
                writer.append("\"").append(m.getTitle().replace("\"","\"\"")).append("\",");
                writer.append("\"").append(m.getDescription().replace("\"","\"\"")).append("\",");
                writer.append("\"").append(m.getGenres().replace("\"","\"\"")).append("\",");
                writer.append(m.getRating()).append(",");
                writer.append(m.getCoverUrl()).append("\n");
            }
            writer.flush();
            Toast.makeText(this, "CSV сохранен: " + csvFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return csvFile;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при сохранении CSV", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void shareCsv(File csvFile) {
        try {
            android.net.Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    csvFile
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Поделиться CSV"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при отправке CSV", Toast.LENGTH_SHORT).show();
        }
    }
}