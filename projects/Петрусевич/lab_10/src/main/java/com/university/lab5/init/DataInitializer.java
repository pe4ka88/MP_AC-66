package com.university.lab5.init;

import com.university.lab5.model.Role;
import com.university.lab5.model.User;
import com.university.lab5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin_local");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@local.com");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setRegistrationDate(LocalDateTime.now());
            userRepository.save(admin);

            User user = new User();
            user.setUsername("user_local");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setEmail("user@local.com");
            user.setRole(Role.ROLE_USER);
            user.setRegistrationDate(LocalDateTime.now());
            userRepository.save(user);
            
            System.out.println("Тестовые пользователи созданы (admin_local / user_local)");
        }
    }
}