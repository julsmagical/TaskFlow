package com.springboot.taskflow.taskflow.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthHelper {

    private AuthHelper() {}

    public static UUID getCurrentUserId(Authentication authentication) {
        return (UUID) authentication.getPrincipal();
    }

    public static String getCurrentRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .map(auth -> auth.replace("ROLE_", ""))
            .orElseThrow(() -> new IllegalStateException("El usuario no tiene un rol asignado."));
    }
}