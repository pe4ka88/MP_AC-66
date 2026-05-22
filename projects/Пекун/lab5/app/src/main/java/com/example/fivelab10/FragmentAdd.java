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

public class FragmentAdd extends Fragment {

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
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        Button buttonTaskInfo = view.findViewById(R.id.buttonTaskInfoAdd);

        buttonTaskInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TaskInfoActivity.class);
            startActivity(intent);
        });

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        buttonAdd.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();

            if (TextUtils.isEmpty(description)) {
                Toast.makeText(requireContext(), R.string.msg_empty_description, Toast.LENGTH_SHORT).show();
                return;
            }

            long newId = dbHelper.addNote(description);
            if (newId == -1) {
                Toast.makeText(requireContext(), R.string.msg_add_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            editTextDescription.setText("");
            Toast.makeText(requireContext(), R.string.msg_added, Toast.LENGTH_SHORT).show();
            if (noteActionListener != null) {
                noteActionListener.onNotesChanged();
            }
        });
    }
}
