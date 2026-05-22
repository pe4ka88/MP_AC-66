package com.example.lr5;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    private static NotesDbHelper dbHelperStatic;
    private NotesDbHelper dbHelper;

    public static FragmentAdd newInstance(NotesDbHelper dbHelper) {
        dbHelperStatic = dbHelper;
        return new FragmentAdd();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = dbHelperStatic;

        EditText etDesc = view.findViewById(R.id.etAddDesc);
        Button btnAdd = view.findViewById(R.id.btnAddNote);

        btnAdd.setOnClickListener(v -> {
            String text = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(text)) {
                Toast.makeText(requireContext(), "Введите описание заметки", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelper.addNote(text + " | Добавил Занько Я.С., АС-66");
            if (id != -1) {
                Toast.makeText(requireContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
                etDesc.setText("");
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).refreshShowFragment();
                    ((MainActivity) getActivity()).openShowTab();
                }
            } else {
                Toast.makeText(requireContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
