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

public class FragmentUpdate extends Fragment {

    private EditText etTitle, etDescription;
    private Button btnUpdate;

    private int noteId;

    public FragmentUpdate() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_update, container, false);

        etTitle = view.findViewById(R.id.editTextTitle);
        etDescription = view.findViewById(R.id.editTextDescription);
        btnUpdate = view.findViewById(R.id.buttonUpdate);

        if (getArguments() != null) {
            noteId = getArguments().getInt("id");
            etTitle.setText(getArguments().getString("title"));
            etDescription.setText(getArguments().getString("description"));
        }

        btnUpdate.setOnClickListener(v -> updateNote());

        return view;
    }

    private void updateNote() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(),
                    "Введите заголовок",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        NotesDBHelper db = new NotesDBHelper(requireContext());

        // ВАЖНО: такой же формат как в FragmentAdd
        String fullText = title + ". " + description;

        HuggingFaceApi api = ApiClient.getApi();
        EmbeddingRequest request = new EmbeddingRequest(fullText);

        btnUpdate.setEnabled(false);

        api.getEmbedding(
                "Bearer " + com.example.note.BuildConfig.HF_TOKEN,
                request
        ).enqueue(new Callback<List<Float>>() {

            @Override
            public void onResponse(Call<List<Float>> call,
                                   Response<List<Float>> response) {

                btnUpdate.setEnabled(true);

                if (!isAdded()) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().isEmpty()) {

                    List<Float> embedding = response.body();

                    // Обновляем заметку вместе с новым embedding
                    db.updateNoteWithEmbedding(noteId, title, description, embedding);

                    Toast.makeText(getContext(),
                            "Заметка обновлена",
                            Toast.LENGTH_SHORT).show();

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

                btnUpdate.setEnabled(true);

                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Ошибка сети: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}