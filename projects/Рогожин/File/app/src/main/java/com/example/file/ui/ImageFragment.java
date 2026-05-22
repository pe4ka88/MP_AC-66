package com.example.file.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.file.R;

public class ImageFragment extends Fragment {

    private ImageView imageView;

    public ImageFragment() {
        super(R.layout.fragment_image);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.homeFragment) {
                    navController.popBackStack(); // возвращаемся к HomeFragment
                } else {
                    // Если уже на HomeFragment — обычный back
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageView);

        String fileUri = getArguments() != null ? getArguments().getString("fileUri") : null;
        if (fileUri == null) return;

        Uri uri = Uri.parse(fileUri);
        imageView.setImageURI(uri);

        // При желании можно добавить клик для увеличения
        imageView.setOnClickListener(v -> {
            // TODO: Можно добавить zoom/pinch или fullscreen
        });
    }
}