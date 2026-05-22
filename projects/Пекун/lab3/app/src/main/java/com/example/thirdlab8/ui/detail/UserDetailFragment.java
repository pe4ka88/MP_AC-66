package com.example.thirdlab8.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.thirdlab8.R;
import com.example.thirdlab8.databinding.FragmentUserDetailBinding;
import com.example.thirdlab8.model.User;

/**
 * Fragment для отображения детальной информации о пользователе
 */
public class UserDetailFragment extends Fragment {
    
    private FragmentUserDetailBinding binding;
    private User user;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Получение данных из аргументов (Bundle)
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            displayUserDetails();
        }
        
        // Кнопка "Назад"
        binding.backButton.setOnClickListener(v -> 
            Navigation.findNavController(view).navigateUp()
        );
    }
    
    private void displayUserDetails() {
        if (user == null) return;
        
        // Заполнение полей данными
        binding.nameTextView.setText(user.getName());
        binding.emailTextView.setText(user.getEmail());
        binding.phoneTextView.setText(user.getPhone() != null ? user.getPhone() : "Не указано");
        binding.companyTextView.setText(user.getCompany() != null ? user.getCompany() : "Не указано");
        binding.websiteTextView.setText(user.getWebsite() != null ? user.getWebsite() : "Не указано");
        binding.addressTextView.setText(user.getAddress() != null ? user.getAddress() : "Не указано");
        
        // Загрузка аватара
        String avatarUrl = user.getAvatar();
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            avatarUrl = "https://i.pravatar.cc/300?img=" + user.getId();
        }
        
        Glide.with(this)
                .load(avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.avatarImageView);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
