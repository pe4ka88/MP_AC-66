package com.example.file.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.button.MaterialButton;
import com.example.file.R;

public class HomeFragment extends Fragment {

    private ActivityResultLauncher<Intent> filePickerLauncher;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Регистрируем launcher для выбора файла
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        handleFile(uri);
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnSelect = view.findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private void handleFile(Uri uri) {
        if (uri == null) return;

        String type = requireContext().getContentResolver().getType(uri);
        if (type == null) {
            Toast.makeText(getContext(), "Не удалось определить тип файла", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("fileUri", uri.toString());

        // Получаем NavController через NavHostFragment
        NavController nav = NavHostFragment.findNavController(this);

        if (type.startsWith("image")) {
            nav.navigate(R.id.action_home_to_imageFragment, bundle);

        } else if (type.startsWith("video")) {
            nav.navigate(R.id.action_home_to_videoFragment, bundle);

        } else if (type.startsWith("audio")) {
            nav.navigate(R.id.action_home_to_audioFragment, bundle);

        } else {
            Toast.makeText(getContext(), "Неподдерживаемый тип файла", Toast.LENGTH_SHORT).show();
        }
    }
}