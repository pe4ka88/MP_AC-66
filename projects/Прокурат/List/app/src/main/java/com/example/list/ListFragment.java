package com.example.list;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private ItemAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    private ProgressBar progressBar;
    private Button btnLoad;
    private DataLoader dataLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        btnLoad = view.findViewById(R.id.btn_load);

        styleButtonText(btnLoad);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ItemAdapter(itemList, item -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showDetailFragment(item);
            }
        });
        recyclerView.setAdapter(adapter);

        dataLoader = new DataLoader(requireContext());

        btnLoad.setOnClickListener(v -> loadData());

        return view;
    }

    private void styleButtonText(Button button) {
        String fullText = button.getText().toString();
        int newlineIndex = fullText.indexOf('\n');
        if (newlineIndex != -1) {
            SpannableString spannable = new SpannableString(fullText);
            spannable.setSpan(new AbsoluteSizeSpan(15, true), newlineIndex + 1, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#B0B0B0")), newlineIndex + 1, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            button.setText(spannable);
        }
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        btnLoad.setEnabled(false);

        String url = "https://jsonplaceholder.org/posts";

        dataLoader.loadData(url, new DataLoader.DataLoadListener() {
            @Override
            public void onSuccess(List<Item> itemList) {
                progressBar.setVisibility(View.GONE);
                btnLoad.setEnabled(true);
                ListFragment.this.itemList.clear();
                ListFragment.this.itemList.addAll(itemList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnLoad.setEnabled(true);
                String userMessage;
                if (error == null || error.isEmpty()) {
                    userMessage = "Неизвестная ошибка. Проверьте подключение к интернету.";
                } else {
                    userMessage = "Ошибка: " + error;
                }
                Toast.makeText(getActivity(), userMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}