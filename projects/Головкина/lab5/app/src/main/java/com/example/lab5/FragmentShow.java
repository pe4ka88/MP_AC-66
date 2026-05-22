package com.example.lab5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class FragmentShow extends Fragment {

    private ListView listViewNotes;
    private TextView tvEmptyNotes;
    private NoteAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        listViewNotes = view.findViewById(R.id.listViewNotes);
        tvEmptyNotes = view.findViewById(R.id.tvEmptyNotes);

        dbHelper = new DatabaseHelper(getContext());

        loadNotes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    public void refreshNotes() {
        loadNotes();
    }

    private void loadNotes() {
        List<Note> notes = dbHelper.getAllNotes();
        Log.d("FragmentShow", "Загружено заметок: " + notes.size());

        if (getActivity() == null) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (notes.isEmpty()) {
                    tvEmptyNotes.setVisibility(View.VISIBLE);
                    listViewNotes.setVisibility(View.GONE);
                } else {
                    tvEmptyNotes.setVisibility(View.GONE);
                    listViewNotes.setVisibility(View.VISIBLE);

                    if (adapter == null) {
                        adapter = new NoteAdapter(notes, LayoutInflater.from(getContext()));
                        listViewNotes.setAdapter(adapter);
                    } else {
                        adapter.updateData(notes);
                    }
                }
            }
        });
    }
}