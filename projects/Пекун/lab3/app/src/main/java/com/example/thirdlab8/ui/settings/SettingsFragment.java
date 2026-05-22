package com.example.thirdlab8.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.thirdlab8.R;
import com.example.thirdlab8.api.RetrofitClient;
import com.example.thirdlab8.databinding.FragmentSettingsBinding;
import com.google.android.material.snackbar.Snackbar;

/**
 * Fragment настроек приложения
 * ИСПРАВЛЕНО: SharedPreferences теперь сохраняется корректно с commit()
 * 
 * Лабораторная работа №8
 * Пекун Марк Сергеевич
 * Группа АС-66
 */
public class SettingsFragment extends Fragment {
    
    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_ITEM_LIMIT = "item_limit";
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // КРИТИЧЕСКИ ВАЖНО: Используем тот же PREFS_NAME что и везде
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        Log.d(TAG, "SettingsFragment создан. Путь к prefs: " + PREFS_NAME);
        
        setupUI();
        loadSettings();
        
        binding.saveButton.setOnClickListener(v -> saveSettings());
        binding.backButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }
    
    private void setupUI() {
        // Настройка Spinner для выбора количества элементов
        String[] limits = {"5", "10", "20", "30", "50", "100"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, limits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.limitSpinner.setAdapter(adapter);
    }
    
    private void loadSettings() {
        Log.d(TAG, "════════════════════════════════════");
        Log.d(TAG, "Загрузка настроек из SharedPreferences");
        
        // Загрузка URL
        String savedUrl = prefs.getString(KEY_SERVER_URL, "https://jsonplaceholder.typicode.com/");
        int savedLimit = prefs.getInt(KEY_ITEM_LIMIT, 10);
        
        Log.d(TAG, "Загружено из prefs:");
        Log.d(TAG, "URL: " + savedUrl);
        Log.d(TAG, "Лимит: " + savedLimit);
        
        // Убираем последний / для отображения
        if (savedUrl.endsWith("/")) {
            savedUrl = savedUrl.substring(0, savedUrl.length() - 1);
        }
        
        // Убираем протокол для удобства редактирования
        if (savedUrl.startsWith("https://")) {
            savedUrl = savedUrl.substring(8);
        } else if (savedUrl.startsWith("http://")) {
            savedUrl = savedUrl.substring(7);
        }
        
        binding.urlEditText.setText(savedUrl);
        
        // Установка выбранного лимита в Spinner
        String limitStr = String.valueOf(savedLimit);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.limitSpinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(limitStr);
            if (position >= 0) {
                binding.limitSpinner.setSelection(position);
            }
        }
        
        Log.d(TAG, "════════════════════════════════════");
        
        Snackbar.make(binding.getRoot(), 
                "📋 Загружены: " + limitStr + " элементов", 
                Snackbar.LENGTH_SHORT).show();
    }
    
    private void saveSettings() {
        if (binding == null) return;
        
        String url = binding.urlEditText.getText().toString().trim();
        
        if (url.isEmpty()) {
            Snackbar.make(binding.getRoot(), "❌ Введите URL сервера", Snackbar.LENGTH_SHORT).show();
            return;
        }
        
        // Добавляем протокол если нет
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        
        // Добавляем / в конце если нет
        if (!url.endsWith("/")) {
            url += "/";
        }
        
        int limit = Integer.parseInt(binding.limitSpinner.getSelectedItem().toString());
        
        Log.d(TAG, "════════════════════════════════════");
        Log.d(TAG, "Сохранение настроек:");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Лимит: " + limit);
        
        // КРИТИЧЕСКИ ВАЖНО: commit() для НЕМЕДЛЕННОГО сохранения
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_URL, url);
        editor.putInt(KEY_ITEM_LIMIT, limit);
        boolean saved = editor.commit(); // СИНХРОННОЕ сохранение!
        
        Log.d(TAG, "Результат commit(): " + saved);
        
        if (saved) {
            // Немедленное обновление Retrofit
            RetrofitClient.getInstance().updateBaseUrl(url);
            
            // Проверка сохранения
            String verifyUrl = prefs.getString(KEY_SERVER_URL, "ОШИБКА");
            int verifyLimit = prefs.getInt(KEY_ITEM_LIMIT, -999);
            
            Log.d(TAG, "Проверка после сохранения:");
            Log.d(TAG, "URL в prefs: " + verifyUrl);
            Log.d(TAG, "Лимит в prefs: " + verifyLimit);
            Log.d(TAG, "════════════════════════════════════");
            
            Snackbar.make(binding.getRoot(), 
                    "✅ Настройки сохранены!\n" + 
                    "Сервер: " + url + "\n" +
                    "Количество: " + limit, 
                    Snackbar.LENGTH_LONG).show();
            
            // Возврат через 1.5 секунды
            binding.getRoot().postDelayed(() -> {
                if (isAdded()) {
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                }
            }, 1500);
        } else {
            Log.e(TAG, "ОШИБКА: commit() вернул false!");
            Log.d(TAG, "════════════════════════════════════");
            
            Snackbar.make(binding.getRoot(), 
                    "❌ Ошибка сохранения настроек", 
                    Snackbar.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

