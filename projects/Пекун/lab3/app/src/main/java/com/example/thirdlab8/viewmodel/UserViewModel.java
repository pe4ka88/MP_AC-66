package com.example.thirdlab8.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.thirdlab8.model.User;
import com.example.thirdlab8.repository.UserRepository;
import java.util.List;

/**
 * ViewModel для управления данными списка пользователей
 * Обеспечивает связь между UI и данными
 * 
 * Лабораторная работа №8
 * Пекун Марк Сергеевич
 * Группа АС-66
 */
public class UserViewModel extends ViewModel {
    
    private final UserRepository repository;
    private final MutableLiveData<List<User>> usersLiveData;
    private final MutableLiveData<Boolean> loadingLiveData;
    private final MutableLiveData<String> errorLiveData;
    
    public UserViewModel() {
        repository = new UserRepository();
        usersLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    
    /**
     * Загрузка пользователей
     * @param limit количество элементов для загрузки
     */
    public void loadUsers(int limit) {
        loadingLiveData.postValue(true);
        errorLiveData.postValue(null);
        
        repository.loadUsers(limit, new UserRepository.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                loadingLiveData.postValue(false);
                if (data != null && !data.isEmpty()) {
                    usersLiveData.postValue(data);
                } else {
                    errorLiveData.postValue("Пустой ответ от сервера");
                }
            }
            
            @Override
            public void onError(String message) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue(message);
            }
        });
    }
    
    // Геттеры для LiveData
    public LiveData<List<User>> getUsers() {
        return usersLiveData;
    }
    
    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Освобождение ресурсов
        repository.cancelRequests();
    }
}
