package com.example.lab5mp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentUpdate extends Fragment {

    private EditText etIdUpd, etTextUpd;
    private Button btnUpd;
    private DBHelper dbHelper;

    public FragmentUpdate() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_update, container, false);

        etIdUpd = v.findViewById(R.id.etIdUpd);
        etTextUpd = v.findViewById(R.id.etTextUpd);
        btnUpd = v.findViewById(R.id.btnUpd);
        dbHelper = new DBHelper(getContext());

        btnUpd.setOnClickListener(view -> {
            String sId = etIdUpd.getText().toString().trim();
            String newText = etTextUpd.getText().toString().trim();

            if (!sId.isEmpty() && !newText.isEmpty()) {
                long id = Long.parseLong(sId);
                int rows = dbHelper.updateNote(id, newText);

                if (rows > 0) {
                    Toast.makeText(getContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }

                etIdUpd.setText("");
                etTextUpd.setText("");

            } else {
                Toast.makeText(getContext(), "Введите ID и новый текст", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
