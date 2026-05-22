package com.example.lab4;

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

public class FragmentDel extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        EditText etId = view.findViewById(R.id.etDelId);
        Button btnDel = view.findViewById(R.id.btnDelNote);

        btnDel.setOnClickListener(v -> {
            String id = etId.getText().toString();
            if (!id.isEmpty()) {
                NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int deleted = db.delete(NotesDatabaseHelper.TABLE_NAME, NotesDatabaseHelper.COLUMN_ID + "=?", new String[]{id});
                if (deleted > 0) {
                    Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                    etId.setText("");
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
