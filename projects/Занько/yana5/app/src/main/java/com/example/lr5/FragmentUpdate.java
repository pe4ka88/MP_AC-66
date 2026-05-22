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

public class FragmentUpdate extends Fragment {

    private static NotesDbHelper dbHelperStatic;
    private NotesDbHelper dbHelper;

    public static FragmentUpdate newInstance(NotesDbHelper dbHelper) {
        dbHelperStatic = dbHelper;
        return new FragmentUpdate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = dbHelperStatic;

        EditText etId = view.findViewById(R.id.etUpdateId);
        EditText etDesc = view.findViewById(R.id.etUpdateDesc);
        Button btnUpdate = view.findViewById(R.id.btnUpdateNote);

        btnUpdate.setOnClickListener(v -> {
            String textId = etId.getText().toString().trim();
            String newText = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(textId) || TextUtils.isEmpty(newText)) {
                Toast.makeText(requireContext(), "Введите номер и новое описание", Toast.LENGTH_SHORT).show();
                return;
            }

            int updated = dbHelper.updateNote(
                    Integer.parseInt(textId),
                    newText + " | Обновил Занько Я.С., АС-66"
            );

            if (updated > 0) {
                Toast.makeText(requireContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
                etId.setText("");
                etDesc.setText("");
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
