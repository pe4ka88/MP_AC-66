package com.example.lab_10;

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

    private DataBase db;
    private EditText editTextDel;
    private Button buttonDel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        db = new DataBase(getActivity());
        editTextDel = view.findViewById(R.id.editTextDel);
        buttonDel = view.findViewById(R.id.buttonDel);

        buttonDel.setOnClickListener(v -> {
            int noteId = Integer.parseInt(editTextDel.getText().toString());
            db.deleteNoteById(noteId);
            Toast.makeText(getActivity(), "Note deleted", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}