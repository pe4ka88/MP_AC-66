package com.example.a5lab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.a5lab.DatabaseHelper;
import com.example.a5lab.R;

public class FragmentAdd extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText etDescription;
    private Button btnAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        dbHelper = new DatabaseHelper(getContext());
        etDescription = view.findViewById(R.id.et_note_description);
        btnAdd = view.findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(v -> {
            String description = etDescription.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Введите описание заметки", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.addNote(description)) {
                Toast.makeText(getContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
                etDescription.setText("");
            } else {
                Toast.makeText(getContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}