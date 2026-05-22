package com.example.lab5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    private EditText editTextDescription;
    private Button btnAdd;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        btnAdd = view.findViewById(R.id.btnAdd);
        dbHelper = new DatabaseHelper(getContext());

        btnAdd.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString();
            if (!description.isEmpty()) {
                dbHelper.addNote(description);
                editTextDescription.setText("");
                Toast.makeText(getContext(), "Note added by Ляшук В.И.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter description", Toast.LENGTH_SHORT).show();
            }
        });

        // Дополнительный признак авторства: диалог с задачей
        view.setOnLongClickListener(v -> {
            showTaskInfo();
            return true;
        });

        return view;
    }

    private void showTaskInfo() {
        new AlertDialog.Builder(getContext())
                .setTitle("Информация о работе")
                .setMessage(getString(R.string.task_description))
                .setPositiveButton("OK", null)
                .show();
    }
}
