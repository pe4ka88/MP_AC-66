package com.example.json;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private ApiManager apiManager;

    // Для бонусов
    private boolean showImages = false;
    private int itemsToShow = 20; // количество элементов

    public interface OnPostSelectedListener {
        void onPostSelected(Post post);
    }

    private OnPostSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(postList, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                if (listener != null) {
                    listener.onPostSelected(post);
                }
            }
        });

        recyclerView.setAdapter(adapter);

        apiManager = new ApiManager();

        return view;
    }

    public void setListener(OnPostSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Загрузка данных с сервера
     */
    public void loadData() {
        showLoading(true);

        apiManager.fetchPosts(new ApiManager.ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                postList.clear();

                // Ограничиваем количество (бонус)
                int limit = Math.min(result.size(), itemsToShow);
                postList.addAll(result.subList(0, limit));

                adapter.setShowImages(showImages);
                adapter.notifyDataSetChanged();

                showLoading(false);

                if (postList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Нет данных для отображения");
                }
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Ошибка: " + errorMessage);
                Toast.makeText(getContext(), "Ошибка загрузки: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Загрузка с кастомного URL (бонус)
     */
    public void loadFromCustomUrl(String url) {
        showLoading(true);

        apiManager.fetchFromCustomUrl(url, new ApiManager.ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                postList.clear();
                postList.addAll(result);
                adapter.setShowImages(showImages);
                adapter.notifyDataSetChanged();
                showLoading(false);
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Ошибка: " + errorMessage);
                Toast.makeText(getContext(), "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Настройки отображения (бонус)
     */
    public void setShowImages(boolean show) {
        this.showImages = show;
        if (adapter != null) {
            adapter.setShowImages(show);
        }
    }

    public void setItemsToShow(int count) {
        this.itemsToShow = count;
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(postList.isEmpty() ? View.GONE : View.VISIBLE);
            tvEmpty.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}