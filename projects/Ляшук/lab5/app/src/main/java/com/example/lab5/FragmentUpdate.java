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

public class FragmentUpdate extends Fragment {

    private EditText editTextId, editTextDescription;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        editTextId = view.findViewById(R.id.editTextUpdateId);
        editTextDescription = view.findViewById(R.id.editTextUpdateDescription);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        dbHelper = new DatabaseHelper(getContext());

        btnUpdate.setOnClickListener(v -> {
            String idStr = editTextId.getText().toString();
            String description = editTextDescription.getText().toString();
            if (!idStr.isEmpty() && !description.isEmpty()) {
                int id = Integer.parseInt(idStr);
                dbHelper.updateNote(id, description);
                editTextId.setText("");
                editTextDescription.setText("");
                Toast.makeText(getContext(), "Note updated by [Ваша Фамилия]", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter ID and description", Toast.LENGTH_SHORT).show();
            }
        });

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
