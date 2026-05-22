package com.example.thirdlab8.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.thirdlab8.R;
import com.example.thirdlab8.databinding.FragmentSplashBinding;

/**
 * Splash экран с авторством
 */
public class SplashFragment extends Fragment {
    
    private FragmentSplashBinding binding;
    private static final int SPLASH_DELAY = 3000; // 3 секунды
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Автоматический переход на главный экран
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && binding != null) {
                Navigation.findNavController(view).navigate(R.id.action_splash_to_userList);
            }
        }, SPLASH_DELAY);
        
        // Возможность пропустить Splash
        binding.getRoot().setOnClickListener(v -> {
            if (isAdded()) {
                Navigation.findNavController(v).navigate(R.id.action_splash_to_userList);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

