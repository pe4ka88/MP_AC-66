package com.example.a5lab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.a5lab.DatabaseHelper;
import com.example.a5lab.R;

public class FragmentUpdate extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText etId, etNewDescription;
    private Button btnUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        dbHelper = new DatabaseHelper(getContext());
        etId = view.findViewById(R.id.et_update_id);
        etNewDescription = view.findViewById(R.id.et_new_description);
        btnUpdate = view.findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(v -> {
            String idStr = etId.getText().toString().trim();
            String newDesc = etNewDescription.getText().toString().trim();

            if (idStr.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);
            if (!dbHelper.isNoteExists(id)) {
                Toast.makeText(getContext(), "Заметка с номером " + id + " не найдена", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.updateNote(id, newDesc)) {
                Toast.makeText(getContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
                etId.setText("");
                etNewDescription.setText("");
            } else {
                Toast.makeText(getContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}