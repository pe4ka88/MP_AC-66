package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private EditText etNoteDescription;
    private Button btnAdd;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        etNoteDescription = view.findViewById(R.id.et_note_description);
        btnAdd = view.findViewById(R.id.btn_add);
        databaseHelper = DatabaseHelper.getInstance(getContext());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etNoteDescription.getText().toString().trim();
                if (!description.isEmpty()) {
                    databaseHelper.addNote(description);
                    etNoteDescription.setText("");
                } else {
                    etNoteDescription.setError("Введите описание заметки");
                }
            }
        });

        return view;
    }
}