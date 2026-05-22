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

public class FragmentDel extends Fragment {

    private static NotesDbHelper dbHelperStatic;
    private NotesDbHelper dbHelper;

    public static FragmentDel newInstance(NotesDbHelper dbHelper) {
        dbHelperStatic = dbHelper;
        return new FragmentDel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_del, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = dbHelperStatic;

        EditText etId = view.findViewById(R.id.etDeleteId);
        Button btnDel = view.findViewById(R.id.btnDeleteNote);

        btnDel.setOnClickListener(v -> {
            String textId = etId.getText().toString().trim();

            if (TextUtils.isEmpty(textId)) {
                Toast.makeText(requireContext(), "Введите номер заметки", Toast.LENGTH_SHORT).show();
                return;
            }

            int deleted = dbHelper.deleteNoteById(Integer.parseInt(textId));
            if (deleted > 0) {
                Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                etId.setText("");
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).refreshShowFragment();
                    ((MainActivity) getActivity()).openShowTab();
                }
            } else {
                Toast.makeText(requireContext(), "Запись с таким номером не найдена", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
