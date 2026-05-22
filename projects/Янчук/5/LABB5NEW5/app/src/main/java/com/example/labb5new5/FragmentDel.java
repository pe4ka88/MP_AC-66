package com.example.labb5new5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
public class FragmentDel extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_del, container, false);
        EditText et = v.findViewById(R.id.etId);
        Button btn = v.findViewById(R.id.btnDel);
        btn.setOnClickListener(view -> {
            int id = Integer.parseInt(et.getText().toString());
            new DBHelper(getContext()).deleteNote(id);
            et.setText("");
        });
        return v;
    }
}

