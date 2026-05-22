package com.example.json;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {

    private static final String ARG_POST = "post";
    private Post post;

    private TextView tvId, tvUserId, tvTitle, tvBody;
    private ImageView ivImage;

    public static DetailFragment newInstance(Post post) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable(ARG_POST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        tvId = view.findViewById(R.id.tvDetailId);
        tvUserId = view.findViewById(R.id.tvDetailUserId);
        tvTitle = view.findViewById(R.id.tvDetailTitle);
        tvBody = view.findViewById(R.id.tvDetailBody);
        ivImage = view.findViewById(R.id.ivDetailImage);

        if (post != null) {
            displayPost();
        }

        return view;
    }

    private void displayPost() {
        tvId.setText("ID: " + post.getId());
        tvUserId.setText("User ID: " + post.getUserId());
        tvTitle.setText(post.getTitle());
        tvBody.setText(post.getBody());

        // Загрузка изображения, если есть
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(post.getImageUrl())
                    .centerCrop()
                    .into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }
    }
}