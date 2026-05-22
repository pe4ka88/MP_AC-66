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

public class FragmentDel extends Fragment {

    private EditText etId;
    private Button btnDelete;
    private TextView tvResult;
    private DatabaseHelper dbHelper;
    private OnDatabaseChangedListener listener;

    public void setListener(OnDatabaseChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        etId = view.findViewById(R.id.etNoteId);
        btnDelete = view.findViewById(R.id.btnDeleteNote);
        tvResult = view.findViewById(R.id.tvDeleteResult);

        dbHelper = new DatabaseHelper(getContext());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = etId.getText().toString().trim();

                if (idStr.isEmpty()) {
                    Toast.makeText(getContext(), "Введите ID заметки", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);

                    if (!dbHelper.noteExists(id)) {
                        tvResult.setText(" Заметка с ID " + id + " не существует");
                        return;
                    }

                    int result = dbHelper.deleteNote(id);

                    if (result > 0) {
                        tvResult.setText(" Заметка с ID " + id + " удалена");
                        etId.setText("");

                        // Уведомляем слушателя об изменении
                        if (listener != null) {
                            listener.onDatabaseChanged();
                        }
                    } else {
                        tvResult.setText(" Ошибка при удалении");
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Некорректный ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}