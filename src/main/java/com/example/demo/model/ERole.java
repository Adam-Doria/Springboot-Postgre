package com.example.demo.model;

/**
 * Énumération des rôles disponibles dans l'application
 */
public enum ERole {
    ROLE_USER,       // Utilisateur standard
    ROLE_MODERATOR,  // Modérateur (peut modérer les messages dans les salons)
    ROLE_ADMIN       // Administrateur (accès complet à l'application)
} 