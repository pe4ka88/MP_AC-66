package com.example.myapplication3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_URL = "url";

    public static DetailFragment newInstance(Photo photo) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, photo.getTitle());
        args.putString(ARG_URL, photo.getUrl());

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView title = root.findViewById(R.id.title);
        ImageView image = root.findViewById(R.id.image);

        if (getArguments() != null) {

            String titleText = getArguments().getString(ARG_TITLE);
            String imageUrl = getArguments().getString(ARG_URL);

            if (!TextUtils.isEmpty(titleText)) {
                title.setText(titleText);
            }

            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(image);
            }
        }

        return root;
    }
}