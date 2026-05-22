package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private EditText etNoteId;
    private Button btnDelete;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        etNoteId = view.findViewById(R.id.et_note_id);
        btnDelete = view.findViewById(R.id.btn_delete);
        databaseHelper = DatabaseHelper.getInstance(getContext());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = etNoteId.getText().toString().trim();
                if (!idString.isEmpty()) {
                    int id = Integer.parseInt(idString);
                    databaseHelper.deleteNote(id);
                    etNoteId.setText("");
                } else {
                    etNoteId.setError("Введите ID заметки");
                }
            }
        });

        return view;
    }
}