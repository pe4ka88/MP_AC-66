package com.example.a5lab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.example.a5lab.DatabaseHelper;
import com.example.a5lab.NoteAdapter;
import com.example.a5lab.R;
import com.example.a5lab.model.Note;
import java.util.List;

public class FragmentShow extends Fragment {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private NoteAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        dbHelper = new DatabaseHelper(getContext());
        listView = view.findViewById(R.id.list_notes);

        loadNotes();
        return view;
    }

    private void loadNotes() {
        List<Note> notes = dbHelper.getAllNotes();
        if (notes.size() < 20) {
            // База данных считается отсутствующей, но по условию у нас уже 20 записей
            android.widget.Toast.makeText(getContext(), "База данных содержит менее 20 записей!", android.widget.Toast.LENGTH_SHORT).show();
        }
        adapter = new NoteAdapter(getContext(), notes);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes(); // обновляем при возврате
    }
}