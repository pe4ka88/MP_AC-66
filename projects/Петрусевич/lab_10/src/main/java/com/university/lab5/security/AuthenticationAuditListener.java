package com.university.lab5.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAuditListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Logger logger = LoggerFactory.getLogger("SECURITY_AUDIT");

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            logger.info("Успешный ВХОД. Пользователь: {}. Источник: {}", 
                event.getAuthentication().getName(),
                event.getAuthentication().getClass().getSimpleName());
        } 
        else if (event.getClass().getSimpleName().contains("LogoutSuccessEvent")) {
            logger.info("Успешный ВЫХОД. Пользователь: {}", event.getAuthentication().getName());
        }
    }
}
