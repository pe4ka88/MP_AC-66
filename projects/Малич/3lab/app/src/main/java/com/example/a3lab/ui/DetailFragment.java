package com.example.a3lab.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.a3lab.R;

public class DetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleText = view.findViewById(R.id.detailTitle);
        TextView categoryText = view.findViewById(R.id.detailCategory);
        TextView descriptionText = view.findViewById(R.id.detailDescription);
        RatingBar ratingBar = view.findViewById(R.id.detailRating);
        TextView idText = view.findViewById(R.id.detailId);

        if (getArguments() != null) {
            String title = getArguments().getString("item_title");
            String category = getArguments().getString("item_category");
            String description = getArguments().getString("item_description");
            double rating = getArguments().getDouble("item_rating", 0);
            int id = getArguments().getInt("item_id", 0);

            titleText.setText(title != null ? title : getString(R.string.no_title));
            categoryText.setText(getString(R.string.category_label) + " " +
                    (category != null ? category : getString(R.string.no_category)));
            descriptionText.setText(description != null ? description : getString(R.string.no_description));
            ratingBar.setRating((float) rating);
            idText.setText(getString(R.string.id_label) + " " + id);
        } else {
            titleText.setText(R.string.no_data_available);
            categoryText.setText(R.string.no_category);
            descriptionText.setText(R.string.no_description);
            ratingBar.setRating(0);
            idText.setText(R.string.id_label);
        }

        return view;
    }
}