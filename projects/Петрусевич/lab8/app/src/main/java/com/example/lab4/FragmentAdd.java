package com.example.lab4;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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

public class FragmentAdd extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        EditText etDesc = view.findViewById(R.id.etAddDesc);
        Button btnAdd = view.findViewById(R.id.btnAddNote);

        btnAdd.setOnClickListener(v -> {
            String desc = etDesc.getText().toString();
            if (!desc.isEmpty()) {
                NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(NotesDatabaseHelper.COLUMN_DESC, desc);
                db.insert(NotesDatabaseHelper.TABLE_NAME, null, values);
                etDesc.setText("");
                Toast.makeText(getContext(), "Добавлено", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
