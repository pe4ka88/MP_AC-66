package com.example.lab4_10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    private EditText etDescription;
    private TextView tvQuoteStatus;
    private NotesDbHelper dbHelper;
    private final QuoteApiService quoteApiService = new QuoteApiService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        dbHelper = new NotesDbHelper(requireContext());
        etDescription = view.findViewById(R.id.etDescription);
        tvQuoteStatus = view.findViewById(R.id.tvQuoteStatus);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        Button btnGetQuote = view.findViewById(R.id.btnGetQuote);

        // Кнопка добавления заметки
        btnAdd.setOnClickListener(v -> {
            String desc = etDescription.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(requireContext(), "Введите описание", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.addNote(desc);
            etDescription.setText("");
            tvQuoteStatus.setText("");
            Toast.makeText(requireContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
        });

        // Кнопка получения случайной цитаты через ZenQuotes API
        btnGetQuote.setOnClickListener(v -> {
            btnGetQuote.setEnabled(false);
            tvQuoteStatus.setText("Загрузка цитаты...");
            quoteApiService.fetchRandomQuote(new QuoteApiService.QuoteCallback() {
                @Override
                public void onSuccess(String quote, String author) {
                    if (!isAdded()) return;
                    String formatted = "\"" + quote + "\"\n— " + author;
                    etDescription.setText(formatted);
                    tvQuoteStatus.setText("Цитата загружена из ZenQuotes API");
                    btnGetQuote.setEnabled(true);
                }

                @Override
                public void onError(String errorMessage) {
                    if (!isAdded()) return;
                    tvQuoteStatus.setText(errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    btnGetQuote.setEnabled(true);
                }
            });
        });

        return view;
    }
}

