package com.example.lab4_10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class FragmentShow extends Fragment {

    private ListView lvNotes;
    private NoteAdapter adapter;
    private NotesDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        dbHelper = new NotesDbHelper(requireContext());
        lvNotes = view.findViewById(R.id.lvNotes);
        Button btnRefresh = view.findViewById(R.id.btnRefresh);

        List<Note> notes = dbHelper.getAllNotes();
        adapter = new NoteAdapter(requireContext(), notes);
        lvNotes.setAdapter(adapter);

        btnRefresh.setOnClickListener(v -> refresh());

        return view;
    }

    public void refresh() {
        if (dbHelper != null && adapter != null) {
            List<Note> notes = dbHelper.getAllNotes();
            adapter.setNotes(notes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
