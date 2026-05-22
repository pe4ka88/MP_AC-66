package com.example.lr5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class FragmentShow extends Fragment {

    private static NotesDbHelper dbHelperStatic;
    private NotesDbHelper dbHelper;
    private NotesAdapter adapter;
    private TextView tvCount;

    public static FragmentShow newInstance(NotesDbHelper dbHelper) {
        dbHelperStatic = dbHelper;
        return new FragmentShow();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = dbHelperStatic;

        ListView listView = view.findViewById(R.id.listViewNotes);
        tvCount = view.findViewById(R.id.tvCount);

        adapter = new NotesAdapter(requireContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        reloadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadNotes();
    }

    public void reloadNotes() {
        if (getContext() == null || dbHelper == null || adapter == null) return;
        adapter.setNotes(dbHelper.getAllNotes());
        int count = adapter.getCount();
        if (tvCount != null) {
            tvCount.setText("Количество заметок: " + count + " | Автор: Занько Я.С. | Группа: АС-66");
        }
    }
}
