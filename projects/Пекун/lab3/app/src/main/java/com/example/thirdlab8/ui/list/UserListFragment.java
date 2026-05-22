package com.example.thirdlab8.ui.list;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.thirdlab8.R;
import com.example.thirdlab8.adapter.UserAdapter;
import com.example.thirdlab8.databinding.FragmentUserListBinding;
import com.example.thirdlab8.model.User;
import com.example.thirdlab8.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment для отображения списка пользователей
 * С расширенными функциями управления данными
 * 
 * Лабораторная работа №8
 * Пекун Марк Сергеевич
 * Группа АС-66
 */
public class UserListFragment extends Fragment {
    
    private static final String TAG = "UserListFragment";
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_ITEM_LIMIT = "item_limit";
    private static final int ITEMS_PER_PAGE = 10;
    
    private FragmentUserListBinding binding;
    private UserViewModel viewModel;
    private UserAdapter adapter;
    private SharedPreferences prefs;
    private boolean isLoading = false;
    private Handler animationHandler = new Handler(Looper.getMainLooper());
    private ObjectAnimator pulseAnimator;
    
    // Пагинация
    private int currentPage = 1;
    private List<User> allUsers = new ArrayList<>();
    
    // Тип запроса
    private enum RequestType { USERS, POSTS, COMMENTS }
    private RequestType currentRequestType = RequestType.USERS;
    
    // Вид отображения
    private boolean isGridView = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        logCurrentSettings();
        
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        setupMenu();
        setupRecyclerView();
        setupButtons();
        observeViewModel();
    }
    
    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_user_list, menu);
            }
            
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (!isAdded() || binding == null) return false;
                
                int id = menuItem.getItemId();
                if (id == R.id.action_settings) {
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_userList_to_settings);
                    return true;
                } else if (id == R.id.action_share) {
                    shareData();
                    return true;
                } else if (id == R.id.action_about) {
                    showAbout();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    
    private void setupRecyclerView() {
        adapter = new UserAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        
        adapter.setOnUserClickListener(user -> {
            if (!isAdded() || binding == null) return;
            
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_userList_to_userDetail, bundle);
        });
    }
    
    private void setupButtons() {
        // Основные кнопки
        binding.loadButton.setOnClickListener(v -> {
            if (!isLoading && isAdded() && binding != null) {
                loadData();
            }
        });
        
        binding.settingsButton.setOnClickListener(v -> {
            if (isAdded() && binding != null) {
                Navigation.findNavController(v).navigate(R.id.action_userList_to_settings);
            }
        });
        
        // Выбор типа запроса
        binding.requestTypeChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipUsers) {
                currentRequestType = RequestType.USERS;
            } else if (checkedId == R.id.chipPosts) {
                currentRequestType = RequestType.POSTS;
            } else if (checkedId == R.id.chipComments) {
                currentRequestType = RequestType.COMMENTS;
            }
            showSnackbar("Выбран тип запроса: " + getRequestTypeName());
        });
        
        // Вид отображения
        binding.viewListButton.setOnClickListener(v -> switchToListView());
        binding.viewGridButton.setOnClickListener(v -> switchToGridView());
        
        // Пагинация
        binding.prevPageButton.setOnClickListener(v -> previousPage());
        binding.nextPageButton.setOnClickListener(v -> nextPage());
        
        // Экспорт и отправка
        binding.exportJsonButton.setOnClickListener(v -> exportToJson());
        binding.exportCsvButton.setOnClickListener(v -> exportToCsv());
        binding.shareButton.setOnClickListener(v -> shareData());
        
        updatePaginationButtons();
    }
    
    private void observeViewModel() {
        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (!isAdded() || binding == null) return;
            
            if (users != null && !users.isEmpty()) {
                showLoadingStatus("✅ Успешно загружено!");
                
                animationHandler.postDelayed(() -> {
                    if (!isAdded() || binding == null) return;
                    
                    hideLoadingOverlay();
                    allUsers = new ArrayList<>(users);
                    currentPage = 1;
                    updatePageDisplay();
                    binding.emptyView.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    
                    showSnackbar("✅ Загружено: " + users.size() + " элементов");
                }, 800);
            }
        });
        
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if (!isAdded() || binding == null) return;
            
            isLoading = loading != null && loading;
            
            if (isLoading) {
                showLoadingOverlay();
            }
            
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.loadButton.setEnabled(!isLoading);
            binding.settingsButton.setEnabled(!isLoading);
        });
        
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (!isAdded() || binding == null) return;
            
            if (error != null) {
                hideLoadingOverlay();
                handleError(error);
            }
        });
    }
    
    private void handleError(String error) {
        // Обработка различных типов исключений с информативными сообщениями
        String errorMessage;
        String errorIcon;
        
        try {
            if (error.contains("UnknownHost") || error.contains("подключения")) {
                errorIcon = "🌐";
                errorMessage = "Нет подключения к интернету";
            } else if (error.contains("Timeout") || error.contains("время")) {
                errorIcon = "⏱️";
                errorMessage = "Превышено время ожидания";
            } else if (error.contains("404")) {
                errorIcon = "🔍";
                errorMessage = "Ресурс не найден (404)";
            } else if (error.contains("500")) {
                errorIcon = "⚠️";
                errorMessage = "Ошибка сервера (500)";
            } else {
                errorIcon = "❌";
                errorMessage = error;
            }
            
            Snackbar.make(binding.getRoot(), errorIcon + " " + errorMessage, Snackbar.LENGTH_LONG)
                    .setAction("Повторить", v -> loadData())
                    .show();
                    
            Log.e(TAG, "Ошибка загрузки: " + error);
            
        } catch (Exception e) {
            Log.e(TAG, "Ошибка обработки исключения", e);
            showSnackbar("❌ Произошла ошибка");
        }
    }
    
    private void loadData() {
        if (!isAdded() || binding == null) return;
        
        try {
            String savedUrl = prefs.getString(KEY_SERVER_URL, "https://jsonplaceholder.typicode.com/");
            int limit = prefs.getInt(KEY_ITEM_LIMIT, 10);
            
            Log.d(TAG, "════════════════════════════════════");
            Log.d(TAG, "Загрузка данных с настройками:");
            Log.d(TAG, "URL: " + savedUrl);
            Log.d(TAG, "Лимит: " + limit);
            Log.d(TAG, "Тип запроса: " + getRequestTypeName());
            Log.d(TAG, "════════════════════════════════════");
            
            if (savedUrl.isEmpty()) {
                throw new IllegalStateException("URL не настроен");
            }
            
            com.example.thirdlab8.api.RetrofitClient.getInstance().updateBaseUrl(savedUrl);
            
            // В зависимости от типа запроса используем разные методы
            viewModel.loadUsers(limit);
            
        } catch (IllegalStateException e) {
            handleError("⚙️ " + e.getMessage() + ". Перейдите в Настройки");
        } catch (Exception e) {
            handleError("❌ Ошибка инициализации: " + e.getMessage());
            Log.e(TAG, "Ошибка загрузки данных", e);
        }
    }
    
    // ===== ПАГИНАЦИЯ =====
    
    private void updatePageDisplay() {
        if (allUsers.isEmpty()) {
            adapter.setUsers(new ArrayList<>());
            return;
        }
        
        int totalPages = (int) Math.ceil((double) allUsers.size() / ITEMS_PER_PAGE);
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allUsers.size());
        
        List<User> pageUsers = allUsers.subList(startIndex, endIndex);
        adapter.setUsers(pageUsers);
        
        binding.pageInfoText.setText("Стр. " + currentPage + "/" + totalPages);
        updatePaginationButtons();
        
        binding.recyclerView.scrollToPosition(0);
    }
    
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePageDisplay();
            showSnackbar("📄 Страница " + currentPage);
        }
    }
    
    private void nextPage() {
        int totalPages = (int) Math.ceil((double) allUsers.size() / ITEMS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            updatePageDisplay();
            showSnackbar("📄 Страница " + currentPage);
        }
    }
    
    private void updatePaginationButtons() {
        int totalPages = (int) Math.ceil((double) allUsers.size() / ITEMS_PER_PAGE);
        binding.prevPageButton.setEnabled(currentPage > 1);
        binding.nextPageButton.setEnabled(currentPage < totalPages);
    }
    
    // ===== ВИД ОТОБРАЖЕНИЯ =====
    
    private void switchToListView() {
        if (!isGridView) return;
        
        isGridView = false;
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        binding.viewListButton.setBackgroundColor(getResources().getColor(R.color.tbank_yellow, null));
        binding.viewGridButton.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        
        showSnackbar("📱 Режим списка");
    }
    
    private void switchToGridView() {
        if (isGridView) return;
        
        isGridView = true;
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        binding.viewGridButton.setBackgroundColor(getResources().getColor(R.color.tbank_yellow, null));
        binding.viewListButton.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        
        showSnackbar("🎨 Режим сетки");
    }
    
    // ===== ЭКСПОРТ ДАННЫХ =====
    
    private void exportToJson() {
        if (allUsers.isEmpty()) {
            showSnackbar("❌ Нет данных для экспорта");
            return;
        }
        
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "users_data_" + System.currentTimeMillis() + ".json");
            
            Gson gson = new Gson();
            String jsonData = gson.toJson(allUsers);
            
            FileWriter writer = new FileWriter(file);
            writer.write(jsonData);
            writer.close();
            
            showSnackbar("✅ JSON сохранён: " + file.getName());
            Log.d(TAG, "JSON файл сохранён: " + file.getAbsolutePath());
            
            // Предложить открыть или поделиться
            offerShareFile(file, "application/json");
            
        } catch (IOException e) {
            handleError("❌ Ошибка записи JSON: " + e.getMessage());
            Log.e(TAG, "Ошибка экспорта JSON", e);
        } catch (Exception e) {
            handleError("❌ Неожиданная ошибка: " + e.getMessage());
            Log.e(TAG, "Неожиданная ошибка при экспорте", e);
        }
    }
    
    private void exportToCsv() {
        if (allUsers.isEmpty()) {
            showSnackbar("❌ Нет данных для экспорта");
            return;
        }
        
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "users_data_" + System.currentTimeMillis() + ".csv");
            
            FileWriter writer = new FileWriter(file);
            
            // Заголовки CSV
            writer.append("ID,Name,Email,Phone,Company,Address\n");
            
            // Данные
            for (User user : allUsers) {
                writer.append(String.valueOf(user.getId())).append(",");
                writer.append(escapeCsv(user.getName())).append(",");
                writer.append(escapeCsv(user.getEmail())).append(",");
                writer.append(escapeCsv(user.getPhone())).append(",");
                writer.append(escapeCsv(user.getCompany())).append(",");
                writer.append(escapeCsv(user.getAddress())).append("\n");
            }
            
            writer.close();
            
            showSnackbar("✅ CSV сохранён: " + file.getName());
            Log.d(TAG, "CSV файл сохранён: " + file.getAbsolutePath());
            
            offerShareFile(file, "text/csv");
            
        } catch (IOException e) {
            handleError("❌ Ошибка записи CSV: " + e.getMessage());
            Log.e(TAG, "Ошибка экспорта CSV", e);
        } catch (Exception e) {
            handleError("❌ Неожиданная ошибка: " + e.getMessage());
            Log.e(TAG, "Неожиданная ошибка при экспорте", e);
        }
    }
    
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private void offerShareFile(File file, String mimeType) {
        try {
            android.net.Uri fileUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
            );
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(mimeType);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            startActivity(Intent.createChooser(shareIntent, "Поделиться файлом"));
            
        } catch (Exception e) {
            Log.e(TAG, "Ошибка отправки файла", e);
        }
    }
    
    // ===== ОТПРАВКА ДАННЫХ =====
    
    private void shareData() {
        if (allUsers.isEmpty()) {
            showSnackbar("❌ Нет данных для отправки");
            return;
        }
        
        try {
            Gson gson = new Gson();
            String jsonData = gson.toJson(allUsers);
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Данные пользователей - Lab8");
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                "Данные пользователей:\n\n" + 
                "Всего элементов: " + allUsers.size() + "\n\n" +
                jsonData);
            
            startActivity(Intent.createChooser(shareIntent, "Отправить через"));
            
            Log.d(TAG, "Данные отправлены через Intent");
            
        } catch (Exception e) {
            handleError("❌ Ошибка отправки: " + e.getMessage());
            Log.e(TAG, "Ошибка отправки данных", e);
        }
    }
    
    private void showAbout() {
        if (!isAdded() || binding == null) return;
        
        Bundle bundle = new Bundle();
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_userList_to_about, bundle);
    }
    
    // ===== АНИМАЦИЯ ЗАГРУЗКИ =====
    
    private void showLoadingOverlay() {
        if (!isAdded() || binding == null) return;
        
        binding.loadingOverlay.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);
        
        pulseAnimator = ObjectAnimator.ofFloat(binding.loadingIcon, "alpha", 1.0f, 0.3f, 1.0f);
        pulseAnimator.setDuration(1500);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimator.start();
        
        animateLoadingStatus();
    }
    
    private void hideLoadingOverlay() {
        if (!isAdded() || binding == null) return;
        
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
        }
        
        animationHandler.removeCallbacksAndMessages(null);
        binding.loadingOverlay.setVisibility(View.GONE);
    }
    
    private void animateLoadingStatus() {
        if (!isAdded() || binding == null) return;
        
        final String[] statuses = {
            "🔍 Поиск по сети...",
            "🌐 Установка соединения...",
            "📡 Отправка запроса...",
            "⏳ Получение данных...",
            "📦 Обработка ответа..."
        };
        
        final int[] currentIndex = {0};
        
        Runnable updateStatus = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || binding == null || !isLoading) return;
                
                binding.loadingStatus.setText(statuses[currentIndex[0]]);
                currentIndex[0] = (currentIndex[0] + 1) % statuses.length;
                
                animationHandler.postDelayed(this, 600);
            }
        };
        
        animationHandler.post(updateStatus);
    }
    
    private void showLoadingStatus(String status) {
        if (!isAdded() || binding == null) return;
        
        binding.loadingTitle.setText(status);
        binding.loadingStatus.setText("Завершение...");
    }
    
    // ===== УТИЛИТЫ =====
    
    private void logCurrentSettings() {
        String url = prefs.getString(KEY_SERVER_URL, "НЕТ");
        int limit = prefs.getInt(KEY_ITEM_LIMIT, -1);
        Log.d(TAG, "════════════════════════════════════");
        Log.d(TAG, "Текущие настройки в SharedPreferences:");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Лимит: " + limit);
        Log.d(TAG, "════════════════════════════════════");
    }
    
    private String getRequestTypeName() {
        switch (currentRequestType) {
            case USERS: return "Пользователи";
            case POSTS: return "Посты";
            case COMMENTS: return "Комментарии";
            default: return "Неизвестно";
        }
    }
    
    private void showSnackbar(String message) {
        if (isAdded() && binding != null) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        logCurrentSettings();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
        }
        animationHandler.removeCallbacksAndMessages(null);
        
        binding = null;
    }
}
