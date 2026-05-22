package com.example.mynotes; // Замени на свой пакет!

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        dbHelper = new DBHelper(getActivity());

        EditText etId = view.findViewById(R.id.etId);
        Button btnDel = view.findViewById(R.id.btnDel);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idText = etId.getText().toString();
                if (!idText.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idText);
                        dbHelper.deleteNote(id);
                        Toast.makeText(getActivity(), "Удалено! (Цеван К.)", Toast.LENGTH_SHORT).show();
                        etId.setText("");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "ID должен быть числом!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Введите ID!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
