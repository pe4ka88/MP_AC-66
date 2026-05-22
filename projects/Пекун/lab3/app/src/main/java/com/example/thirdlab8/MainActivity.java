package com.example.thirdlab8;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.thirdlab8.api.RetrofitClient;
import com.example.thirdlab8.databinding.ActivityMainBinding;

/**
 * Главная активность приложения
 * Использует Navigation Component для управления фрагментами
 * 
 * ========================================
 * Лабораторная работа №8
 * Пекун Марк Сергеевич
 * Группа АС-66
 * ========================================
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // КРИТИЧЕСКИ ВАЖНО: Инициализация Retrofit с сохранёнными настройками  
        RetrofitClient.getInstance().initFromPreferences(this);
        
        // Включение ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Настройка Navigation Component
        setupNavigation();
    }
    
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}