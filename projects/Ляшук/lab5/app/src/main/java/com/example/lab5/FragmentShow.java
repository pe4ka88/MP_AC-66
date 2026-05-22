package com.example.lab5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class FragmentShow extends Fragment {

    private ListView listView;
    private DatabaseHelper dbHelper;
    private NotesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listViewNotes);
        dbHelper = new DatabaseHelper(getContext());
        refreshList();

        // Признак авторства: вызов инфо по долгому нажатию
        view.setOnLongClickListener(v -> {
            showTaskInfo();
            return true;
        });

        return view;
    }

    private void showTaskInfo() {
        new AlertDialog.Builder(getContext())
                .setTitle("Информация о работе")
                .setMessage(getString(R.string.task_description))
                .setPositiveButton("OK", null)
                .show();
    }

    public void refreshList() {
        if (dbHelper != null && listView != null) {
            List<Note> notes = dbHelper.getAllNotes();
            adapter = new NotesAdapter(getContext(), notes);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }
}
