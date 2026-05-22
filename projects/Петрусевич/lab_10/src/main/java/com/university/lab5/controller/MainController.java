package com.university.lab5.controller;

import com.university.lab5.model.Role;
import com.university.lab5.model.User;
import com.university.lab5.repository.UserRepository;
import com.university.lab5.service.KeycloakProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.keycloak.representations.idm.UserRepresentation;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakProfileService keycloakProfileService;

    @GetMapping("/")
    public String index() {
        return "redirect:/profile";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerProcess(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password) {
        if(userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=exists";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.ROLE_USER);
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);

        return "redirect:/login?registered";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        boolean isKeycloak = authentication instanceof OAuth2AuthenticationToken;
        model.addAttribute("isKeycloak", isKeycloak);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        if (isKeycloak) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String username = token.getPrincipal().getAttribute("preferred_username");
            model.addAttribute("username", username);
            
            UserRepresentation kcUser = keycloakProfileService.getUserInfo(username);
            if (kcUser != null) {
                model.addAttribute("email", kcUser.getEmail());
                model.addAttribute("firstName", kcUser.getFirstName());
                model.addAttribute("lastName", kcUser.getLastName());
            } else {
                model.addAttribute("email", token.getPrincipal().getAttribute("email"));
                model.addAttribute("firstName", token.getPrincipal().getAttribute("given_name"));
                model.addAttribute("lastName", token.getPrincipal().getAttribute("family_name"));
            }
            model.addAttribute("registrationDate", "Управляется Keycloak");
            
            String avatarUrl = keycloakProfileService.getAvatarUrl(username);
            model.addAttribute("avatarUrl", avatarUrl);
        } else {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("email", user.getEmail());
                
                String dateStr = user.getRegistrationDate() != null ? user.getRegistrationDate().format(formatter) : "Нет данных";
                model.addAttribute("registrationDate", dateStr);
                
                model.addAttribute("avatarUrl", user.getAvatarUrl());
            }
        }
        return "profile";
    }

    @PostMapping("/profile/update-password")
    public String updateLocalPassword(Authentication authentication, 
                                      @RequestParam String oldPassword,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                    return "redirect:/profile?error=wrong_old_password";
                }
                if (!newPassword.equals(confirmPassword)) {
                    return "redirect:/profile?error=password_mismatch";
                }
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }
        }
        return "redirect:/profile?success";
    }

    @PostMapping("/profile/update-keycloak")
    public String updateKeycloakProfile(Authentication authentication, 
                                        @RequestParam String firstName, 
                                        @RequestParam String lastName,
                                        @RequestParam String email) {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            String username = token.getPrincipal().getAttribute("preferred_username");
            keycloakProfileService.updateUserNameAndEmail(username, firstName, lastName, email);
        }
        return "redirect:/profile?success";
    }

    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(Authentication authentication, @RequestParam("avatar") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            boolean isKeycloak = authentication instanceof OAuth2AuthenticationToken;
            String username = authentication.getName();
            if (isKeycloak) {
                username = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("preferred_username");
            }

            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) uploadDir.mkdir();

            String extension = "";
            int extIndex = file.getOriginalFilename().lastIndexOf(".");
            if(extIndex > 0) extension = file.getOriginalFilename().substring(extIndex);
            
            String filename = username + "_" + System.currentTimeMillis() + extension;
            Path path = Paths.get("uploads", filename);
            Files.write(path, file.getBytes());

            String avatarUrl = "/uploads/" + filename;

            if (isKeycloak) {
                keycloakProfileService.updateAvatarUrl(username, avatarUrl);
            } else {
                User user = userRepository.findByUsername(username).orElse(null);
                if(user != null) {
                    user.setAvatarUrl(avatarUrl);
                    userRepository.save(user);
                }
            }
        }
        return "redirect:/profile?success";
    }
}