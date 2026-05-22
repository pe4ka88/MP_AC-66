package com.example.a3lab.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.a3lab.R;
import com.example.a3lab.utils.SharedPrefsManager;

public class SettingsFragment extends Fragment {
    private EditText serverUrlInput;
    private Spinner endpointSpinner;
    private Button saveButton;
    private SharedPrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        serverUrlInput = view.findViewById(R.id.serverUrlInput);
        endpointSpinner = view.findViewById(R.id.endpointSpinner);
        saveButton = view.findViewById(R.id.saveSettingsButton);
        prefsManager = new SharedPrefsManager(requireContext());

        // Установка текста из resources
        saveButton.setText(R.string.save_settings);
        serverUrlInput.setHint(R.string.server_base_url);

        // Массив для отображения в Spinner
        String[] endpoints = {
                getString(R.string.endpoint_posts),
                getString(R.string.endpoint_products),
                getString(R.string.endpoint_todos),
                getString(R.string.endpoint_comments)
        };

        // Реальные значения для API
        String[] endpointValues = {"/posts", "/products", "/todos", "/comments"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, endpoints);
        endpointSpinner.setAdapter(adapter);

        // Загрузка сохраненных настроек
        serverUrlInput.setText(prefsManager.getServerUrl());
        String currentEndpoint = prefsManager.getSelectedEndpoint();
        int position = 0;
        for (int i = 0; i < endpointValues.length; i++) {
            if (endpointValues[i].equals(currentEndpoint)) {
                position = i;
                break;
            }
        }
        endpointSpinner.setSelection(position);

        saveButton.setOnClickListener(v -> {
            String newUrl = serverUrlInput.getText().toString().trim();
            if (newUrl.isEmpty()) {
                Toast.makeText(getContext(), R.string.enter_server_url, Toast.LENGTH_SHORT).show();
                return;
            }
            String newEndpoint = endpointValues[endpointSpinner.getSelectedItemPosition()];
            prefsManager.saveSettings(newUrl, newEndpoint);
            Toast.makeText(getContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}