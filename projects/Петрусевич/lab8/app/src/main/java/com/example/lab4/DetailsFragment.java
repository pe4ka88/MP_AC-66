package com.example.lab4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.concrete_item, container, false);

        TextView textViewID = view.findViewById(R.id.IDConcrete);
        TextView textViewName = view.findViewById(R.id.NameConcrete);
        TextView textViewEmail = view.findViewById(R.id.EmailConcrete);
        TextView textViewAbout = view.findViewById(R.id.AboutConcrete);
        Button returnBtn = view.findViewById(R.id.BackBtn);

        if (getArguments() != null) {
            String jsonStr = getArguments().getString("item_data");
            try {
                JSONObject temp = new JSONObject(jsonStr);
                textViewID.setText("ID of person: " + temp.getString("id"));
                textViewName.setText("Name of person: " + temp.getString("name"));
                textViewEmail.setText("Email of person: " + temp.getString("email"));
                textViewAbout.setText("About: " + temp.getString("about"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        returnBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }
}
