package com.example.lab7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab7.data.AppDatabase;
import com.example.lab7.data.HistoryEntry;
import com.example.lab7.data.HistoryLogger;
import com.example.lab7.ui.UiHelpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final List<HistoryEntry> entries = new ArrayList<>();
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(entries);
        recyclerView.setAdapter(adapter);

        UiHelpers.bindBack(this);
        UiHelpers.bindAuthorButton(this);

        findViewById(R.id.btnHistoryRefresh).setOnClickListener(v -> loadHistory());
        findViewById(R.id.btnHistoryClearHint).setOnClickListener(v -> {
            HistoryLogger.log(this, "HISTORY", "OPENED");
            loadHistory();
        });
        findViewById(R.id.btnHistoryTop).setOnClickListener(v -> {
            recyclerView.smoothScrollToPosition(0);
            HistoryLogger.log(this, "HISTORY", "SCROLL_TOP");
        });
        findViewById(R.id.btnHistoryBottom).setOnClickListener(v -> {
            if (!entries.isEmpty()) {
                recyclerView.smoothScrollToPosition(entries.size() - 1);
            }
            HistoryLogger.log(this, "HISTORY", "SCROLL_BOTTOM");
        });
        findViewById(R.id.btnHistoryZoomIn).setOnClickListener(v -> {
            adapter.updateScale(+1f);
            HistoryLogger.log(this, "HISTORY", "ZOOM_IN");
        });
        findViewById(R.id.btnHistoryZoomOut).setOnClickListener(v -> {
            adapter.updateScale(-1f);
            HistoryLogger.log(this, "HISTORY", "ZOOM_OUT");
        });

        loadHistory();
    }

    private void loadHistory() {
        dbExecutor.execute(() -> {
            List<HistoryEntry> data = AppDatabase.getInstance(this).historyDao().getAll();
            runOnUiThread(() -> {
                entries.clear();
                entries.addAll(data);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdown();
    }

    private static final class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {

        private final List<HistoryEntry> items;
        private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        private float scaleSp = 14f;

        private HistoryAdapter(List<HistoryEntry> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            HistoryEntry entry = items.get(position);
            holder.module.setText(entry.module);
            holder.action.setText(entry.action);
            holder.time.setText(formatter.format(new Date(entry.timestamp)));

            holder.module.setTextSize(scaleSp + 1f);
            holder.action.setTextSize(scaleSp);
            holder.time.setTextSize(Math.max(11f, scaleSp - 2f));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        void updateScale(float delta) {
            scaleSp = Math.max(12f, Math.min(20f, scaleSp + delta));
            notifyDataSetChanged();
        }

        private static final class Holder extends RecyclerView.ViewHolder {
            final TextView module;
            final TextView action;
            final TextView time;

            Holder(@NonNull View itemView) {
                super(itemView);
                module = itemView.findViewById(R.id.textHistoryModule);
                action = itemView.findViewById(R.id.textHistoryAction);
                time = itemView.findViewById(R.id.textHistoryTime);
            }
        }
    }
}
