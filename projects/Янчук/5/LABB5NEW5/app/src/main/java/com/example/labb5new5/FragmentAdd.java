package com.example.labb5new5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add, container, false);

        EditText etDesc = v.findViewById(R.id.etDesc);
        EditText etFull = v.findViewById(R.id.etFull);
        Button btn = v.findViewById(R.id.btnAdd);

        btn.setOnClickListener(view -> {

            String desc = etDesc.getText().toString().trim();
            String full = etFull.getText().toString().trim();

            if (desc.isEmpty() || full.isEmpty()) {
                return; // можно добавить Toast, если хочешь
            }

            new DBHelper(getContext()).addNote(desc, full);

            etDesc.setText("");
            etFull.setText("");
        });

        return v;
    }
}
