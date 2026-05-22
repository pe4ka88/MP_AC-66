package com.example.myapplication3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button loadButton, nextButton, prevButton;
    private Spinner imageSourceSpinner;
    private EditText limitInput;

    private int page = 0;
    private int limit = 20;

    private List<Photo> allPhotos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        loadButton = root.findViewById(R.id.loadButton);
        nextButton = root.findViewById(R.id.nextButton);
        prevButton = root.findViewById(R.id.prevButton);
        imageSourceSpinner = root.findViewById(R.id.imageSourceSpinner);
        limitInput = root.findViewById(R.id.limitInput);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"picsum.photos", "dummyimage.com", "placehold.co"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSourceSpinner.setAdapter(spinnerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadButton.setOnClickListener(v -> loadData());

        nextButton.setOnClickListener(v -> {
            page++;
            showPage();
        });

        prevButton.setOnClickListener(v -> {
            if (page > 0) page--;
            showPage();
        });

        return root;
    }

    private void loadData() {
        if (!TextUtils.isEmpty(limitInput.getText().toString())) {
            limit = Integer.parseInt(limitInput.getText().toString());
        }

        // устанавливаем источник картинок
        switch (imageSourceSpinner.getSelectedItemPosition()) {
            case 1: Photo.imageSource = "dummyimage"; break;
            case 2: Photo.imageSource = "placeholder"; break;
            default: Photo.imageSource = "picsum"; break;
        }

        ApiService api = ApiClient.getClient(null);
        api.getPhotos("https://jsonplaceholder.typicode.com/photos").enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                Toast.makeText(getContext(),
                        "Код: " + response.code() + " | Тело: " + (response.body() == null ? "null" : response.body().size() + " шт"),
                        Toast.LENGTH_LONG).show();

                if (response.isSuccessful() && response.body() != null) {
                    allPhotos = response.body();
                    page = 0;
                    showPage();
                } else {
                    Toast.makeText(getContext(), "Пустой ответ от сервера", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPage() {
        if (allPhotos == null) return;

        int start = page * limit;
        int end = Math.min(start + limit, allPhotos.size());

        if (start >= allPhotos.size()) {
            Toast.makeText(getContext(), "Нет больше данных", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Photo> subList = allPhotos.subList(start, end);

        recyclerView.setAdapter(new PostAdapter(subList, photo -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).openDetail(photo);
            }
        }));
    }
}