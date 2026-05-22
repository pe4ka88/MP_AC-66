package com.example.note.fragments;

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

import com.example.note.R;
import com.example.note.db.NotesDBHelper;

public class FragmentDel extends Fragment {

    private EditText etId;
    private Button btnDel;

    public FragmentDel() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_del, container, false);

        etId = view.findViewById(R.id.editTextId);
        btnDel = view.findViewById(R.id.buttonDel);

        btnDel.setOnClickListener(v -> {

            if(etId.getText().toString().isEmpty()){
                Toast.makeText(getContext(),
                        "Введите номер заметки", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(etId.getText().toString());

            NotesDBHelper db = new NotesDBHelper(requireContext());
            db.deleteNote(id);

            etId.setText("");
            Toast.makeText(getContext(),
                    "Заметка удалена", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
