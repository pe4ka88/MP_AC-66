package com.example.lab5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    private EditText etDescription;
    private Button btnAdd;
    private TextView tvResult;
    private DatabaseHelper dbHelper;
    private OnDatabaseChangedListener listener;

    public void setListener(OnDatabaseChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        etDescription = view.findViewById(R.id.etNoteDescription);
        btnAdd = view.findViewById(R.id.btnAddNote);
        tvResult = view.findViewById(R.id.tvAddResult);

        dbHelper = new DatabaseHelper(getContext());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString().trim();

                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Введите описание заметки", Toast.LENGTH_SHORT).show();
                    return;
                }

                long id = dbHelper.addNote(description + " (добавлено Кацевич А. Ю.)");

                if (id != -1) {
                    tvResult.setText("✅ Заметка добавлена с ID: " + id);
                    etDescription.setText("");

                    // Уведомляем слушателя об изменении
                    if (listener != null) {
                        listener.onDatabaseChanged();
                    }

                    // Проверка количества заметок
                    int count = dbHelper.getNotesCount();
                    if (count >= 20) {
                        tvResult.append("\n Всего заметок: " + count);
                    }
                } else {
                    tvResult.setText(" Ошибка при добавлении");
                }
            }
        });

        return view;
    }
}