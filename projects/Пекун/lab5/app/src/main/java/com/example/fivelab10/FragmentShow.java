package com.example.fivelab10;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentShow extends Fragment {

    private DBHelper dbHelper;
    private CustomAdapter customAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DBHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listViewNotes = view.findViewById(R.id.listViewNotes);
        Button buttonTaskInfo = view.findViewById(R.id.buttonTaskInfoShow);

        buttonTaskInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TaskInfoActivity.class);
            startActivity(intent);
        });

        customAdapter = new CustomAdapter(requireContext(), new ArrayList<>());
        listViewNotes.setAdapter(customAdapter);
        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();
        refreshNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotes();
    }

    public void refreshNotes() {
        if (customAdapter != null && dbHelper != null) {
            customAdapter.updateData(dbHelper.getAllNotes());
        }
    }
}
