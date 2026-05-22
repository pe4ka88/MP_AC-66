package com.example.labb5new5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update, container, false);
        EditText etId = v.findViewById(R.id.etId);
        EditText etDesc = v.findViewById(R.id.etDesc);
        Button btn = v.findViewById(R.id.btnUpdate);
        btn.setOnClickListener(view -> {
            int id = Integer.parseInt(etId.getText().toString());
            new DBHelper(getContext()).updateNote(id, etDesc.getText().toString());
            etId.setText("");
            etDesc.setText("");
        });
        return v;
    }
}

