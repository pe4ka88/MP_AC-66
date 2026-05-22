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

public class FragmentUpdate extends Fragment {

    private EditText etUpdateId;
    private EditText etUpdateDescription;
    private NotesDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        dbHelper = new NotesDbHelper(requireContext());
        etUpdateId = view.findViewById(R.id.etUpdateId);
        etUpdateDescription = view.findViewById(R.id.etUpdateDescription);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(v -> {
            String idStr = etUpdateId.getText().toString().trim();
            String desc = etUpdateDescription.getText().toString().trim();
            if (idStr.isEmpty() || desc.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);
            Note note = dbHelper.getNoteById(id);
            if (note == null) {
                Toast.makeText(requireContext(), "Заметка с id " + id + " не найдена", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.updateNote(id, desc);
            etUpdateId.setText("");
            etUpdateDescription.setText("");
            Toast.makeText(requireContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
