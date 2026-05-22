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

public class FragmentDel extends Fragment {

    private DB       db;
    private EditText etId;
    private Button   btnDel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        etId   = view.findViewById(R.id.etDelId);
        btnDel = view.findViewById(R.id.btnDel);

        db = new DB(requireContext());
        db.open();

        btnDel.setOnClickListener(v -> {
            String idStr = etId.getText().toString().trim();
            if (idStr.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Введите номер заметки", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);
            boolean deleted = db.deleteNote(id);
            etId.setText("");

            if (deleted) {
                Toast.makeText(requireContext(),
                        "Заметка №" + id + " удалена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Заметка №" + id + " не найдена", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) db.close();
    }
}