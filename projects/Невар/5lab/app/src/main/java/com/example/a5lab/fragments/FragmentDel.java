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

public class FragmentDel extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText etId;
    private Button btnDel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        dbHelper = new DatabaseHelper(getContext());
        etId = view.findViewById(R.id.et_note_id);
        btnDel = view.findViewById(R.id.btn_del);

        btnDel.setOnClickListener(v -> {
            String idStr = etId.getText().toString().trim();
            if (idStr.isEmpty()) {
                Toast.makeText(getContext(), "Введите номер заметки", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);
            if (dbHelper.deleteNote(id)) {
                Toast.makeText(getContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                etId.setText("");
            } else {
                Toast.makeText(getContext(), "Заметка с номером " + id + " не найдена", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}