package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Classe représentant une requête de connexion
 */
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Getters et setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 