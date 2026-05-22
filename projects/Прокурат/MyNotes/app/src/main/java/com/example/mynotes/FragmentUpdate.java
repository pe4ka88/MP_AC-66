package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private EditText etNoteId, etNewDescription;
    private Button btnUpdate;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        etNoteId = view.findViewById(R.id.et_update_id);
        etNewDescription = view.findViewById(R.id.et_new_description);
        btnUpdate = view.findViewById(R.id.btn_update);
        databaseHelper = DatabaseHelper.getInstance(getContext());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = etNoteId.getText().toString().trim();
                String newDescription = etNewDescription.getText().toString().trim();

                if (!idString.isEmpty() && !newDescription.isEmpty()) {
                    int id = Integer.parseInt(idString);
                    databaseHelper.updateNote(id, newDescription);
                    etNoteId.setText("");
                    etNewDescription.setText("");
                } else {
                    if (idString.isEmpty()) {
                        etNoteId.setError("Введите ID заметки");
                    }
                    if (newDescription.isEmpty()) {
                        etNewDescription.setError("Введите новое описание");
                    }
                }
            }
        });

        return view;
    }
}