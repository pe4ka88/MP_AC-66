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

public class FragmentDel extends Fragment {

    private EditText editTextId;
    private Button btnDel;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        editTextId = view.findViewById(R.id.editTextId);
        btnDel = view.findViewById(R.id.btnDel);
        dbHelper = new DatabaseHelper(getContext());

        btnDel.setOnClickListener(v -> {
            String idStr = editTextId.getText().toString();
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                dbHelper.deleteNote(id);
                editTextId.setText("");
                Toast.makeText(getContext(), "Note deleted by Ляшук В.И.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter ID", Toast.LENGTH_SHORT).show();
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
