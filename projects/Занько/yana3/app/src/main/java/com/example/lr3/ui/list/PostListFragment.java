package com.example.lr3.ui.list;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lr3.R;
import com.example.lr3.data.RetrofitClient;
import com.example.lr3.databinding.FragmentPostListBinding;
import com.example.lr3.model.Post;
import com.example.lr3.model.PostsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListFragment extends Fragment {

    private FragmentPostListBinding binding;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PostAdapter(this::openDetail);

        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);

        binding.btnLoad.setOnClickListener(v -> loadData());

        binding.btnTask.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.lab_title))
                .setMessage(getString(R.string.task_text))
                .setPositiveButton("OK", (d, w) -> d.dismiss())
                .show());
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLoad.setEnabled(!loading);
    }

    private void loadData() {
        setLoading(true);

        RetrofitClient.api().getPosts().enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getPosts() != null) {
                    List<Post> posts = response.body().getPosts();
                    adapter.submit(posts);
                    Toast.makeText(requireContext(), "Загружено: " + posts.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Ошибка ответа сервера: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostsResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openDetail(Post post) {
        NavController nav = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putSerializable("post", post);
        nav.navigate(R.id.postDetailFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
