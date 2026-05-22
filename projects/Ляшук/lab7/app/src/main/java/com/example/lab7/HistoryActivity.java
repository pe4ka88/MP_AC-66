package com.example.lab7;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DatabaseHelper(this);
        dbHelper.addRecord("Просмотрена история (Ляшук В.И.)");

        ListView listView = findViewById(R.id.historyListView);
        Button btnBack = findViewById(R.id.btnBackHistory);

        List<String> history = dbHelper.getAllHistory();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);
        listView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }
}
