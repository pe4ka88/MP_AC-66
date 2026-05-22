package com.example.lab_10;

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
import androidx.viewpager.widget.ViewPager;

public class FragmentAdd extends Fragment {

    private DataBase db;
    private EditText editTextAdd;
    private Button buttonAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        db = new DataBase(getActivity());
        editTextAdd = view.findViewById(R.id.editTextAdd);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(v -> {
            String description = editTextAdd.getText().toString();
            if (!description.isEmpty()) {
                db.addNote(description);
                editTextAdd.setText("");
                Toast.makeText(getActivity(), "Note added", Toast.LENGTH_SHORT).show();

                ViewPager viewPager = getActivity().findViewById(R.id.viewPager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(0);
                }
            } else {
                Toast.makeText(getActivity(), "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}