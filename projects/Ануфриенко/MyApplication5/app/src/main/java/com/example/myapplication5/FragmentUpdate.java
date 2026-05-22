package com.example.myapplication5;

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

import com.example.myapplication5.R;
import com.example.myapplication5.DB;

public class FragmentUpdate extends Fragment {

    private DB       db;
    private EditText etId;
    private EditText etNewDescription;
    private Button   btnUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        etId             = view.findViewById(R.id.etUpdateId);
        etNewDescription = view.findViewById(R.id.etNewDescription);
        btnUpdate        = view.findViewById(R.id.btnUpdate);

        db = new DB(requireContext());
        db.open();

        btnUpdate.setOnClickListener(v -> {
            String idStr = etId.getText().toString().trim();
            String desc  = etNewDescription.getText().toString().trim();

            if (idStr.isEmpty() || desc.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idStr);
            boolean updated = db.updateNote(id, desc);

            etId.setText("");
            etNewDescription.setText("");

            if (updated) {
                Toast.makeText(requireContext(),
                        "Заметка №" + id + " обновлена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Заметка №" + id + " не найдена", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) db.close();
    }
}