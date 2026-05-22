package com.example.fivelab10;

import android.content.Context;
import android.content.Intent;
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

    private DBHelper dbHelper;
    private NoteActionListener noteActionListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DBHelper(context);
        if (context instanceof NoteActionListener) {
            noteActionListener = (NoteActionListener) context;
        }
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

        EditText editTextId = view.findViewById(R.id.editTextUpdateId);
        EditText editTextDescription = view.findViewById(R.id.editTextUpdateDescription);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);
        Button buttonTaskInfo = view.findViewById(R.id.buttonTaskInfoUpdate);

        buttonTaskInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TaskInfoActivity.class);
            startActivity(intent);
        });

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        buttonUpdate.setOnClickListener(v -> {
            String idText = editTextId.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (TextUtils.isEmpty(idText) || TextUtils.isEmpty(description)) {
                Toast.makeText(requireContext(), R.string.msg_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                Toast.makeText(requireContext(), R.string.msg_invalid_id, Toast.LENGTH_SHORT).show();
                return;
            }

            int updated = dbHelper.updateNote(id, description);
            if (updated > 0) {
                editTextId.setText("");
                editTextDescription.setText("");
                Toast.makeText(requireContext(), R.string.msg_updated, Toast.LENGTH_SHORT).show();
                if (noteActionListener != null) {
                    noteActionListener.onNotesChanged();
                }
            } else {
                Toast.makeText(requireContext(), R.string.msg_not_found, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
