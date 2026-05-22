package com.example.lab_14;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(this);
        List<MediaHistory> history = db.mediaHistoryDao().getAllHistory();
        
        HistoryAdapter adapter = new HistoryAdapter(history);
        recyclerView.setAdapter(adapter);

        Button btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> finish());
    }
}