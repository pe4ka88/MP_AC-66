package com.example.lab5mp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    private EditText etText;
    private Button btnAdd;
    private DBHelper dbHelper;

    public FragmentAdd() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add, container, false);

        etText = v.findViewById(R.id.etText);
        btnAdd = v.findViewById(R.id.btnAdd);
        dbHelper = new DBHelper(getContext());

        btnAdd.setOnClickListener(view -> {
            String text = etText.getText().toString().trim();

            if (!text.isEmpty()) {
                dbHelper.addNote(text);
                Toast.makeText(getContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
                etText.setText("");
            } else {
                Toast.makeText(getContext(), "Введите текст", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
