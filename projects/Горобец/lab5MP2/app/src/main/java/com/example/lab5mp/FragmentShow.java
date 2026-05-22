package com.example.lab5mp;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentShow extends Fragment {

    private ListView listView;
    private NotesAdapter adapter;
    private DBHelper dbHelper;

    public FragmentShow() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_show, container, false);

        listView = v.findViewById(R.id.listNotes);
        dbHelper = new DBHelper(getContext());

        loadNotes();

        return v;
    }

    private void loadNotes() {
        Cursor c = dbHelper.getAllNotes();
        ArrayList<Note> notes = new ArrayList<>();

        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(DBHelper.COL_ID);
            int textIndex = c.getColumnIndex(DBHelper.COL_TEXT);

            do {
                long id = c.getLong(idIndex);
                String text = c.getString(textIndex);
                notes.add(new Note(id, text));
            } while (c.moveToNext());
        }

        c.close();

        adapter = new NotesAdapter(getContext(), notes);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes(); // обновление при возвращении на вкладку
    }
}
