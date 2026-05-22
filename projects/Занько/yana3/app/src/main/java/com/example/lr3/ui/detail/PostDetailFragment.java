package com.example.lr3.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.lr3.databinding.FragmentPostDetailBinding;
import com.example.lr3.model.Post;

public class PostDetailFragment extends Fragment {

    private FragmentPostDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Post post = null;
        if (getArguments() != null) {
            Object obj = getArguments().getSerializable("post");
            if (obj instanceof Post) post = (Post) obj;
        }

        if (post != null) {
            binding.tvDetailId.setText("ID: " + post.getId() + " • userId: " + post.getUserId());
            binding.tvDetailTitle.setText(post.getTitle());
            binding.tvDetailBody.setText(post.getBody());
        } else {
            binding.tvDetailTitle.setText("Нет данных");
            binding.tvDetailBody.setText("Элемент не был передан.");
        }

        // Явная кнопка назад (плюс будет Up-кнопка в тулбаре)
        binding.btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
