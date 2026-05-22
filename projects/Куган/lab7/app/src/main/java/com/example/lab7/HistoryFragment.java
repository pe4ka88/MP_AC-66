package com.example.lab7;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView listView;
    private Button btnClear;
    private HistoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private static final String TAG = "HistoryFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        try {
            listView = view.findViewById(R.id.listViewHistory);
            btnClear = view.findViewById(R.id.btnClearHistory);

            dbHelper = new DatabaseHelper(requireContext());

            // Загружаем историю
            loadHistory();

            btnClear.setOnClickListener(v -> {
                try {
                    dbHelper.clearHistory();
                    loadHistory();
                    Toast.makeText(getContext(), "История очищена", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Ошибка очистки истории: " + e.getMessage());
                    Toast.makeText(getContext(), "Ошибка очистки", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Ошибка создания фрагмента: " + e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    private void loadHistory() {
        try {
            List<DatabaseHelper.HistoryItem> history = dbHelper.getAllHistory();
            Log.d(TAG, "Загружено записей: " + history.size());

            if (history == null || history.isEmpty()) {
                // Создаем пустой адаптер
                List<DatabaseHelper.HistoryItem> emptyList = new ArrayList<>();
                adapter = new HistoryAdapter(emptyList, LayoutInflater.from(getContext()));
                listView.setAdapter(adapter);

                Toast.makeText(getContext(), "История пуста", Toast.LENGTH_SHORT).show();
            } else {
                // Создаем адаптер с данными
                adapter = new HistoryAdapter(history, LayoutInflater.from(getContext()));
                listView.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка загрузки истории: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка загрузки истории", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory(); // Обновляем при возврате на фрагмент
    }
}