package com.university.lab5.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakProfileService {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    private Keycloak getKeycloakClient() {
        String serverUrl = issuerUri.replace("/realms/lab-realm", "");
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("lab-realm")
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public UserRepresentation getUserInfo(String username) {
        try {
            Keycloak keycloak = getKeycloakClient();
            List<UserRepresentation> users = keycloak.realm("lab-realm").users().search(username);
            if (!users.isEmpty()) {
                // Return full representation by ID to ensure attributes are loaded
                return keycloak.realm("lab-realm").users().get(users.get(0).getId()).toRepresentation();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении профиля Keycloak: " + e.getMessage());
        }
        return null;
    }

    public void updateUserNameAndEmail(String username, String firstName, String lastName, String email) {
        try {
            Keycloak keycloak = getKeycloakClient();
            List<UserRepresentation> users = keycloak.realm("lab-realm").users().search(username);
            
            if (!users.isEmpty()) {
                String userId = users.get(0).getId();
                UserRepresentation user = keycloak.realm("lab-realm").users().get(userId).toRepresentation();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                keycloak.realm("lab-realm").users().get(userId).update(user);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении профиля Keycloak: " + e.getMessage());
        }
    }

    public void updateAvatarUrl(String username, String avatarUrl) {
        try {
            Keycloak keycloak = getKeycloakClient();
            List<UserRepresentation> users = keycloak.realm("lab-realm").users().search(username);

            if (!users.isEmpty()) {
                String userId = users.get(0).getId();
                UserRepresentation user = keycloak.realm("lab-realm").users().get(userId).toRepresentation();
                user.singleAttribute("avatarUrl", avatarUrl);
                keycloak.realm("lab-realm").users().get(userId).update(user);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении аватара Keycloak: " + e.getMessage());
        }
    }

    public String getAvatarUrl(String username) {
        try {
            Keycloak keycloak = getKeycloakClient();
            List<UserRepresentation> users = keycloak.realm("lab-realm").users().search(username);
            if(!users.isEmpty()) {
                UserRepresentation fullUser = keycloak.realm("lab-realm").users().get(users.get(0).getId()).toRepresentation();
                if (fullUser.getAttributes() != null && fullUser.getAttributes().containsKey("avatarUrl")) {
                    return fullUser.getAttributes().get("avatarUrl").get(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении аватара Keycloak: " + e.getMessage());
        }
        return null;
    }
}