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

public class FragmentUpdate extends Fragment {

    private DataBase db;
    private EditText editTextUpdateId, editTextUpdateDescription;
    private Button buttonUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        db = new DataBase(getActivity());
        editTextUpdateId = view.findViewById(R.id.editTextUpdateId);
        editTextUpdateDescription = view.findViewById(R.id.editTextUpdateDescription);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);

        buttonUpdate.setOnClickListener(v -> {
            int noteId = Integer.parseInt(editTextUpdateId.getText().toString());
            String newDescription = editTextUpdateDescription.getText().toString();
            db.updateNoteById(noteId, newDescription);
            Toast.makeText(getActivity(), "Note updated", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}