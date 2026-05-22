package com.example.lab4_10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {

    private EditText etNoteId;
    private NotesDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        dbHelper = new NotesDbHelper(requireContext());
        etNoteId = view.findViewById(R.id.etNoteId);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(v -> {
            String idStr = etNoteId.getText().toString().trim();
            if (idStr.isEmpty()) {
                Toast.makeText(requireContext(), "Введите номер заметки", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);
            Note note = dbHelper.getNoteById(id);
            if (note == null) {
                Toast.makeText(requireContext(), "Заметка с id " + id + " не найдена", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.deleteNote(id);
            etNoteId.setText("");
            Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
