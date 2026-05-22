package com.example.mynotes; // Замени на свой пакет!

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        dbHelper = new DBHelper(getActivity());

        EditText etIdUpdate = view.findViewById(R.id.etIdUpdate);
        EditText etDescUpdate = view.findViewById(R.id.etDescUpdate);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idText = etIdUpdate.getText().toString();
                String newDesc = etDescUpdate.getText().toString();

                if (!idText.isEmpty() && !newDesc.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idText);
                        dbHelper.updateNote(id, newDesc);
                        Toast.makeText(getActivity(), "Обновлено! (Лысюк Р.)", Toast.LENGTH_SHORT).show();
                        etIdUpdate.setText("");
                        etDescUpdate.setText("");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "ID должен быть числом!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Заполните оба поля!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
