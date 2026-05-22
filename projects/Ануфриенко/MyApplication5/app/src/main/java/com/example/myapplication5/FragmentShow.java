package com.example.myapplication5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication5.R;
import com.example.myapplication5.NotesListAdapter;
import com.example.myapplication5.DB;
import com.example.myapplication5.Note;

import java.util.List;

public class FragmentShow extends Fragment {

    private DB               db;
    private NotesListAdapter adapter;
    private ListView         listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.lvNotes);

        // Открываем БД и загружаем данные
        db = new DB(requireContext());
        db.open();

        List<Note> notes = db.getAllNotes();
        adapter = new NotesListAdapter(requireContext(), notes);
        listView.setAdapter(adapter);

        return view;
    }

    // Обновляем список каждый раз, когда вкладка становится видимой
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && db != null) {
            adapter.updateData(db.getAllNotes());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) db.close();
    }
}