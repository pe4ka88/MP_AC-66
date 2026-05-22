package com.example.geo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geo.data.LocationDatabaseHelper;
import com.example.geo.data.LocationPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.*;
import com.yandex.runtime.Error;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvResult;
    private LocationDatabaseHelper dbHelper;
    private SearchManager searchManager;

    private long fromDate;
    private long toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics);

        tvResult = findViewById(R.id.tvResult);
        dbHelper = new LocationDatabaseHelper(this);

        fromDate = getIntent().getLongExtra("fromDate", 0);
        toDate = getIntent().getLongExtra("toDate", System.currentTimeMillis());

        searchManager = SearchFactory.getInstance()
                .createSearchManager(SearchManagerType.COMBINED);

        loadStatistics();
    }

    private void loadStatistics() {

        List<LocationPoint> points =
                dbHelper.getByPeriod(fromDate, toDate);

        if (points.isEmpty()) {
            tvResult.setText("Нет данных");
            return;
        }

        // 1️⃣ Сортируем по времени (ВАЖНО!)
        points.sort(Comparator.comparingLong(lp -> lp.timestamp));

        Map<String, Integer> map = new HashMap<>();

        String lastOrganizationKey = null;
        int validVisitsCount = 0;

        for (LocationPoint lp : points) {

            String currentKey = String.format("%.3f_%.3f",
                    lp.latitude,
                    lp.longitude);

            // 2️⃣ Если это та же организация что и предыдущая — пропускаем
            if (currentKey.equals(lastOrganizationKey)) {
                continue;
            }

            // 3️⃣ Иначе считаем посещение
            map.put(currentKey,
                    map.getOrDefault(currentKey, 0) + 1);

            validVisitsCount++;

            lastOrganizationKey = currentKey;
        }

        // Сортировка по количеству посещений
        List<Map.Entry<String, Integer>> sorted =
                new ArrayList<>(map.entrySet());

        sorted.sort((a, b) -> b.getValue() - a.getValue());

        StringBuilder result = new StringBuilder();
        result.append("Всего уникальных посещений: ")
                .append(validVisitsCount)
                .append("\n\n");

        int limit = Math.min(5, sorted.size());

        processNextOrganization(
                sorted,
                0,
                limit,
                result
        );
    }

    private void processNextOrganization(
            List<Map.Entry<String, Integer>> sorted,
            int index,
            int limit,
            StringBuilder result) {

        if (index >= limit) {
            tvResult.setText(result.toString());
            return;
        }

        String[] coords = sorted.get(index)
                .getKey()
                .split("_");

        double lat = Double.parseDouble(coords[0]);
        double lon = Double.parseDouble(coords[1]);

        int visits = sorted.get(index).getValue();

        getOrganizationName(
                lat,
                lon,
                visits,
                index + 1,
                result,
                () -> processNextOrganization(
                        sorted,
                        index + 1,
                        limit,
                        result
                )
        );
    }

    private void getOrganizationName(
            double lat,
            double lon,
            int visits,
            int index,
            StringBuilder result,
            Runnable onComplete) {

        searchManager.submit(
                new Point(lat, lon),
                16,
                new SearchOptions(),
                new Session.SearchListener() {

                    @Override
                    public void onSearchResponse(Response response) {

                        String name = "Неизвестно";
                        String category = "";

                        if (!response.getCollection()
                                .getChildren().isEmpty()) {

                            var obj = response.getCollection()
                                    .getChildren()
                                    .get(0)
                                    .getObj();

                            name = obj.getName();

                            BusinessObjectMetadata metadata =
                                    obj.getMetadataContainer()
                                            .getItem(BusinessObjectMetadata.class);

                            if (metadata != null &&
                                    !metadata.getCategories().isEmpty()) {

                                category = metadata.getCategories()
                                        .get(0)
                                        .getName();
                            }
                        }

                        result.append(index)
                                .append(". ")
                                .append(name);

                        if (!category.isEmpty()) {
                            result.append(" — ")
                                    .append(category);
                        }

                        result.append(" (")
                                .append(visits)
                                .append(" раз)\n\n");

                        onComplete.run();
                    }

                    @Override
                    public void onSearchError(Error error) {
                        onComplete.run();
                    }
                }
        );
    }
}