package com.example.note.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.note.R;
import com.example.note.api.ApiClient;
import com.example.note.api.EmbeddingRequest;
import com.example.note.api.HuggingFaceApi;
import com.example.note.db.NotesDBHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAdd extends Fragment {

    private EditText etTitle;
    private EditText etDescription;
    private Button btnAdd;

    public FragmentAdd() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_add, container, false);

        etTitle = view.findViewById(R.id.editTextTitle);
        etDescription = view.findViewById(R.id.editTextDescription);
        btnAdd = view.findViewById(R.id.buttonAdd);

        btnAdd.setOnClickListener(v -> addNote());

        return view;
    }

    private void addNote() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(),
                    "Введите заголовок", Toast.LENGTH_SHORT).show();
            return;
        }

        NotesDBHelper db = new NotesDBHelper(requireContext());

        String fullText;

        if (description.isEmpty()) {
            fullText = title;
        } else {
            fullText = title + ". " + description;
        }

        HuggingFaceApi api = ApiClient.getApi();
        EmbeddingRequest request = new EmbeddingRequest(fullText);

        btnAdd.setEnabled(false); // защита от двойного нажатия

        api.getEmbedding(
                "Bearer " + com.example.note.BuildConfig.HF_TOKEN,
                request
        ).enqueue(new Callback<List<Float>>() {

            @Override
            public void onResponse(Call<List<Float>> call,
                                   Response<List<Float>> response) {

                btnAdd.setEnabled(true);

                if (!isAdded()) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().isEmpty()) {

                    List<Float> embedding = response.body();

                    db.addNoteWithEmbedding(title, description, embedding);

                    Toast.makeText(getContext(),
                            "Заметка добавлена", Toast.LENGTH_SHORT).show();

                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();

                } else {

                    Toast.makeText(getContext(),
                            "Ошибка при создании embedding",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Float>> call, Throwable t) {

                btnAdd.setEnabled(true);

                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Ошибка сети: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}