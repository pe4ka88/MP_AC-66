package com.example.myapplication8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Список всех зафиксированных локаций с датой, временем и координатами.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView     tvEmpty;
    private HistoryAdapter adapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvHistory);
        tvEmpty      = view.findViewById(R.id.tvHistoryEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Обновляем при возврате на вкладку
    }

    private void loadData() {
        List<LocationEntry> entries = DatabaseHelper.getInstance(requireContext())
                .getAllLocations();

        // Показываем в обратном порядке (последние сверху)
        java.util.Collections.reverse(entries);

        if (entries.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new HistoryAdapter(entries);
            recyclerView.setAdapter(adapter);
        }
    }

    // ══════════════════════════════════════════════════════
    //  RecyclerView Adapter
    // ══════════════════════════════════════════════════════

    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

        private final List<LocationEntry> data;
        private final SimpleDateFormat sdfDate =
                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        private final SimpleDateFormat sdfTime =
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        HistoryAdapter(List<LocationEntry> data) { this.data = data; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            LocationEntry e = data.get(position);
            int num = data.size() - position; // Нумерация с 1

            // Порядковый номер и дата
            h.tvNumber.setText(String.valueOf(num));
            h.tvDate.setText(sdfDate.format(new Date(e.getTimestamp())));
            h.tvTime.setText(sdfTime.format(new Date(e.getTimestamp())));

            // Название места
            String place = e.getPlaceName();
            if (place != null && !place.isEmpty()) {
                h.tvPlace.setText(place);
                h.tvPlace.setVisibility(View.VISIBLE);
            } else {
                h.tvPlace.setVisibility(View.GONE);
            }

            // Координаты
            h.tvCoords.setText(String.format(Locale.getDefault(),
                    "%.5f°N, %.5f°E", e.getLatitude(), e.getLongitude()));

            // Точность
            h.tvAccuracy.setText(String.format(Locale.getDefault(),
                    "±%.0f м", e.getAccuracy()));
        }

        @Override
        public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvNumber, tvDate, tvTime, tvPlace, tvCoords, tvAccuracy;
            VH(View v) {
                super(v);
                tvNumber   = v.findViewById(R.id.tvItemNumber);
                tvDate     = v.findViewById(R.id.tvItemDate);
                tvTime     = v.findViewById(R.id.tvItemTime);
                tvPlace    = v.findViewById(R.id.tvItemPlace);
                tvCoords   = v.findViewById(R.id.tvItemCoords);
                tvAccuracy = v.findViewById(R.id.tvItemAccuracy);
            }
        }
    }
}