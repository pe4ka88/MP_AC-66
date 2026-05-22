package com.example.a3lab;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.a3lab.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            Post post;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                post = getArguments().getSerializable("post", Post.class);
            } else {
                post = (Post) getArguments().getSerializable("post");
            }

            if (post != null) {
                binding.textViewDetailTitle.setText(post.getTitle());
                binding.textViewDetailId.setText("Post ID: " + post.getId());
                binding.textViewDetailBody.setText("This is a detailed description of the photo with ID " + post.getId() + ". Since the original API provides limited text, we've expanded it for the detail view.");
                
                // План Б: Используем стабильный сервис picsum.photos
                String imageUrl = "https://picsum.photos/seed/" + post.getId() + "/600/400";
                
                Glide.with(this)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.stat_notify_error)
                        .into(binding.imageViewLarge);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}