package com.example.labb5new5;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class FragmentShow extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_show, container, false);

        ListView listView = v.findViewById(R.id.listView);

        DBHelper db = new DBHelper(getContext());
        List<Note> notes = db.getAllNotes();

        NoteAdapter adapter = new NoteAdapter(getContext(), notes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Note note = notes.get(position);

            Intent intent = new Intent(getContext(), NoteDetailsActivity.class);
            intent.putExtra("title", note.description);
            intent.putExtra("text", note.fullText);

            startActivity(intent);
        });

        return v;
    }
}
