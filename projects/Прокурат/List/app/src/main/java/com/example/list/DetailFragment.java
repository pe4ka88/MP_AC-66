package com.example.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private static final String ARG_ITEM = "item";

    public static DetailFragment newInstance(Item item) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView tvId = view.findViewById(R.id.tv_id);
        TextView tvUserId = view.findViewById(R.id.tv_userId);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvBody = view.findViewById(R.id.tv_body);

        if (getArguments() != null) {
            @SuppressWarnings("deprecation")
            Item item = (Item) getArguments().getSerializable(ARG_ITEM);
            if (item != null) {
                tvId.setText("ID: " + item.getId());
                tvUserId.setText("User ID: " + item.getUserId());
                tvTitle.setText(item.getTitle());
                tvBody.setText(item.getBody());
            }
        }

        return view;
    }
}