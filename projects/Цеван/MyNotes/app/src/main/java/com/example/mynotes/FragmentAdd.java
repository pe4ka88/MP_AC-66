package com.example.mynotes; // Замени на свой пакет!

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        dbHelper = new DBHelper(getActivity());

        EditText etDesc = view.findViewById(R.id.etDesc);
        Button btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etDesc.getText().toString();
                if (!text.isEmpty()) {
                    dbHelper.addNote(text);
                    Toast.makeText(getActivity(), "Добавлено! (Цеван К.)", Toast.LENGTH_SHORT).show();
                    etDesc.setText(""); // очищаем поле
                } else {
                    Toast.makeText(getActivity(), "Введите текст!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
