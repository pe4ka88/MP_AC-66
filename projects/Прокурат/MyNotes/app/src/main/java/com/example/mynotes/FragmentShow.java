package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class FragmentShow extends Fragment {
    private ListView listViewNotes;
    private DatabaseHelper databaseHelper;
    private NoteAdapter noteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        listViewNotes = view.findViewById(R.id.list_view_notes);
        databaseHelper = DatabaseHelper.getInstance(getContext());

        loadNotes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        ArrayList<Note> notesList = databaseHelper.getAllNotes();
        noteAdapter = new NoteAdapter(getLayoutInflater(), notesList);
        listViewNotes.setAdapter(noteAdapter);
    }
}