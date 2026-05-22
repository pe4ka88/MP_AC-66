package com.example.lab_8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Details extends Fragment {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";

    public static Details newInstance(int id, String title, String description) {
        Details fragment = new Details();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView idTextView = view.findViewById(R.id.detailID);
        TextView titleTextView = view.findViewById(R.id.detailTitle);
        TextView descriptionTextView = view.findViewById(R.id.detailDescription);
        Button closeButton = view.findViewById(R.id.buttonCloseDetails);

        if (getArguments() != null) {
            idTextView.setText("ID: " + getArguments().getInt(ARG_ID));
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            descriptionTextView.setText(getArguments().getString(ARG_DESCRIPTION));
        }

        closeButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).hideDetails();
            }
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }
}
