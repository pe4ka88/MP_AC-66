package com.example.lab5mp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentDel extends Fragment {

    private EditText etIdDel;
    private Button btnDel;
    private DBHelper dbHelper;

    public FragmentDel() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_del, container, false);

        etIdDel = v.findViewById(R.id.etIdDel);
        btnDel = v.findViewById(R.id.btnDel);
        dbHelper = new DBHelper(getContext());

        btnDel.setOnClickListener(view -> {
            String sId = etIdDel.getText().toString().trim();

            if (!sId.isEmpty()) {
                long id = Long.parseLong(sId);
                int rows = dbHelper.deleteNote(id);

                if (rows > 0) {
                    Toast.makeText(getContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }

                etIdDel.setText("");

            } else {
                Toast.makeText(getContext(), "Введите ID", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
