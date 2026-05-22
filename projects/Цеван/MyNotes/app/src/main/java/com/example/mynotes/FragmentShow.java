package com.example.mynotes; // Твой пакет

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.List;

public class FragmentShow extends Fragment {
    private ListView listView;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listView);
        dbHelper = new DBHelper(getActivity());
        updateList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList(); // Обновляем список, когда возвращаемся на вкладку
    }

    public void updateList() {
        if (dbHelper != null && getActivity() != null) {
            List<Note> notes = dbHelper.getAllNotes();
            NoteAdapter adapter = new NoteAdapter(getActivity(), notes);
            listView.setAdapter(adapter);
        }
    }
}
