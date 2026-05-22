package com.example.geo;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.geo.data.LocationDatabaseHelper;
import com.example.geo.data.LocationPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacesFragment extends Fragment {

    private LocationDatabaseHelper dbHelper;
    private ExpandableListView expandableListView;

    public PlacesFragment() {
        super(R.layout.fragment_places);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = view.findViewById(R.id.expandableListView);
        dbHelper = new LocationDatabaseHelper(requireContext());

        loadPlaces();
    }

    private void loadPlaces() {
        // 1. Получаем все посещенные точки
        List<LocationPoint> points = dbHelper.getAll();

        // 2. Группируем по class
        Map<String, List<String>> grouped = new HashMap<>();
        for (LocationPoint p : points) {
            String cls = p.osmClass != null ? p.osmClass : "Прочее";
            String name = p.name != null ? p.name : "Без имени";

            if (!grouped.containsKey(cls)) {
                grouped.put(cls, new ArrayList<>());
            }
            if (!grouped.get(cls).contains(name)) {
                grouped.get(cls).add(name);
            }
        }

        // 3. Подготовка данных для SimpleExpandableListAdapter
        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData = new ArrayList<>();

        for (String cls : grouped.keySet()) {
            Map<String, String> curGroupMap = new HashMap<>();
            curGroupMap.put("GROUP_NAME", cls);
            groupData.add(curGroupMap);

            List<Map<String, String>> children = new ArrayList<>();
            for (String name : grouped.get(cls)) {
                Map<String, String> childMap = new HashMap<>();
                childMap.put("CHILD_NAME", name);
                children.add(childMap);
            }
            childData.add(children);
        }

        ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                requireContext(),
                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{"GROUP_NAME"},
                new int[]{android.R.id.text1},
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"CHILD_NAME"},
                new int[]{android.R.id.text1}
        );

        expandableListView.setAdapter(adapter);
    }
}