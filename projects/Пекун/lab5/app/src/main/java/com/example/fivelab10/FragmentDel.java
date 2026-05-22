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

public class FragmentDel extends Fragment {

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
        return inflater.inflate(R.layout.fragment_del, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTextId = view.findViewById(R.id.editTextDeleteId);
        Button buttonDel = view.findViewById(R.id.buttonDel);
        Button buttonTaskInfo = view.findViewById(R.id.buttonTaskInfoDel);

        buttonTaskInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TaskInfoActivity.class);
            startActivity(intent);
        });

        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();

        buttonDel.setOnClickListener(v -> {
            String idText = editTextId.getText().toString().trim();
            if (TextUtils.isEmpty(idText)) {
                Toast.makeText(requireContext(), R.string.msg_empty_id, Toast.LENGTH_SHORT).show();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                Toast.makeText(requireContext(), R.string.msg_invalid_id, Toast.LENGTH_SHORT).show();
                return;
            }

            int deleted = dbHelper.deleteNoteById(id);
            if (deleted > 0) {
                editTextId.setText("");
                Toast.makeText(requireContext(), R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                if (noteActionListener != null) {
                    noteActionListener.onNotesChanged();
                }
            } else {
                Toast.makeText(requireContext(), R.string.msg_not_found, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
