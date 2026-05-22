package com.example.myapplication5;

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

import com.example.myapplication5.R;
import com.example.myapplication5.DB;

public class FragmentAdd extends Fragment {

    private DB       db;
    private EditText etDescription;
    private Button   btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        etDescription = view.findViewById(R.id.etDescription);
        btnAdd        = view.findViewById(R.id.btnAdd);

        db = new DB(requireContext());
        db.open();

        btnAdd.setOnClickListener(v -> {
            String desc = etDescription.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Введите описание заметки", Toast.LENGTH_SHORT).show();
                return;
            }
            db.addNote(desc);
            etDescription.setText("");
            Toast.makeText(requireContext(),
                    "Заметка добавлена", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) db.close();
    }
}