package com.example.myapplication7;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryActivity — отображение истории операций из SQLite.
 *
 * Дополнительно:
 *   • Показывает суммарное расстояние между точками съёмки/просмотра.
 *   • Кнопка «Очистить» удаляет всю историю.
 */
public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private LocationHelper locationHelper;
    private ListView       listView;
    private TextView       tvSummary;
    private TextView       tvDistance;
    private ArrayAdapter<String> adapter;
    private List<String>   displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        db             = new DatabaseHelper(this);
        locationHelper = new LocationHelper(this);

        listView   = findViewById(R.id.lv_history);
        tvSummary  = findViewById(R.id.tv_history_summary);
        tvDistance = findViewById(R.id.tv_total_distance);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        Button btnClear = findViewById(R.id.btn_clear_history);
        btnClear.setOnClickListener(v -> clearHistory());

        Button btnBack = findViewById(R.id.btn_history_back);
        btnBack.setOnClickListener(v -> finish());

        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    // ──────────────────────── Data ────────────────────────

    private void loadHistory() {
        displayList.clear();
        List<HistoryRecord> records = db.getAllRecords();

        if (records.isEmpty()) {
            displayList.add("История пуста");
            tvSummary .setText("Записей: 0");
            tvDistance.setText("Суммарное расстояние: 0 м");
            adapter.notifyDataSetChanged();
            return;
        }

        // Подсчёт суммарного расстояния между последовательными точками
        float totalDistance = 0f;
        for (int i = 1; i < records.size(); i++) {
            HistoryRecord prev = records.get(i);      // список обратный (новые первые)
            HistoryRecord curr = records.get(i - 1);
            if (prev.latitude != 0 && curr.latitude != 0) {
                totalDistance += LocationHelper.distanceBetween(
                        prev.latitude, prev.longitude,
                        curr.latitude, curr.longitude);
            }
        }

        // Формирование строк для ListView
        for (HistoryRecord r : records) {
            String line = String.format(
                    "[%s] %s\n  %s\n  📍 %.4f°, %.4f°",
                    r.activityType, r.description,
                    r.timestamp,
                    r.latitude, r.longitude);
            displayList.add(line);
        }

        tvSummary .setText("Записей: " + records.size());
        tvDistance.setText(String.format(
                "Суммарное расстояние: %.1f м  (%.2f км)",
                totalDistance, totalDistance / 1000f));

        adapter.notifyDataSetChanged();
    }

    private void clearHistory() {
        db.clearHistory();
        loadHistory();
        Toast.makeText(this, "История очищена", Toast.LENGTH_SHORT).show();
    }
}